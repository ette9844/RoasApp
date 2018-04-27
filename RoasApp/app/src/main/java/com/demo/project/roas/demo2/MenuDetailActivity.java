package com.demo.project.roas.demo2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static com.demo.project.roas.demo2.QRcameraActivity.cart;
import static com.demo.project.roas.demo2.QRcameraActivity.restaurant;

/**
 * Created by pc on 2017-10-12.
 */

public class MenuDetailActivity extends ActionBarActivity
{
    //수정사항 - 변수 추가
    String MenuKey;
    int price;
    int rate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiry_menudetail);

        Button reviewButton = (Button) findViewById(R.id.reviewButton);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.detail_rating);
        TextView ratingValue = (TextView) findViewById(R.id.ratingValue);

        Intent intent = getIntent();
        final String menukey = intent.getStringExtra("Menukey");
        Log.d("MenuKey", menukey);
        int rateTemp = intent.getIntExtra("rate",0);

        MenuKey = menukey;  //수정사항

        ratingBar.setRating(rateTemp);
        ratingValue.setText(rateTemp+".0");
        Log.d("Rating", rateTemp+"");

        //홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //수정사항
        setTitle("메뉴 상세정보 - "+MenuKey);

        final DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference();

        //메뉴DB에서 메뉴 상세 정보 불러오기
        mDatabase.child(restaurant).child("menu").child(menukey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get menu value
                if(dataSnapshot == null) return;
                //상세메뉴view의 요소들
                TextView detailName = (TextView)findViewById(R.id.detail_name);
                TextView detailContent = (TextView)findViewById(R.id.detail_content);
                TextView ratingValue = (TextView)findViewById(R.id.ratingValue);
                ImageView detailImage = (ImageView)findViewById(R.id.detail_image);
                TextView detailPrice = (TextView)findViewById(R.id.detail_price);

                FirebaseStorage storage = FirebaseStorage.getInstance();

                //수정사항 pic 가져오는 방식 수정, 가격 정보 추가로 가져오기
                //메뉴 사진 reference가져오기(+url decoding)

                String picUrl = "";
                StorageReference pic;
                try {
                    StorageReference picRef = storage.getReference().child(restaurant+"/"+menukey+".png");
                    picUrl = URLDecoder.decode(picRef.toString(), "UTF-8");
                    pic = storage.getReferenceFromUrl(picUrl);
                    Log.e("picName", pic.toString());
                } catch (UnsupportedEncodingException e)
                {
                    Log.e("Exception", "UnsupportedEncodingException");
                    StorageReference picRefRe = storage.getReference().child(restaurant);
                    String picFull = picRefRe.toString()+"/"+ menukey+".png";
                    Log.e("picName", picFull);
                    pic = storage.getReferenceFromUrl(picUrl);
                }

                price = Integer.parseInt(dataSnapshot.child("price").getValue().toString());
                String strPrice = String.format("%,d", price);
                Log.e("last_pic", pic.toString());

                detailName.setText(dataSnapshot.getKey().toString());
                detailPrice.setText(strPrice);
                detailContent.setText(dataSnapshot.child("contents").getValue().toString());
                //detailRating, ratingValue
                Glide.with(MenuDetailActivity.this)
                        .using(new FirebaseImageLoader())
                        .load(pic)
                        .error(R.drawable.error)
                        .into(detailImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Error
            }
        });


        //리뷰의 평균값 계산하기
//        mDatabase.child(restaurant).child("review").child(menukey).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int sum=0;
//                int count= 0;
//                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
//                    count++;
//                    sum+= Integer.parseInt(postSnapshot.child("avg").getValue().toString());
//                    Log.e("avg: ", postSnapshot.child("avg").getValue().toString());
//                }
//                if(count==0)
//                    rate=0;
//                else
//                    rate = sum/count;
//
//                RatingBar detailRating = (RatingBar)findViewById(R.id.detail_rating);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuDetailActivity.this, ReviewActivity.class);
                intent.putExtra("restaurant",restaurant);
                intent.putExtra("selectedMenu", menukey);

                MenuDetailActivity.this.startActivity(intent);
            }
        });

    }

    //장바구니 버튼 클릭리스너
    public void btnCart_detail(View v)
    {
        //메뉴 이름과 가격정보를 intent에 담고 수량 조절 팝업 호출
        //finish();
        Intent intent = new Intent(MenuDetailActivity.this, NumControlPopup.class);

        intent.putExtra("MenuKey", MenuKey);
        intent.putExtra("price", ""+price);
        startActivity(intent);
    }

    //액션바의 버튼 기능
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            //액션 바의 뒤로 가기 버튼 기능
            Intent intent = new Intent(MenuDetailActivity.this, MenuActivity.class);
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
        Intent intent = new Intent(MenuDetailActivity.this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onBackPressed();
    }
}
