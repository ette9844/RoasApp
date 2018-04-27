package com.demo.project.roas.demo2;

/**
 * Created by is on 2017-10-07.
 */

public class Rank {

    String menu;
    String contents;
    String count;
    String price;
    int rate;
    int rank;

    public Rank(String menu, String contents, String count, String price, int rank, int rate) {
        this.menu = menu;
        this.contents = contents;
        this.count = count;
        this.price = price;
        this.rank = rank;
        this.rate = rate;
    }
    public Rank(String contents, String count, String price){
        this.contents = contents;
        this.count = count;
        this.price = price;
    }

    public Rank() {}

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
