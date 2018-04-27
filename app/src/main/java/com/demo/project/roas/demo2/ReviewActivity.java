package com.demo.project.roas.demo2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    public static String selectedMenu;// = "chicken"; //현재 리뷰를 보기위해 선택된 것   합칠떄 getExtraString 으로 받아온거 넣을예정
    public static String restaurant;// = "test"; //현재 식당의 이름
    public String userID;
    private List<Review> reviewList;
    private ReviewListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        selectedMenu = intent.getStringExtra("selectedMenu");
        restaurant = intent.getStringExtra("restaurant");

        ImageView backButton = (ImageView) findViewById(R.id.backButton);
        Button writeButton = (Button) findViewById(R.id.writeButton);
        Button refreshButton = (Button) findViewById(R.id.refreshButton);
        final ListView reviewListView = (ListView) findViewById(R.id.listView);
        TextView nameText = (TextView) findViewById(R.id.nameText);

        nameText.setText(selectedMenu);

        adapterList(reviewListView);

        //back 버튼을 누르면 이전 메뉴로 돌아간다.
        backButton.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //작성 버튼을 누르면 리뷰를 작성할수 있는 layout 으로 전환된다.
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //작성하는 layout으로 Activity 전환
                Intent intent = new Intent(ReviewActivity.this, ReviewWrite.class);
                intent.putExtra("type",0);
                ReviewActivity.this.startActivity(intent);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterList(reviewListView);
            }
        });
//        원래 위치로 이동
//        rankText.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //작성하는 layout으로 Activity 전환
//                Intent intent = new Intent(ReviewActivity.this, RankList.class);
//                intent.putExtra("restaurant", restaurant);
//                ReviewActivity.this.startActivity(intent);
//            }
//
//        });
    }

    public void adapterList(final ListView listView){
        //Firebase DB 참조
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reviewRef = rootRef.child(restaurant+"/review/"+selectedMenu);
        reviewList = new ArrayList<Review>();

        //DB의 변화를 수신대기함
        //Log.d("Test", reviewRef.getKey());
        reviewRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviewList.clear();//이전에 있던 데이터 삭제
                //dataSnapshot 밑의 위해 첫번째 child 가르킴
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                //whild문을 이용해서 dataSnpshot의 모든 child에 접근
                while(iterator.hasNext())
                {
                    DataSnapshot child = iterator.next();
                    Review review = child.getValue(Review.class);
                    review.setDate(child.getKey());

                    reviewList.add(review);
                }
                Collections.sort(reviewList,new CompareDateAsc());

                adapter = new ReviewListAdapter(getApplicationContext(), reviewList);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ReviewActivity.this, ReviewDetail.class);
                intent.putExtra("writer", reviewList.get(position).getId());
                intent.putExtra("content", reviewList.get(position).getContent());
                intent.putExtra("star", reviewList.get(position).getAvg());
                intent.putExtra("date", reviewList.get(position).getDate());
                intent.putExtra("pw", reviewList.get(position).getPw());

                ReviewActivity.this.startActivity(intent);
            }
        });
    }

    static class CompareDateAsc implements Comparator<Review>{

        @Override
        public int compare(Review o1, Review o2) {
            // TODO Auto-generated method stub
            return o2.getDate().compareTo(o1.getDate());
        }
    }
}
