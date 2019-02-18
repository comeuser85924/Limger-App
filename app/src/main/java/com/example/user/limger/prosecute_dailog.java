package com.example.user.limger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.app.AlertDialog.THEME_HOLO_LIGHT;

/**
 * Created by User on 2018/7/3.
 */

public class prosecute_dailog extends AppCompatDialogFragment {

    Context mContext;

    StorageTask mUploadTasks;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private String imgUrl;
    ImageView pro_img;
    TextView pro_txt_member,txtnick,txtschool,txtsex;
    EditText pro_et_title,pro_et_detail;
    Button pro_bt_uploadimg;
    CircleImageView Imgprosecute;
    DatabaseReference member,prosecute_dataRef,prosecuteLasttime_dataRef;
    StorageReference mStorageRef;
    FirebaseAuth firebaseAuth;
    String p_premid,p_maccount,roomid;
    String myselfaccount;
    String titles,details;
    String user_id;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        member = FirebaseDatabase.getInstance().getReference("member");
        prosecute_dataRef = FirebaseDatabase.getInstance().getReference().child("prosecute");
        prosecuteLasttime_dataRef = FirebaseDatabase.getInstance().getReference().child("prosecuteLatesttime");
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        p_premid = getArguments().getString("p_premid");
        p_maccount = getArguments().getString("p_maccount");
        roomid = getArguments().getString("roomid");


        member.child(firebaseAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myselfaccount = dataSnapshot.child("m_account").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        member.child(p_premid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.child("m_head").getValue().toString()).into(Imgprosecute);
                txtnick.setText(dataSnapshot.child("m_nick").getValue().toString());
                if(dataSnapshot.child("m_gender").getValue().toString().equals("0")){
                    txtsex.setText("(女)");
                }else{
                    txtsex.setText("(男)");
                }
                pro_txt_member.setText(p_maccount);
                txtschool.setText(dataSnapshot.child("m_school").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    //開啟相簿 放照片至imgeview
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(pro_img);
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTasks = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            imgUrl = taskSnapshot.getDownloadUrl().toString();

                            System.out.println("圖片1----"+imgUrl);
                        }
                    });
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_prosecute_dailog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),THEME_HOLO_LIGHT);
        Imgprosecute =view.findViewById(R.id.Imgprosecute);
        txtnick = view.findViewById(R.id.txtnick);
        txtschool= view.findViewById(R.id.txtschool);
        txtsex = view.findViewById(R.id.txtsex);
        pro_img = view.findViewById(R.id.pro_img);
        pro_txt_member = view.findViewById(R.id.pro_txt_ID);
        pro_et_title = view.findViewById(R.id.pro_et_title);
        pro_et_detail = view.findViewById(R.id.pro_et_detail);
        pro_bt_uploadimg = view.findViewById(R.id.pro_bt_uploadimg);


        builder.setView(view)

                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })

                .setPositiveButton("確定檢舉", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        if (mImageUri != null) {


                            titles = pro_et_title.getText().toString().trim();
                            details = pro_et_detail.getText().toString();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date curDate  = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                            final String str = formatter.format(curDate);
                            final String pid = prosecute_dataRef.push().getKey();

                            prosecuteLatesttime_firebase pro_Late = new prosecuteLatesttime_firebase(pid,p_premid);
                            prosecuteLasttime_dataRef.child(p_premid).setValue(pro_Late);

                            System.out.println("檢舉最新資料------"+p_premid);
                            System.out.println("檢舉最新資料------"+pro_Late);
                            prosecute_firebase pro_data = new prosecute_firebase(pid,p_premid,user_id,titles,details
                                    ,imgUrl,str,roomid,"0");
                            prosecute_dataRef.child(pid).setValue(pro_data);
                            Toast.makeText(mContext,"檢舉成功", Toast.LENGTH_SHORT).show();


//                        }
//                        else {
//                            Toast.makeText(getContext(), "檢舉需有圖為證，請截圖", Toast.LENGTH_SHORT).show();
//                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK);

        pro_bt_uploadimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //啟動圖片庫，執行onActivityResult程式

                if (mUploadTasks != null && mUploadTasks.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);

                }
            }
        });

        return dialog;
    }

    //不知道幹嘛 反正上面會呼叫到
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


}
