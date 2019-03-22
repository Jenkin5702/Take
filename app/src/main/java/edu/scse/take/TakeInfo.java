package edu.scse.take;

public class TakeInfo {
    private String contact; //联系方式
    private String name; //联系人姓名
    private double price; //价格
    private String place; //地点
    private String comment; //备注

    public TakeInfo(String contact, String name, double price, String place, String comment) {
        this.contact = contact;
        this.name = name;
        this.price = price;
        this.place = place;
        this.comment = comment;
    }

    public String getContact() {
        return contact;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getPlace() {
        return place;
    }

    public String getComment() {
        return comment;
    }
}
