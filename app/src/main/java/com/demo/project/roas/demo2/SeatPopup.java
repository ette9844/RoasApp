package com.demo.project.roas.demo2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class SeatPopup extends Activity {
    TextView text =  null;
    int nowSeat =0;
    int totalSeat=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_seat_popup);
        text = (TextView) findViewById(R.id.text);
        Button confirmButton = (Button) findViewById(R.id.confirmButton);

        Intent intent = getIntent();
        String restaurant = intent.getStringExtra("restaurant");
        //Firebase DB 참조
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference seatRef = rootRef.child(restaurant+"/seat");
        DatabaseReference orderRef = rootRef.child(restaurant+"/order");

        seatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalSeat = Integer.parseInt(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //dataSnapshot 밑의 위해 첫번째 child 가르킴
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                //whild문을 이용해서 dataSnpshot의 모든 child에 접근
                while(iterator.hasNext()) {
                    DataSnapshot child = iterator.next();
                    nowSeat++;
                }
                text.setText(nowSeat+"/"+totalSeat);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

    }
}
