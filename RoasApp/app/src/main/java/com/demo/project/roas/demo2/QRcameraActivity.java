package com.demo.project.roas.demo2;


import android.app.Activity;
import android.content.Intent;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class QRcameraActivity extends AppCompatActivity
{
    //장바구니, 주문, 가게이름(id), 테이블 번호 static변수
    public static ArrayList<Cart> cart = new ArrayList<Cart>();
    public static ArrayList<Cart> order = new ArrayList<Cart>();
    public static String restaurant;
    public static String table_num;

    //동적 리스트뷰를 위한 menulist 변수 & 어댑터
    ListView menuListView;
    MenuListViewAdapter adapter = new MenuListViewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcamera);

        //QR 코드 인식 카메라 띄우는 코드
        /*Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "ALL");*/
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();

        FirebaseApp.initializeApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(scanResult.getContents() != null && requestCode == IntentIntegrator.REQUEST_CODE)    //성공적으로 스캔했을 경우
        {
            //qr코드에서 url값 받아오는 코드
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            final String contents = result.getContents();
             /*final String contents = data.getStringExtra("SCAN_RESULT");
               String format = data.getStringExtra("SCAN_RESULT_FORMAT");*/

             //불러온 정보를 음식점 이름 과 테이블번호로 나눠 저장
             StringTokenizer tokened_contents = new StringTokenizer(contents, "_");

             String tmp_r = tokened_contents.nextToken();
             table_num = tokened_contents.nextToken();

             // 같은 음식점일 경우 기존 cart, order 유지 (* 실수로 초기화면에 돌아갔을 경우 방지)
             if (!tmp_r.equals(restaurant)) {
                  order.clear();
                  cart.clear();      // 다른 음식점일 경우 cart, order초기화 (* 카트/주문확인에 서로 다른 음식점의 메뉴가 담기는 것을 방지)
             }

             restaurant = tmp_r;

                 //menu 액티비티로 이동
                 Intent intent = new Intent(QRcameraActivity.this, MenuActivity.class);
                 startActivity(intent);
        }
        else       //성공적으로 스캔하지 못한 경우
        {
            Toast.makeText(getApplicationContext(), "QR스캔이 취소되었습니다.", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

}