package com.demo.project.roas.demo2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import static com.demo.project.roas.demo2.QRcameraActivity.cart;
import static com.demo.project.roas.demo2.QRcameraActivity.order;
import static com.demo.project.roas.demo2.QRcameraActivity.restaurant;
import static com.demo.project.roas.demo2.QRcameraActivity.table_num;

/**
 * Created by pc on 2017-11-13.
 */

public class OrderPopup extends Activity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_order_popup);

        Intent intent = getIntent();
        final String who_call = intent.getStringExtra("who_call");

        //레이아웃의 뷰 참조 및 어댑터 생성
        TextView confirmButton = (TextView) findViewById(R.id.confirmButton);
        ListView orderList = (ListView)findViewById(R.id.orderList);
        OrderListViewAdapter adapter = new OrderListViewAdapter();

        orderList.setAdapter(adapter);

        //동적으로 아이템 추가시 발생하는 스크롤 문제 해결
        orderList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        //총 주문 금액을 계산하기 위한 변수
        int priceSum = 0;

        //order에 들어있는 모든 항목에 대해
        for(Cart c : order)
        {
            Log.d("order", "for문 내부");
            String name = c.getMenu();  //메뉴 이름 (key)
            int num = c.getNum();       //메뉴 갯수
            int price = c.getPrice();   //메뉴 가격

            //총합 계산
            priceSum = priceSum + (num * price);

            //커스텀 리스트뷰의 어댑터에 연결
            Log.d("order", "adapter연결");
            adapter.addItem(name, num, price);
        }

        //변경사항 고지
        Log.d("button", "adapter고지");
        adapter.notifyDataSetChanged();

        //총 합계 출력
        TextView totalPrice = (TextView)findViewById(R.id.order_total);
        String price = String.format("%,d", priceSum);
        totalPrice.setText(price);

        //확인 버튼 onclick 리스너
        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(who_call.equals("cartAct"))  //팝업창을 부른 액티비티가 카트 액티비티일 경우
                {
                    Toast.makeText(getApplicationContext(), "주문 완료!", Toast.LENGTH_SHORT).show();
                    //firebase 에서 database를 불러오는 reference
                    final DatabaseReference mDatabase;
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    //order DB에 해당 테이블 child를 생성 + complete=n
                    mDatabase.child(restaurant).child("order").child(table_num).child("complete").setValue("n");

                    int totalSum = 0;
                    //order의 모든 항목에 대해
                    for(Cart c: order)
                    {
                        //카트에서 메뉴 정보 불러오기
                        String menuKey = c.getMenu();
                        int menuNum = c.getNum();
                        int menuSum = c.getPrice() * menuNum;

                        totalSum += menuSum;

                        //오더 DB에 메뉴 정보 등록하기
                        mDatabase.child(restaurant).child("order").child(table_num).
                                child("foods").child(menuKey).child("number").setValue(menuNum);
                        mDatabase.child(restaurant).child("order").child(table_num).
                                child("foods").child(menuKey).child("sum").setValue(menuSum);
                    }

                    mDatabase.child(restaurant).child("order").child(table_num).child("sum").setValue(totalSum);
                    //메시지 기능을 위한 토큰 저장
                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    Log.d("token", refreshedToken);
                    mDatabase.child(restaurant).child("order").child(table_num).child("token").setValue(refreshedToken);
                }
                finish();
            }
        });


    }

    //백 버튼 기능 오버라이드
    @Override
    public void onBackPressed()
    {
        //cart에 order의 변수들 이동 (깊은 복사)
        for(int i = 0; i < order.size(); i++)
        {
            cart.add(new Cart(order.get(i)));
        }
        //order 초기화
        order.clear();
        finish();
    }
}
