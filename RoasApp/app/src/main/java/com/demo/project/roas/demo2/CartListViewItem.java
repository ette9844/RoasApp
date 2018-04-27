package com.demo.project.roas.demo2;

import com.google.firebase.storage.StorageReference;

/**
 * Created by pc on 2017-10-21.
 */

public class CartListViewItem
{
    private StorageReference cartImage;     //메뉴 이미지
    private String cartName;        //메뉴 이름
    private int cartPrice;       //메뉴 가격
    private int cartNum;            //메뉴 개수
    private int cartSumPrice;       //메뉴 개수 * 메뉴 가격

    public void setCartImage(StorageReference image) { cartImage = image; }

    public void setCartName(String name) { cartName = name; }

    public void setCartPrice(int price)
    {
        cartPrice = price;
    }

    public void setCartNum(int number) { cartNum = number; }

    public void setCartSumPrice() {  cartSumPrice = cartPrice * cartNum;  }

    public StorageReference getCartImage()
    {
        return this.cartImage;
    }

    public String getCartName()
    {
        return this.cartName;
    }

    public int getCartPrice()
    {
        return this.cartPrice;
    }

    public int getCartNum() { return this.cartNum; }

    public int getCartSumPrice()
    {
        cartSumPrice = cartPrice * cartNum;
        return cartSumPrice;
    }

    public void decrease()
    {
        if(cartNum == 1)
            return;
        else
            cartNum--;
    }

    public void increase() { cartNum++; }
}