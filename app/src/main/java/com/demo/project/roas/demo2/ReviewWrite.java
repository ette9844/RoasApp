package com.demo.project.roas.demo2;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReviewWrite extends AppCompatActivity {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_IMAGE_CROP = 3;

    private String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}; //권한 설정 변수
    private static final int MULTIPLE_PERMISSIONS = 101;

    ImageView image;
    Uri photoURI;
    String takePic=null;
    int picStat=0;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    String date=null;
    String newDate=null;
    int star=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);
        checkPermissions();

        ImageView backButton = (ImageView) findViewById(R.id.backButton);
        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText pwText = (EditText) findViewById(R.id.pwText);
        final EditText contentText = (EditText) findViewById(R.id.contentText);
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        TextView titleText = (TextView) findViewById(R.id.titleText);
        Button imageButton = (Button) findViewById(R.id.imageButton);
        Button picButton = (Button) findViewById(R.id.picButton);
        image = (ImageView) findViewById(R.id.image);
        Button completeButton = (Button) findViewById(R.id.completeButton);
        final ImageView xButton = (ImageView) findViewById(R.id.xButton);

        final Spinner spinner = (Spinner)findViewById(R.id.star);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.rate, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Firebase Storage 참조
        final FirebaseStorage storage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);   //수정인지 작성인지 판별
        titleText.setText("작성하기");
        if (type == 1)//수정일경우 이전의 값을 불러와서 set한다.
        {
            String writer = intent.getStringExtra("writer");
            String pw = intent.getStringExtra("pw");
            String content = intent.getStringExtra("content");
            titleText.setText("수정하기");
            date = intent.getStringExtra("date");
            star = intent.getIntExtra("star",1);

            String path = ReviewActivity.restaurant + "/" + ReviewActivity.selectedMenu + "/" + date + ".png";
            StorageReference storageReference = storage.getReference(path);
            Log.d("storagePath", storageReference.toString());

            Glide.with(getApplicationContext())
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
                    .skipMemoryCache(true)// 메모리 캐시 저장 off
                    .error(R.drawable.error)
//                    .into(image);
                    .into(new ViewTarget<ImageView, GlideDrawable>(image) {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            image.setImageDrawable(resource);
                            picStat =1;
                            xButton.setVisibility(ImageView.VISIBLE);
                        }
                    });

            idText.setText(writer);
            pwText.setText(pw);
            contentText.setText(content);
            spinner.setSelection(star-1);
            }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String string = (String)spinner.getSelectedItem();
                star = Integer.parseInt(string);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
                //추가 권한 필요
                picStat =1;
                xButton.setVisibility(ImageView.VISIBLE);
            }
        });

        picButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try{
                    photoFile = createFile();
                }catch(IOException ex)
                {
                    ex.printStackTrace();
                }
                if(photoFile !=null)
                {
                 //누가(7.0)부터는 file://로 시작.. 다른 앱과 주고받기 불가능
                    photoURI = FileProvider.getUriForFile(ReviewWrite.this.getApplicationContext(),"com.demo.project.roas.demo2",photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    picStat =1;
                    xButton.setVisibility(ImageView.VISIBLE);
                }
//                Uri mUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "ROAS_TEMP.PNG"));
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//                }
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String content = contentText.getText().toString();
                String id = idText.getText().toString();
                String pw = pwText.getText().toString();
//                int star = Integer.parseInt(starEdit.getText().toString()); spinner를 선택할때마다 star값 변경으로 인해 필요가 없어짐
                Log.d("idWrite", id);
                if(id.equals("") || pw.equals(""))
                    {
                        Toast toast = Toast.makeText(ReviewWrite.this, "아이디와 비밀번호를 작성해주세요!", Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                    String timeStamp;
                    if(date==null){
                        timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                    }else {
                        timeStamp = date;
                    }
                    //Database에 접근
                    DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference(ReviewActivity.restaurant+"/review/"+ReviewActivity.selectedMenu+"/"+ timeStamp);
                    dataRef.child("avg").setValue(star);
                    dataRef.child("content").setValue(content);
                    dataRef.child("id").setValue(id);
                    dataRef.child("pw").setValue(pw);

                    //참조 생성
                    String path = ReviewActivity.restaurant+"/"+ReviewActivity.selectedMenu+"/"+timeStamp + ".png";
                    StorageReference storageRef = storage.getReference(path);
                    if(picStat==1)
                    {
                        //메모리에서 업로드드
                        image.setDrawingCacheEnabled(true);
                        image.buildDrawingCache();
                        Bitmap bitmap = image.getDrawingCache();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        int radioId = radioGroup.getCheckedRadioButtonId();

                        if(radioId == R.id.intactRadio){
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] data = baos.toByteArray();
                            UploadTask uploadTask = storageRef.putBytes(data);
                        }else{
                            bitmap.compress(Bitmap.CompressFormat.WEBP, 50, baos);
                            byte[] data = baos.toByteArray();
                            UploadTask uploadTask = storageRef.putBytes(data);
                        }
                    }
                }
                Intent intent = new Intent(ReviewWrite.this, CompletePopup.class);
                ReviewWrite.this.startActivityForResult(intent, 39);
//                finish();
            }
        });

        xButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                picStat=0;
                image.setImageResource(R.drawable.error);
                xButton.setVisibility(ImageView.INVISIBLE);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private File createFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = timeStamp + "_ROAS_TEMP.png";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/roas/"+imageName);
        if(!storageDir.exists())
        {
            storageDir.getParentFile().mkdirs();
            storageDir.createNewFile();
        }

        takePic = storageDir.getAbsolutePath();
        return storageDir;
    }
    private void Picture(Intent data) {
        if(photoURI !=null)
        {
            try {
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(takePic);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = exifOrientationToDegrees(exifOrientation);
                Bitmap bitmap = BitmapFactory.decodeFile(takePic);//경로를 통해 비트맵으로 전환
                image.setImageBitmap(rotate(bitmap, exifDegree));

            } catch (Exception e) {
                e.printStackTrace();
            }

//            Bitmap bitmap = BitmapFactory.decodeFile(takePic);
//            image.setImageBitmap(bitmap);

        }else {

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED){
//            //Manifest.permission.READ_CALENDAR이 접근 승낙 상태 일때
//        } else{
//            //Manifest.permission.READ_CALENDAR이 접근 거절 상태 일때
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CALENDAR)){
//                //사용자가 다시 보지 않기에 체크를 하지 않고, 권한 설정을 거절한 이력이 있는 경우
//            } else{
//                //사용자가 다시 보지 않기에 체크하고, 권한 설정을 거절한 이력이 있는 경우
//            }
//            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
//            //만약 사용자가 다시 보지 않기에 체크를 했을 경우엔 권한 설정 다이얼로그가 뜨지 않고,
//            //곧바로 OnRequestPermissionResult가 실행된다.
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALENDAR},0);
//        }

            try {
                ExifInterface exif = null;
                Uri imgUri = data.getData();
                String imagePath = getRealPathFromURI(imgUri);
                try {
                    exif = new ExifInterface(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = exifOrientationToDegrees(exifOrientation);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
                image.setImageBitmap(rotate(bitmap, exifDegree));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int exifOrientationToDegrees(int exifOrientation) {
            if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return 90;
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
                return 180;
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
                return 270;
        }
        return 0;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public Bitmap rotate(Bitmap src, float degree) {
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    private void addGallery(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(takePic);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 39)
            finish();
        if(resultCode != RESULT_OK)
            return;
        switch(requestCode) {
            case REQUEST_IMAGE_PICK:
                Picture(data);
                break;
            case REQUEST_TAKE_PHOTO:
                addGallery();
                Picture(data);
                takePic=null;
                photoURI=null;
                break;
            default:
                break;
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) { //사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) { //권한이 추가되었으면 해당 리스트가 empty가 아니므로 request 즉 권한을 요청합니다.
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    //아래는 권한 요청 Callback 함수입니다. PERMISSION_GRANTED로 권한을 획득했는지 확인할 수 있습니다. 아래에서는 !=를 사용했기에
//권한 사용에 동의를 안했을 경우를 if문으로 코딩되었습니다.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    //권한 획득에 동의를 하지 않았을 경우 아래 Toast 메세지를 띄우며 해당 Activity를 종료시킵니다.
    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
}