package com.demo.project.roas.demo2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Iterator;

import static com.demo.project.roas.demo2.QRcameraActivity.cart;
import static com.demo.project.roas.demo2.QRcameraActivity.restaurant;
import static com.demo.project.roas.demo2.QRcameraActivity.table_num;

/**
 * Created by pc on 2017-10-30.
 */

public class MenuActivity extends AppCompatActivity
{
    //동적 리스트뷰를 위한 menulist 변수 & 어댑터
    ListView menuListView;
    MenuListViewAdapter adapter = new MenuListViewAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        FirebaseApp.initializeApp(this);

        //홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //리스트뷰 참조 및 adapter 달기
        menuListView = (ListView)findViewById(R.id.menuList);
        menuListView.setAdapter(adapter);

        //동적으로 아이템 추가시 발생하는 스크롤 문제 해결
        menuListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        //장바구니 버튼 onclick 리스너
        Button cartButton = (Button)findViewById(R.id.cartButton);
        cartButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v)
            {
                if(cart.size() == 0)
                    Toast.makeText(getApplicationContext(), "메뉴를 선택해주세요.", Toast.LENGTH_SHORT).show();
                else
                {
                    //인텐트에 변수를 담고 상세정보 activity로
                    Intent intent = new Intent(MenuActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            }
        });

        //호출하기 버튼 onclick 리스너
        Button callButton = (Button)findViewById(R.id.callButton);
        callButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v)
            {
                //어플 종료 확인창
                AlertDialog.Builder alert_ex = new AlertDialog.Builder(MenuActivity.this);
                alert_ex.setMessage("직원을 호출하겠습니까?");

                alert_ex.setPositiveButton("취소", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //취소시 아무 것도 하지 않음
                    }
                });
                alert_ex.setNegativeButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //호출벨 기능

                    }
                });
                alert_ex.setTitle(""+table_num+"번 테이블 호출 확인");
                AlertDialog alert = alert_ex.create();
                alert.show();
            }
        });

        setMenuList();

        //리스트뷰의 클릭이벤트 정의
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuListViewItem item = (MenuListViewItem) adapter.getItem(position);
                Log.d("exRating", item.getMenuRating()+"");
                Log.d("exMenukey", item.getMenuName());
                Intent intent = new Intent (MenuActivity.this, MenuDetailActivity.class);
                intent.putExtra("Menukey", item.getMenuName());
                intent.putExtra("rate", item.getMenuRating());
                MenuActivity.this.startActivity(intent);
            }
        });
    }

    //메뉴 리스트를 설정하는 함수
    protected  void setMenuList()
    {
        //받아온 가게-테이블 번호 정보로 타이틀 설정하기
        setTitle(restaurant + "-" + table_num + "번 테이블");



        //firebase 의 storage reference
        final FirebaseStorage storage = FirebaseStorage.getInstance();

        //firebase 에서 database를 불러오는 reference
        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //유무검사
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(restaurant).exists())
                {
                    //존재할 경우->다음 코드로 이동
                }
                else
                {
                    //존재하지 않을경우
                    Toast.makeText(getApplicationContext(), "잘못된 QR코드 입니다.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child(restaurant).child("menu").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get menu value
                if(dataSnapshot == null)
                {
                    //디비에서 맞는 정보를 찾지 못하였을 경우 Error
                    Toast.makeText(getApplicationContext(), "잘못된 QR코드 입니다.", Toast.LENGTH_SHORT).show();
                    finishActivity(1);
                }
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    //메뉴DB에서 사진정보, 가격 등의 정보 불러오기
                    final String name = postSnapshot.getKey().toString();
                    String priceStr = postSnapshot.child("price").getValue().toString();
                    int priceInt = Integer.parseInt(priceStr);
                    final String price = String.format("%,d", priceInt);

                    //수정사항
                    //메뉴 사진 reference가져오기(+url decoding)
                    String picUrl = "";
                    try {
                        StorageReference picRef = storage.getReference().child(restaurant+"/"+name+".png");
                        picUrl = URLDecoder.decode(picRef.toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e)
                    {
                        Log.e("MenuActivity Exception", "UnsupportedEncodingException");
//                        StorageReference picRef = storage.getReference().child(restaurant);
//                        String picFull = picRef.toString()+"/"+ name+".png";
//                        pic = storage.getReferenceFromUrl(picFull);
                    }
                    final StorageReference pic = storage.getReferenceFromUrl(picUrl);

                    //평점계산을위해 Reivew접근
                    DatabaseReference reviewChild = mDatabase.child(restaurant).child("review/"+name);
                    reviewChild.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int sum=0;
                            int count= 0;
                            int rate=0;
                            for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                                count++;
                                sum+= Integer.parseInt(postSnapshot.child("avg").getValue().toString());
                                Log.e("avg: ", postSnapshot.child("avg").getValue().toString());
                            }
                            if(count==0)
                                rate=0;
                            else
                                rate = sum/count;
                            adapter.addItem(pic, name, price, rate);
                            //변경사항 고지
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
//
//                    float rate = 0;
//                    //float rate = getRatingsAverage(restaurant, name);
//
//                    //커스텀 리스트뷰의 어댑터에 연결
//                    adapter.addItem(pic, name, price, rate);
//                    //변경사항 고지
//                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //디비에서 맞는 정보를 찾지 못하였을 경우 Error
                Toast.makeText(getApplicationContext(), "잘못된 QR코드 입니다.", Toast.LENGTH_SHORT).show();
                finishActivity(1);
            }
        });
    }

    //좌석현황 버튼 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_qr, menu);
        return true;
    }

    //액션바의 버튼 기능
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            //액션 바의 뒤로 가기 버튼 기능
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            return true;
        }

        if(id == R.id.action_seat)
        {
            //좌석현황 버튼 기능
            Intent intent = new Intent(MenuActivity.this, SeatPopup.class);
            intent.putExtra("restaurant", restaurant);
            MenuActivity.this.startActivity(intent);
            return true;
        }

        if(id == R.id.ranking)
        {
            Intent intent = new Intent(MenuActivity.this, RankList.class);
            intent.putExtra("restaurant", restaurant);
            MenuActivity.this.startActivity(intent);
            return true;
        }

        if(id == R.id.action_order)
        {
            //주문확인 버튼 기능
            Intent intent = new Intent(MenuActivity.this, OrderPopup.class);
            intent.putExtra("who_call", "MenuAct");
            MenuActivity.this.startActivity(intent);
            return true;
        }

        if(id ==R.id.action_info){
            Intent intent = new Intent(MenuActivity.this, TableDetail.class);
            intent.putExtra("restaurant", restaurant);
            intent.putExtra("tableNum", table_num);
            MenuActivity.this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //백 버튼 기능 오버라이드
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onBackPressed();
    }
}
