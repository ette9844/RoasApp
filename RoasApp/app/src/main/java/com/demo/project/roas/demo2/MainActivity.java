package com.demo.project.roas.demo2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //QR 코드 인식 카메라 띄우는 코드
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "ALL");
        startActivityForResult(intent, 0);
        //QR 코드에서 읽어온 url값 불러오기

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        String contents = null;
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0)
        {
            if(resultCode == Activity.RESULT_OK)     //성공적으로 스캔했을 경우
            {
                //qr코드에서 url값 받아오는 코드
                contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");

                //받아온 값 출력하는 코드
                TextView tv = (TextView) findViewById(R.id.scanResult);
                tv.setText(contents);

                Log.i("확인", "QR코드에서 받아온 값" + contents.toString());
            }
            else if(resultCode == RESULT_CANCELED)      //성공적으로 스캔하지 못한 경우
            {

            }
        }

    }
}
