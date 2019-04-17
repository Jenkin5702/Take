package edu.scse.take;

public class ItemBeanRecord { //委托记录
    public int portraitRes; //头像
    public String time; //时间
    public String username; //用户名
    public String location; //地点
    public String content; //委托内容

    public ItemBeanRecord(int portraitRes, String time, String username, String location, String content) {
        this.portraitRes = portraitRes;
        this.time = time;
        this.username = username;
        this.location = location;
        this.content = content;
    }
}
