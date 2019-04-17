package edu.scse.take;

public class ItemBeanMessage { //消息
    public int portraitRes; //头像
    public String time; //接收消息时间
    public String username; //用户名
    public String location; //地点
    public String content; //消息内容

    public ItemBeanMessage(int portraitRes, String time, String username, String location, String content) {
        this.portraitRes = portraitRes;
        this.time = time;
        this.username = username;
        this.location = location;
        this.content = content;
    }
}
