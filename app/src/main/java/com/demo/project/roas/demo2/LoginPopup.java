package com.demo.project.roas.demo2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPopup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_popup);

        Intent intent = getIntent();
        final String pw = intent.getStringExtra("pw");
        final String writer = intent.getStringExtra("writer");
        final String content = intent.getStringExtra("content");
        final String date = intent.getStringExtra("date");
        final EditText editText = (EditText) findViewById(R.id.editText);
        final int star = intent.getIntExtra("star",0);
        Button confirmButton = (Button) findViewById(R.id.confirmButton);
        Button cancelButton = (Button ) findViewById(R.id.cancelButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editText.getText().toString();
                if(input.equals(pw))
                {
                    Toast.makeText(LoginPopup.this, "비밀번호 확인 성공", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginPopup.this, ReviewWrite.class);
                    intent.putExtra("type",1);
                    intent.putExtra("writer", writer);
                    intent.putExtra("pw",pw);
                    intent.putExtra("content", content);
                    intent.putExtra("date", date);
                    intent.putExtra("star", star);
                    LoginPopup.this.startActivity(intent);
                    finish();
                }
                else
                {
                    Toast toast = Toast.makeText(LoginPopup.this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
