package com.demo.project.roas.demo2;

/**
 * Created by pc on 2017-10-21.
 */

public class Cart
{
    private String menu;
    private int num;
    private int price;

    Cart()
    {
        //생성자
        menu = "--";
        num = 0;
        price = 0;
    }

    Cart(String menu, int num, int price)
    {
        this.menu = menu;
        this.num = num;
        this.price = price;
    }

    Cart(Cart c)
    {
        //복사 생성자
        this.menu = c.getMenu();
        this.num = c.getNum();
        this.price = c.getPrice();
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getMenu() {
        return menu;
    }

    public int getNum() {
        return num;
    }

    public int getPrice() {
        return price;
    }

    //수정사항 - increase decrease추가, price변수 추가 getset함수 추가, 생성자 추가 및 변경
    public void decrease()
    {
        if(num == 1)
        {
            num = 1;
        }
        else
            this.num--;

    }

    public void increase() { this.num++; }
}
