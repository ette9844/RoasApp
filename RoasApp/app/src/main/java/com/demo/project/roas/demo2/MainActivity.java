package com.demo.project.roas.demo2;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;
import java.util.StringTokenizer;

import static com.demo.project.roas.demo2.QRcameraActivity.cart;
import static com.demo.project.roas.demo2.QRcameraActivity.order;
import static com.demo.project.roas.demo2.QRcameraActivity.restaurant;
import static com.demo.project.roas.demo2.QRcameraActivity.table_num;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView qrButton = (TextView) findViewById(R.id.qrCameraButton);

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent QRcameraIntent = new Intent(MainActivity.this, QRcameraActivity.class);
                MainActivity.this.startActivity(QRcameraIntent);
            }
        });

    }

    //백 버튼 기능 오버라이드
    @Override
    public void onBackPressed()
    {
        //어플 종료 확인창
        AlertDialog.Builder alert_ex = new AlertDialog.Builder(this);
        alert_ex.setMessage("로아스를 종료하시겠습니까?");

        alert_ex.setPositiveButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //취소시 아무 것도 하지 않음
            }
        });
        alert_ex.setNegativeButton("종료", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cart 및 변수 초기화
                cart.clear();
                order.clear();
                restaurant = "";
                table_num = "";
                //어플 종료
                finish();
            }
        });
        alert_ex.setTitle("종료 확인창");
        AlertDialog alert = alert_ex.create();
        alert.show();
    }

    // 어플리케이션과 모든 프로세스를 종료하는 함수
    public void close()
    {
        finish();
        int nSDKVersion = Integer.parseInt(Build.VERSION.SDK);
        if(nSDKVersion < 8)    //2.1이하
        {
            ActivityManager actMng = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
            actMng.restartPackage(getPackageName());
        }
        else
        {
            new Thread(new Runnable() {
                public void run() {
                    ActivityManager actMng = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
                    String strProcessName = getApplicationInfo().processName;
                    while(true)
                    {
                        List<ActivityManager.RunningAppProcessInfo> list = actMng.getRunningAppProcesses();
                        for(ActivityManager.RunningAppProcessInfo rap : list)
                        {
                            if(rap.processName.equals(strProcessName))
                            {
                                if(rap.importance >= ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND)
                                    actMng.restartPackage(getPackageName());
                                Thread.yield();
                                break;
                            }
                        }
                    }
                }
            }, "Process Killer").start();
        }
    }

}
