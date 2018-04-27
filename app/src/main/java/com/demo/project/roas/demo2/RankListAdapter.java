package com.demo.project.roas.demo2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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

/**
 * Created by is on 2017-10-07.
 */

public class RankListAdapter extends BaseAdapter {

    Context context;
    private List<Rank> list;

    public RankListAdapter(Context context, List<Rank> list){
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
        FirebaseStorage storage = FirebaseStorage.getInstance();

        View v = View.inflate(context, R.layout.rank, null);

        ImageView image = (ImageView) v.findViewById(R.id.image);
        ImageView rankImage = (ImageView) v.findViewById(R.id.rankImage);
        TextView menuText = (TextView) v.findViewById(R.id.menuText);
        TextView price = (TextView) v.findViewById(R.id.price);
        TextView contents = (TextView) v.findViewById(R.id.contents);
        TextView score = (TextView) v.findViewById(R.id.score);
        RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);

        Drawable stars = ratingBar.getProgressDrawable();
        stars.setTint(ContextCompat.getColor(context, R.color.colorAccent));
        ratingBar.setRating(list.get(position).getRate());

        menuText.setText(list.get(position).getMenu());
        contents.setText(list.get(position).getContents());
        price.setText(list.get(position).getPrice()+" 원");

        switch (list.get(position).getRank()){
            case 1:
                rankImage.setImageResource(R.drawable.one);
                break;
            case 2:
                rankImage.setImageResource(R.drawable.two);
                break;
            case 3:
                rankImage.setImageResource(R.drawable.three);
                break;
            case 4:
                rankImage.setImageResource(R.drawable.four);
                break;
            case 5:
                rankImage.setImageResource(R.drawable.five);
                break;
            case 6:
                rankImage.setImageResource(R.drawable.six);
                break;
            case 7:
                rankImage.setImageResource(R.drawable.seven);
                break;
            case 8:
                rankImage.setImageResource(R.drawable.eight);
                break;
            case 9:
                rankImage.setImageResource(R.drawable.nine);
                break;
            case 10:
                rankImage.setImageResource(R.drawable.ten);
                break;
        }

        //Firebase Storage에 접근한뒤 Gilde로 이미지를 가져와서 review에 넣는다.
        String path = RankList.restaurant+"/"+list.get(position).getMenu()+".png";
                                                    //ReviewActivity.selectedMenu+".png";
        StorageReference storageReference = storage.getReference(path);

        //ImageView imageView;
        Glide.with(v.getContext())
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .error(R.drawable.error)
                .into(image);
        score.setText("평점: "+list.get(position).getRate()+".0");

        return v;
    }
}
