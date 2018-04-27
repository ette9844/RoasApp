package com.demo.project.roas.demo2;

/**
 * Created by pc on 2017-10-09.
 */

import android.graphics.drawable.Drawable;

import com.google.firebase.storage.StorageReference;

import java.util.StringTokenizer;

//메뉴 리스트뷰 아이템 데이터에 대한 클래스
public class MenuListViewItem {
    private StorageReference menuImage;     //메뉴 이미지
    private String menuName;        //메뉴 이름
    private String menuPrice;       //메뉴 가격
    private int menuRating;             //평균 평점을 받아와서 저장하는 변수, 차후 이 값을 ratingBar에 적용해서 출력할 예정

    public void setMenuImage(StorageReference image)
    {
        menuImage = image;
    }

    public void setMenuName(String name) { menuName = name; }

    public void setMenuPrice(String price)
    {
        menuPrice = price;
    }

    public void setMenuRating(int rating) { menuRating = rating; }

    public StorageReference getMenuImage()
    {
        return this.menuImage;
    }

    public String getMenuName()
    {
        return this.menuName;
    }

    public String getMenuPrice()
    {
        return this.menuPrice;
    }

    public int getMenuRating() { return this.menuRating; }
}
