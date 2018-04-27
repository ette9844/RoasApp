package com.demo.project.roas.demo2;

/**
 * Created by pc on 2017-11-13.
 */

public class OrderListViewItem
{
    private String orderName;        //메뉴 이름
    private int orderNum;            //메뉴 개수
    private int orderSumPrice;      //메뉴 개수 * 메뉴 가격

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public void setOrderSumPrice(int price) {
        this.orderSumPrice = price * this.orderNum;
    }

    public String getOrderName() {
        return orderName;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public int getOrderSumPrice() {
        return orderSumPrice;
    }

}
