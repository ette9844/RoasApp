package com.demo.project.roas.demo2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class TableDetail extends Activity {

    String restaurant;
    String tableNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_table_detail);

        Intent intent = getIntent();
        restaurant = intent.getStringExtra("restaurant");
        String tableNum = intent.getStringExtra("tableNum");

        Button confirmButton = (Button) findViewById(R.id.confirmButton);
        TextView restaurant = (TextView) findViewById(R.id.restaurant);
        TextView table = (TextView) findViewById(R.id.tableNum);

        restaurant.setText(this.restaurant);
        table.setText(tableNum+"번 테이블");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
