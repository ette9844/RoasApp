package com.demo.project.roas.demo2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ReviewDetail extends Activity {

    private String writer;
    private String content;
    private int star;
    private String date;
    private String pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        //Firebase Storage 참조
        FirebaseStorage storage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        writer = intent.getStringExtra("writer");
        content = intent.getStringExtra("content");
        date = intent.getStringExtra("date");
        String subDate = date.substring(2,4) + "." + date.substring(4,6) + "." + date.substring(6,8);
        this.star = intent.getIntExtra("star", 0);
        pw = intent.getStringExtra("pw");

        ImageView backButton = (ImageView) findViewById(R.id.backButton);
        TextView idText = (TextView) findViewById(R.id.idText);
        ImageView image = (ImageView) findViewById(R.id.image);
        TextView contentText = (TextView) findViewById(R.id.content);
        TextView score = (TextView) findViewById(R.id.score);
        TextView dateText = (TextView) findViewById(R.id.dateText);
        Button modifyButton = (Button) findViewById(R.id.modifyButton);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        ratingBar.setRating(star);

        //image.setImageResource(R.drawable.test);
        String path = ReviewActivity.restaurant+"/"+ReviewActivity.selectedMenu+"/"+date+".png";
        StorageReference storageReference = storage.getReference(path);

        Glide.with(getApplicationContext())
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .error(R.drawable.error)
                .into(image);

        idText.setText(writer + " 님의 리뷰");
        contentText.setText(content);
        dateText.setText(subDate + " 에 작성");
        score.setText(star+".0");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent popupIntent = new Intent(ReviewDetail.this, LoginPopup.class);
                popupIntent.putExtra("pw", pw);
                popupIntent.putExtra("writer", writer);
                popupIntent.putExtra("content", content);
                popupIntent.putExtra("date", date);
                popupIntent.putExtra("star", star);
                ReviewDetail.this.startActivity(popupIntent);
            }
        });
    }
}
