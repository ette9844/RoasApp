package com.demo.project.roas.demo2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ReviewListAdapter extends BaseAdapter {

    Context context;
    private List<Review> list;

    public ReviewListAdapter(Context context, List<Review> list){
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Firebase Storage 참조
        FirebaseStorage storage = FirebaseStorage.getInstance();

        View v = View.inflate(context, R.layout.reviewlist, null);

        ImageView image = (ImageView) v.findViewById(R.id.image);
        TextView idText = (TextView) v.findViewById(R.id.idText);
        TextView content = (TextView) v.findViewById(R.id.content);
        TextView score = (TextView) v.findViewById(R.id.score);
        TextView dateText = (TextView) v.findViewById(R.id.dateText);
        RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);

        Drawable stars = ratingBar.getProgressDrawable();
        stars.setTint(ContextCompat.getColor(context, R.color.colorAccent));
        ratingBar.setRating(list.get(position).getAvg());

        idText.setText(list.get(position).getId());
        content.setText(list.get(position).getContent());

        String date = list.get(position).getDate();
        String subDate = date.substring(2,4) + "." + date.substring(4,6) + "." + date.substring(6,8);
        Log.d("test", subDate);
        dateText.setText(subDate);


        //Firebase Storage에 접근한뒤 Gilde로 이미지를 가져와서 review에 넣는다.
        String path = ReviewActivity.restaurant+"/"+ReviewActivity.selectedMenu+"/"+list.get(position).getDate()+".png";
        StorageReference storageReference = storage.getReference(path);

        //ImageView imageView;
        Glide.with(v.getContext())
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .error(R.drawable.error)
                .into(image);
        score.setText("평점: "+list.get(position).getAvg()+".0");
        return v;
    }
}
