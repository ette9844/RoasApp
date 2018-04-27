package com.demo.project.roas.demo2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import static com.demo.project.roas.demo2.QRcameraActivity.cart;
import static com.demo.project.roas.demo2.QRcameraActivity.order;
import static com.demo.project.roas.demo2.QRcameraActivity.restaurant;
import static com.demo.project.roas.demo2.QRcameraActivity.table_num;

/**
 * Created by pc on 2017-10-21.
 */

public class CartActivity extends ActionBarActivity implements ListItemClickHelp
{
    final Activity act = this;
    private ArrayList<CartListViewItem> m_cartListViewItems = new ArrayList<CartListViewItem>();
    private CartListViewAdapter adapter = new CartListViewAdapter(this, m_cartListViewItems, this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Intent intent = getIntent();

        //홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //리스트뷰와 어댑터 선언
        ListView cartListView;

        //리스트뷰 참조
        cartListView = (ListView) findViewById(R.id.cartList);

        //리스트뷰 밑에 총 주문 금액 footer 달기
        View footer = getLayoutInflater().inflate(R.layout.cartlist_inflater, null, false);
        cartListView.addFooterView(footer);

        //adapter달기
        if(cartListView.getAdapter() == null)
            cartListView.setAdapter(adapter);
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //update only
                    adapter.notifyDataSetChanged();
                }
            });
        }

        //타이틀 설정
        setTitle("장바구니");

        //동적으로 아이템 추가시 발생하는 스크롤 문제 해결
        //cartListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        //총 주문 금액을 계산하기 위한 변수
        int priceSum = 0;

        //firebase 의 storage reference
        final FirebaseStorage storage = FirebaseStorage.getInstance();

        //cart에 들어있는 모든 항목에 대해
        for(Cart c : cart)
        {
            Log.d("button", "for문 내부");
            String name = c.getMenu();  //메뉴 이름 (key)
            int num = c.getNum();       //메뉴 갯수
            int price = c.getPrice();   //메뉴 가격

            //메뉴 사진 reference가져오기(+url decoding)
            StorageReference picRef = storage.getReference().child(restaurant);//"/menu/");
            String picFullRef = picRef.toString() +"/"+ name+".png";
            Log.e("picName", picFullRef);
            StorageReference pic = storage.getReferenceFromUrl(picFullRef);

            //총합 계산
            priceSum = priceSum + (num * price);

            //커스텀 리스트뷰의 어댑터에 연결
            Log.d("button", "adapter연결");
            adapter.addItem(pic, name, price, num);
        }

        //변경사항 고지
        Log.d("button", "adapter고지");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

        //장바구니 버튼 클릭 이벤트
        Button order_btn = (Button) findViewById(R.id.orderButton);
        order_btn.setOnClickListener(order_btnClickListener);

        //총합 출력
/*        View inflatedView = getLayoutInflater().inflate(R.layout.cartlist_inflater, null);*/
        TextView priceSumView = (TextView)footer.findViewById(R.id.priceSum);
        String strSum = String.format("%,d", priceSum);
        priceSumView.setText(strSum+"");
    }

    Button.OnClickListener order_btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //order에 cart의 변수들 이동 (깊은 복사)
            for(int i = 0; i < cart.size(); i++)
            {
                order.add(new Cart(cart.get(i)));
            }
            //cart 초기화
            cart.clear();
            finish();
            Intent intent = new Intent(CartActivity.this, OrderPopup.class);
            intent.putExtra("who_call", "cartAct"); //누가 오더팝업을 호출했는지
            startActivity(intent);
        }
    };

    //리스트뷰의 버튼 onclick들
    @Override
    public void onClick(View item, View widget, int position, int which)
    {
        switch (which)
        {
            case R.id.deleteButton:
                //삭제 버튼 클릭 시 cart에서 내용물 삭제
                //해당 메뉴가 등록되어있는 index를 찾아서 삭제
                int count;
                count = adapter.getCount();

                if(count > 0) {
                    m_cartListViewItems.remove(position);
                    Log.d("button", "count에 해당하는 cart값은?"+cart.get(position));
                    cart.remove(position);
                    if(act != null)
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    updateSum();
                    if(count == 1)
                    {
                        finish();
                    }
                }
                break;

            case R.id.minusButton:
                //- 버튼 클릭 시 cart에서 해당 내용물의 수량을 감소
                //해당 메뉴가 등록되어있는 index를 찾아서 해당 값을 수정
                int m_count;
                m_count = adapter.getCount();

                if(m_count > 0)
                {
                    CartListViewItem tmpCart = new CartListViewItem();
                    tmpCart = m_cartListViewItems.get(position);
                    //수량 감소, 값 수정
                    tmpCart.decrease();
                    m_cartListViewItems.set(position, tmpCart);
                    //장바구니 변수 속 값 수정
                    cart.get(position).decrease();
                    updateSum();
                    if(act != null)
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.refreshAdapter(m_cartListViewItems);
                            }
                        });
                }
                break;

            case R.id.plusButton:
                //+ 버튼 클릭 시 cart에서 해당 내용물의 수량을 감소
                //해당 메뉴가 등록되어있는 index를 찾아서 해당 값을 수정
                int p_count;
                p_count = adapter.getCount();

                if(p_count > 0)
                {
                    CartListViewItem tmpCart = new CartListViewItem();
                    tmpCart = m_cartListViewItems.get(position);
                    //수량 증가, 값 수정
                    tmpCart.increase();
                    m_cartListViewItems.set(position, tmpCart);
                    //장바구니 변수 속 값 수정
                    cart.get(position).increase();
                    updateSum();
                    if(act != null)
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.refreshAdapter(m_cartListViewItems);
                            }
                        });
                }
                break;

            default:
                break;
        }
    }

    //액션바의 버튼 기능
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            //액션 바의 뒤로 가기 버튼 기능
            Intent intent = new Intent(CartActivity.this, MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //백 버튼 기능 오버라이드
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(CartActivity.this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onBackPressed();
    }

    //카트값 업데이트하는 함수
    public void updateSum()
    {
        int priceSum = 0;
        //cart에 들어있는 모든 항목에 대해
        for(Cart c : cart)
        {
            int num = c.getNum();       //메뉴 갯수
            int price = c.getPrice();   //메뉴 가격

            //총합 계산
            priceSum = priceSum + (num * price);
        }
        //총합 update
        LinearLayout footer = (LinearLayout)findViewById(R.id.cart_footer);
        TextView priceSumView = (TextView)footer.findViewById(R.id.priceSum);
        String strSum = String.format("%,d", priceSum);
        priceSumView.setText(strSum+"");
    }
}
