package com.example.user.limger;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class registe extends AppCompatActivity {

    ActionBar actionBar;
    CircleImageView circleImageView;
    Button butimg;
    RadioButton radioButton_man, radioButton_woman;
    RadioGroup radioGroupg;
    EditText et_name, et_nickname, et_account, et_password, et_password1, et_email, et_phone, et_school, et_subject, et_year, et_mon, et_day, et_interest;
    ProgressBar mProgressBar;
    MenuInflater inflater;
    ProgressDialog pd;

    String sum;
    String Gender = null;

    String mid;
    String account;
    String password;
    StorageReference mStorageRef;
    DatabaseReference reg_memberRef;
    FirebaseAuth firebaseAuth;
    SQLiteDatabase db;
    String user_uid;

    private StorageTask mUploadTask;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    int i =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registe);


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

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        reg_memberRef = FirebaseDatabase.getInstance().getReference("member");
        firebaseAuth = FirebaseAuth.getInstance();
        defin();    //取得物件
        radioGrouplistener();   //radiobutton男女
        butimglistener();  //相片選擇


    }

    public void defin() {

        butimg = findViewById(R.id.but_rg_img);
        radioGroupg = findViewById(R.id.radiogroup);
        radioButton_man = findViewById(R.id.radioButton_rg_man);
        radioButton_woman = findViewById(R.id.radioButton_rg_woman);

        et_name = findViewById(R.id.et_rg_name);
        et_nickname = findViewById(R.id.et_rg_nickname);
        et_account = findViewById(R.id.fg_email);
        et_password = findViewById(R.id.et_rg_password);
        et_password1 = findViewById(R.id.et_rg_password1);
        et_email = findViewById(R.id.et_rg_email);
        et_phone = findViewById(R.id.et_rg_phone);
        et_school = findViewById(R.id.et_rg_school);
        et_subject = findViewById(R.id.et_rg_subject);
        et_year = findViewById(R.id.et_rg_year);
        et_mon = findViewById(R.id.et_rg_mon);
        et_day = findViewById(R.id.et_rg_day);
        et_interest = findViewById(R.id.et_rg_interest);

        mProgressBar = findViewById(R.id.progressBar);
        circleImageView = findViewById(R.id.memdata_dailog_img);
    }

    //男女
    private void radioGrouplistener() {
        radioGroupg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.radioButton_rg_woman) {
                    Gender = "0";
                } else {
                    Gender = "1";

                }
            }
        });
    }

    //匯入menu中"註冊完成"字樣
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater = getMenuInflater();
        inflater.inflate(R.menu.registe, menu);
        return true;
    }

    //製作返回健和註冊功能
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean flag = false;
        //noinspection SimplifiableIfStatement
        if (id == R.id.registe) {

            but_regisedlistener();//註冊按鈕功能

//            Boolean insert = db.insert(mid,account,password);
//            if(insert==true){
//                Toast.makeText(registe.this,"新增至SQLite資料庫",Toast.LENGTH_SHORT);
//            }

            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(circleImageView);
        }
    }

    //選擇大頭貼
    private void butimglistener() {
        butimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //啟動圖片庫，執行onActivityResult程式
//                startActivityForResult(galleryPhoto.openGalleryIntent(),GALLERY_REQUEST);
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(registe.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);

//                    uploadFile();
                }
            }
        });
    }



    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //檢查註冊內容是否錯誤並上傳資料
    private void but_regisedlistener() {

        String stop = null; //Edittext


        final String name = et_name.getText().toString();
        final String nickname = et_nickname.getText().toString();
        account = et_account.getText().toString();
        password = et_password.getText().toString();
        final String password1 = et_password1.getText().toString();
        final String email = et_email.getText().toString();
        final String phone = et_phone.getText().toString();
        final String school = et_school.getText().toString();
        final String subject = et_subject.getText().toString();
        final String year = et_year.getText().toString();
        final String mon = et_mon.getText().toString();
        final String day = et_day.getText().toString();
        final String interest = et_interest.getText().toString();

        //出生年月日
        sum = year + "-" + mon + "-" + day;

        //密碼不一致
        if (!password.equals(password1)) {
            et_password.setError("密碼不一致");
        } else if (password.length() < 6) {
            et_password.setError("請輸入6位以上");
        }

        String rgitem[] = {name, nickname, account, password, password1, email, phone, school, subject, year, mon, day, interest};
        EditText etitem[] = {et_name, et_nickname, et_account, et_password, et_password1, et_email, et_phone, et_school, et_subject, et_year, et_mon, et_day, et_interest};
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
        if (stop == "123" || Gender == null) {
            Toast.makeText(registe.this, "尚未填完", Toast.LENGTH_LONG).show();
        } else {
            if (mImageUri != null) {
                if (email.contains("@yahoo.com.tw") || email.contains("@gmail.com") || email.contains("@gm.cyut.edu.tw")) {
                    //--------------年月日之後再詳細製作
                    reg_memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot reg_dataSnapshot: dataSnapshot.getChildren()){
                                registe_firebase memberData = reg_dataSnapshot.getValue(registe_firebase.class);
                                if(memberData.m_email.equals(email)){
                                    i+=1;
                                    if(i==1){
                                        et_email.setError("信箱已存在");
                                        Toast.makeText(registe.this, "信箱已註冊過", Toast.LENGTH_LONG).show();
                                        break;
                                    }

                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    if (Integer.valueOf(year) < 2018 && Integer.valueOf(year) > 1900 &&
                            Integer.valueOf(mon) > 1 && Integer.valueOf(mon) < 12 &&
                            Integer.valueOf(day) > 1 && Integer.valueOf(day) < 31) {
                        if(phone.length()==10 ||phone.length()==8 ){
                            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                                    + "." + getFileExtension(mImageUri));

                            mUploadTask = fileReference.putFile(mImageUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
//                                    mProgressBar.setProgress(0);
                                                }
                                            }, 500);

                                            firebaseAuth.createUserWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString())
                                                    .addOnCompleteListener(registe.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            pd = ProgressDialog.show(registe.this, "註冊中...", "系統執行中請稍等");
                                                            if (task.isSuccessful()) {
                                                                user_uid = firebaseAuth.getCurrentUser().getUid();
                                                                mid = reg_memberRef.push().getKey();
                                                                registe_firebase rf = new registe_firebase(user_uid, name, nickname, sum, Gender, account, password, email, phone, school,
                                                                        subject, taskSnapshot.getDownloadUrl().toString(), interest, 30, "0", null, null);
                                                                reg_memberRef.child(user_uid).setValue(rf);
                                                                Toast.makeText(registe.this,"註冊成功",Toast.LENGTH_LONG).show();
                                                                finish();
                                                            }else{
                                                                Toast.makeText(registe.this,"網路或系統有誤，請重試...",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(registe.this, "上傳失敗", Toast.LENGTH_SHORT).show();
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
                        }else{
                            Toast.makeText(registe.this, "請輸入正確的手機格式", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(registe.this, "請輸入正確的日期格式", Toast.LENGTH_LONG).show();

                    }
                } else {
                    et_email.setError("格式有誤");
                }
            } else {
                Toast.makeText(this, "請選擇大頭貼", Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    protected void onStart() {
        super.onStart();

    }
}
