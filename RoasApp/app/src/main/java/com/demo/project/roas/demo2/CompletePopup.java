package com.demo.project.roas.demo2;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CompletePopup extends Activity {
    Button confirmButton;
    TextView text;
    MainHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_complete_popup);

        confirmButton = (Button) findViewById(R.id.confirmButton);
        text = (TextView) findViewById(R.id.text);

        final Intent intent = getIntent();
        intent.putExtra("complete", 1);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(39, intent);
                finish();
            }
        });

        DelayThread thread = new DelayThread();
        thread.start();

        handler = new MainHandler();
    }
    public class MainHandler extends Handler {

        public void handleMessage(Message msg){
            text.setText("작성 완료");
            confirmButton.setVisibility(View.VISIBLE);
        }
    };

    private class DelayThread extends Thread {

        public DelayThread() {
        }

        public void run() {
            try{
                Thread.sleep(2000);
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}