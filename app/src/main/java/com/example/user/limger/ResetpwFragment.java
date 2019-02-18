package com.example.user.limger;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResetpwFragment extends Fragment {
    Activity mActivity;
    AppCompatActivity mAppCompatActivity;
    private SharedPreferences sp;

    FirebaseAuth auth;
    DatabaseReference member_dataRef;

    ActionBar actionBar;
    ProgressDialog dialog;
    TextInputEditText et_resetpw, et_resetpw2, et_oldpw;

    MenuInflater inflater;
    String m_pw;

    public ResetpwFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dialog = new ProgressDialog(getContext());

        ((membermodify) getActivity()).resetActionBar(true,
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = getActivity();

    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.resetpw, menu);
    }

    //按下修改密碼
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean flag = false;
        if (id == R.id.Reset_pw) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                if (et_resetpw.getText().toString().length() < 6 && et_resetpw.getText().toString().length() >= 0) {
                    Toast.makeText(getContext(), "新密碼請輸入6位數以上", Toast.LENGTH_SHORT).show();
                } else {
                    if (et_resetpw.getText().toString().equals(et_resetpw2.getText().toString())) {
                        if (et_oldpw.getText().toString().equals(m_pw)) {
                            dialog.setMessage("密碼重製中....請稍等");
                            dialog.show();
                            user.updatePassword(et_resetpw.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        DatabaseReference updateChild = member_dataRef.child(auth.getCurrentUser().getUid());
                                        HashMap<String, Object> updateMap = new HashMap<String, Object>();
                                        updateMap.put("m_pw", et_resetpw.getText().toString());
                                        updateChild.updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    sp = getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sp.edit();
                                                    editor.putBoolean("resetpw", true);
                                                    editor.commit();
                                                    System.out.println("resetpwBoolean----" + sp.getBoolean("resetpw", false));
                                                    dialog.dismiss();
                                                    Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                                                    auth.signOut();
                                                    Intent intent = new Intent(getContext(), Login.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });

                                    } else{
                                        task.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println("修改密碼失敗------"+e);
                                            }
                                        });
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "修改失敗，請確認網路是否異常", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "舊密碼有誤", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext(), "新密碼不一致", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resetpw, container, false);
        auth = FirebaseAuth.getInstance();
        member_dataRef = FirebaseDatabase.getInstance().getReference("member");
        et_resetpw = view.findViewById(R.id.et_resetpw);
        et_resetpw2 = view.findViewById(R.id.et_resetpw2);
        et_oldpw = view.findViewById(R.id.et_oldpw);
        member_dataRef.child(auth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        m_pw = dataSnapshot.child("m_pw").getValue().toString();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return view;
    }

}
