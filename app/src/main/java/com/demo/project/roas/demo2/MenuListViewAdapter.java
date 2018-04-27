package com.demo.project.roas.demo2;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by pc on 2017-10-09.
 */

public class MenuListViewAdapter extends BaseAdapter
{
    // adapter에 추가된 데이터를 저장하기 위한 arraylist
    private ArrayList<MenuListViewItem> menuListViewItemList = new ArrayList<MenuListViewItem>();

    // MenuListViewAdapter의 생성자
    public MenuListViewAdapter()
    {

    }

    // adapter에 사용되는 데이터 개수를 리턴 : 필수
    @Override
    public int getCount(){
        return menuListViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 view를 리턴 : 필수
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.menulistview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView menuImageView = (ImageView) convertView.findViewById(R.id.menu_image) ;
        TextView menuNameView = (TextView) convertView.findViewById(R.id.menu_name) ;
        TextView menuPriceView = (TextView) convertView.findViewById(R.id.menu_price) ;
        RatingBar menuRatingBar = (RatingBar) convertView.findViewById(R.id.menu_rating);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        MenuListViewItem menuListViewItem = menuListViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        Glide.with(context).using(new FirebaseImageLoader())
                .load(menuListViewItem.getMenuImage())
                .error(R.drawable.error)
                .into(menuImageView);
        menuNameView.setText(menuListViewItem.getMenuName());
        menuPriceView.setText(menuListViewItem.getMenuPrice());
        menuRatingBar.setRating(menuListViewItem.getMenuRating());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position)
    {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position)
    {
        return menuListViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(StorageReference image, String name, String price, int rating)
    {
        MenuListViewItem item = new MenuListViewItem();

        item.setMenuImage(image);
        item.setMenuName(name);
        item.setMenuPrice(price);
        item.setMenuRating(rating);

        menuListViewItemList.add(item);
    }

}