package com.example.user.limger;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class membermodify extends AppCompatActivity {
    private FragmentManager manager;
    private android.support.v4.app.FragmentTransaction transaction;
    ResetpwFragment resetpwFragment;
    ProgressDialog pd;

    ActionBar actionBar;
    MenuInflater inflater;
    CircleImageView mmod_imageView;
    Button but_mmod_img;
    ScrollView scrollView;
    EditText et_name, et_nickname, et_account, et_phone,
            et_school, et_subject, et_year, et_mon, et_day, et_interest;
    TextView txt_email, txt_Resetpw, txt_gender;

    Date date;
    String sum;
    String m_account, m_pw, m_name, m_nick, m_email, Year, Mon, day, m_tel, m_school, m_subject, m_interest, m_head;
    String m_gender = "";
    String mState;
    StorageReference mStorageRef;
    DatabaseReference modifydatabaseRef;
    FirebaseAuth firebaseAuth;
    String user_uid;
    Boolean putPic = false;
    private StorageTask mUploadTask;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    List<registe_firebase> accountList;
    Boolean Sameaccount =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membermodify);

        manager = getSupportFragmentManager();

        //title字體顏色設定
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFA38473")));
        View viewActionBar = getLayoutInflater().inflate(R.layout.title, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Limger");
        actionBar.setCustomView(viewActionBar, params);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        modifydatabaseRef = FirebaseDatabase.getInstance().getReference().child("member");
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getCurrentUser().getUid();
        accountList = new ArrayList<>();
        mmod_defin();    //取得物件
        display_data();//帶值出來顯示
        mmod_but_mmodlistener();
        ResetPassword();
    }


    public void mmod_defin() {

        mmod_imageView = findViewById(R.id.mmod_imageView);

        but_mmod_img = findViewById(R.id.but_mmod_img);

        et_name = findViewById(R.id.et_mmod_name);
        et_nickname = findViewById(R.id.et_mmod_nickname);
        et_account = findViewById(R.id.et_mmod_account);
        txt_email = findViewById(R.id.txt_email);
        et_phone = findViewById(R.id.et_mmod_tel);
        et_school = findViewById(R.id.et_mmod_school);
        et_subject = findViewById(R.id.et_mmod_subject);
        et_year = findViewById(R.id.et_mmod_year);
        et_mon = findViewById(R.id.et_mmod_mon);
        et_day = findViewById(R.id.et_mmod_day);
        et_interest = findViewById(R.id.et_mmod_inter);
        txt_gender = findViewById(R.id.txt_gender);
        txt_Resetpw = findViewById(R.id.txt_Resetpw);
        scrollView = findViewById(R.id.modify_scrollview);
    }

    private void ResetPassword() {
        txt_Resetpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mState = "HIDE_MENU";
                scrollView.setVisibility(View.GONE);
                transaction = manager.beginTransaction();
                resetpwFragment = new ResetpwFragment();
                transaction.replace(R.id.LinearLayout_resetpw, resetpwFragment, "resetpwFragment");

//                Bundle bundles = new Bundle();
//                bundles.putString("Title", Title);
//                resetpwFragment.setArguments(bundles);
                transaction.commit();
            }
        });
    }

    private void display_data() {
        modifydatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    registe_firebase memberData = artistSnapshot.getValue(registe_firebase.class);
                    accountList.add(memberData);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        modifydatabaseRef.child(user_uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        m_account = dataSnapshot.child("m_account").getValue().toString();
                        m_pw = dataSnapshot.child("m_pw").getValue().toString();
                        m_name = dataSnapshot.child("m_name").getValue().toString();
                        m_nick = dataSnapshot.child("m_nick").getValue().toString();
                        m_email = dataSnapshot.child("m_email").getValue().toString();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            date = simpleDateFormat.parse(dataSnapshot.child("m_birth").getValue().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Year = String.valueOf(date.getYear() + 1900);
                        Mon = String.valueOf(date.getMonth() + 1);
                        day = String.valueOf(date.getDate());
                        m_tel = dataSnapshot.child("m_tel").getValue().toString();
                        m_school = dataSnapshot.child("m_school").getValue().toString();
                        m_subject = dataSnapshot.child("m_subject").getValue().toString();
                        m_interest = dataSnapshot.child("m_interest").getValue().toString();
                        m_head = dataSnapshot.child("m_head").getValue().toString();

                        m_gender = dataSnapshot.child("m_gender").getValue().toString();
                        if (m_gender.equals("0")) {
                            m_gender = "女";
                        } else if (m_gender.equals("1")) {
                            m_gender = "男";
                        }
                        et_account.setText(m_account);

                        et_name.setText(m_name);
                        et_nickname.setText(m_nick);
                        txt_email.setText("*" + m_email);
                        et_year.setText(Year);
                        et_mon.setText(Mon);
                        et_day.setText(day);
                        et_phone.setText(m_tel);
                        et_school.setText(m_school);
                        et_subject.setText(m_subject);
                        et_interest.setText(m_interest);
                        txt_gender.setText(m_gender);
                        Picasso.get().load(m_head).into(mmod_imageView);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //呼叫meun中的modify字樣出來
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.modify, menu);

        //如果點選密碼修改就把Menu隱藏
        if (mState != null) {
            MenuItem item = menu.findItem(R.id.sure_modify);
            item.setVisible(false);   //hide it
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        return true;
    }

    //製作返回健功能和修改完成
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean flag = false;
        //noinspection SimplifiableIfStatement
        if (id == R.id.sure_modify) {
            String stop = null; //Edittext
            final String name = et_name.getText().toString();
            final String nickname = et_nickname.getText().toString();
            final String account = et_account.getText().toString();

            final String phone = et_phone.getText().toString();
            final String school = et_school.getText().toString();
            final String subject = et_subject.getText().toString();
            String year = et_year.getText().toString();
            String mon = et_mon.getText().toString();
            String day = et_day.getText().toString();
            final String interest = et_interest.getText().toString();

            //出生年月日
            sum = year + "-" + mon + "-" + day;


            String rgitem[] = {name, nickname, account, phone, school, subject, year, mon, day, interest};
            EditText etitem[] = {et_name, et_nickname, et_account, et_phone, et_school, et_subject, et_year, et_mon, et_day, et_interest};
            //檢查空值
            for (int i = 0; i < rgitem.length; i++) {
                String this_eritrm = rgitem[i];
                EditText this_etitem = etitem[i];
                //如果有空值，就傳123至stop中
                if (this_eritrm.isEmpty()) {
                    this_etitem.setError("未輸入");
                    stop = "123";
                    break;
                }
            }

            //如果stop等於123和Gender等於null ，就顯示"有空缺"
            if (stop == "123") {
                Toast.makeText(membermodify.this, "尚未填完", Toast.LENGTH_SHORT).show();
            } else {
                pd = ProgressDialog.show(membermodify.this, "修改中...", "系統更改中請稍等");
                if (putPic) {
                    StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                            + "." + getFileExtension(mImageUri));
                    mUploadTask = fileReference.putFile(mImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                    DatabaseReference updateChild = modifydatabaseRef.child(user_uid);
                                    HashMap<String, Object> updateMap = new HashMap<String, Object>();
                                    updateMap.put("m_name", name);
                                    updateMap.put("m_nick", nickname);
                                    updateMap.put("m_birth", sum);
                                    updateMap.put("m_account", account);
                                    updateMap.put("m_tel", phone);
                                    updateMap.put("m_school", school);
                                    updateMap.put("m_subject", subject);
                                    updateMap.put("m_head", taskSnapshot.getDownloadUrl().toString());
                                    updateMap.put("m_interest", interest);
                                    updateChild.updateChildren(updateMap);
                                    pd.dismiss();
                                    Toast.makeText(membermodify.this, "修改完成", Toast.LENGTH_SHORT).show();
                                    finish();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(membermodify.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    System.out.println("上傳失敗---" + e.getMessage());
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
//                            mProgressBar.setProgress((int) progress);
                                }
                            });
                } else {

                    for (int i =0;i<accountList.size();i++){

                        if(accountList.get(i).m_account.equals(account)){
                            if(accountList.get(i).id.equals(user_uid)){
                                Sameaccount =false;
                            }else{
                                Toast.makeText(membermodify.this, "帳號已存在", Toast.LENGTH_SHORT).show();
                                et_account.setError("帳號已存在");
                                Sameaccount =true;
                                break;
                            }
                        }else{
                            Sameaccount =false;
                        }
                    }
                    if(Sameaccount==false){

                        DatabaseReference updateChild = modifydatabaseRef.child(user_uid);
                        HashMap<String, Object> updateMap = new HashMap<String, Object>();
                        updateMap.put("m_name", name);
                        updateMap.put("m_nick", nickname);
                        updateMap.put("m_birth", sum);
                        updateMap.put("m_account", account);
                        updateMap.put("m_tel", phone);
                        updateMap.put("m_school", school);
                        updateMap.put("m_subject", subject);
                        updateMap.put("m_interest", interest);
                        updateChild.updateChildren(updateMap);
                        pd.dismiss();
                        Toast.makeText(membermodify.this, "修改完成", Toast.LENGTH_SHORT).show();
                        finish();
                    }


                }

//                if (mImageUri != null) {
//
//                } else {
//                    Toast.makeText(this, "請選擇大頭貼", Toast.LENGTH_SHORT).show();
//                }


            }


            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                new AlertDialog.Builder(membermodify.this)
                        .setTitle("確定離開")
                        .setMessage("確定是否放棄目前修改內容?")
                        .setIcon(R.drawable.warning1)
                        .setPositiveButton("確定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        finish();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub

                                    }
                                }).show();
                flag = true;
                break;
            default:
                flag = super.onOptionsItemSelected(item);
                break;
        }
        return flag;
    }


    //開啟相簿 放照片至imgeview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            putPic = true;
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mmod_imageView);
        }
    }

    //點選相片功能
    private void mmod_but_mmodlistener() {
        but_mmod_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //啟動圖片庫，執行onActivityResult程式
//                startActivityForResult(galleryPhoto.openGalleryIntent(),GALLERY_REQUEST);
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(membermodify.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);


                }
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(membermodify.this)
                    .setTitle("離開")
                    .setMessage("確定要放棄此操作嗎?")
                    .setIcon(R.drawable.warning1)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).show();
        }
        return true;
    }

    //ResetpwFragment的返回建隱藏
    public void resetActionBar(boolean childAction, int drawerMode) {
        if (childAction) {
            // [Undocumented?] trick to get up button icon to show

            actionBar.setDisplayHomeAsUpEnabled(true);


        } else {

        }


    }
}
