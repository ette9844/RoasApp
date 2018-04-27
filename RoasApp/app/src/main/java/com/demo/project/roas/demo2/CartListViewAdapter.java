package com.demo.project.roas.demo2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by pc on 2017-10-21.
 */

public class CartListViewAdapter extends BaseAdapter
{
    // adapter에 추가된 데이터를 저장하기 위한 arraylist
    private ArrayList<CartListViewItem> cartListViewItemList = new ArrayList<CartListViewItem>();

    //onclick을 위한 변수들
    private Context context;
    private ListItemClickHelp callback;
    //private LayoutInflater mInflater;

    // CartListViewAdapter의 생성자
    public CartListViewAdapter()
    {

    }
    //생성자2
    public CartListViewAdapter(Context context, ArrayList<CartListViewItem> list, ListItemClickHelp callback)
    {
        this.context = context;
        this.cartListViewItemList = list;
        this.callback = callback;
    }

    // adapter에 사용되는 데이터 개수를 리턴 : 필수
    @Override
    public int getCount(){
        return cartListViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 view를 리턴 : 필수
    public View getView(int position, View convertView, final ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cartlistview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView cartImageView = (ImageView) convertView.findViewById(R.id.cartImage);
        TextView cartNameView = (TextView) convertView.findViewById(R.id.cartName);
        TextView cartPriceView = (TextView) convertView.findViewById(R.id.cartPrice);
        TextView cartNum = (TextView) convertView.findViewById(R.id.cartNum);
        TextView cartSumView = (TextView) convertView.findViewById(R.id.cartSumPrice);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final CartListViewItem cartListViewItem = cartListViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        // 사진 데이터 반영
        Glide.with(context).using(new FirebaseImageLoader())
                .load(cartListViewItem.getCartImage())
                .into(cartImageView);
        // 메뉴 이름 반영
        cartNameView.setText(cartListViewItem.getCartName());
        // 수정사항
        // 가격 정보 반영
        String price = String.format("%,d", cartListViewItem.getCartPrice());
        cartPriceView.setText(price);
        // 메뉴 개수 반영
        cartNum.setText(Integer.toString(cartListViewItem.getCartNum()));
        // 메뉴 총합 반영
        String priceSum = String.format("%,d", cartListViewItem.getCartSumPrice());
        cartSumView.setText(priceSum);

        //가격 총합 반영
        String totalSum = String.format("%,d", getTotalSum());


        //버튼 오버라이드들을 위한 변수들
        final View view = convertView;
        final int p = position;
        TextView deleteButton = (TextView) convertView.findViewById(R.id.deleteButton);
        final int one = deleteButton.getId();
        //삭제 버튼 onclick
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.d("button", "delete버튼 onclick내부");
                callback.onClick(view, parent, p, one);
            }
        });


        //- 버튼 onclick
        Button minusButton = (Button) convertView.findViewById(R.id.minusButton);
        final int two = minusButton.getId();
        minusButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                callback.onClick(view, parent, p, two);
            }
        });

        //+ 버튼 onclick
        Button plusButton = (Button) convertView.findViewById(R.id.plusButton);
        final int three = plusButton.getId();
        plusButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                callback.onClick(view, parent, p, three);
            }
        });


        return convertView;
    }

    //adapter refresh
    public synchronized void refreshAdapter(ArrayList<CartListViewItem> items)
    {
        ArrayList<CartListViewItem> tmp = new ArrayList<CartListViewItem>();
        tmp.addAll(items);
        cartListViewItemList.clear();
        cartListViewItemList.addAll(tmp);
        notifyDataSetChanged();
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
        return cartListViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(StorageReference image, String name, int price, int num)
    {
        CartListViewItem item = new CartListViewItem();

        item.setCartImage(image);
        item.setCartName(name);
        item.setCartPrice(price);
        item.setCartNum(num);
        item.setCartSumPrice();

        cartListViewItemList.add(item);
    }

    //리스트의 아이템들의 가격 총합을 구하는 함수
    public int getTotalSum()
    {
        int sum = 0;
        for(CartListViewItem c : cartListViewItemList)
        {
            sum += c.getCartNum() * c.getCartPrice();
        }
        return sum;
    }

}
