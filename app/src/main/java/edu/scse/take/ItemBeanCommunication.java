package edu.scse.take;

public class ItemBeanCommunication {//动态项目
    public int portraitRes; //头像
    public int imageResId; //图片
    public String time; //发布时间
    public String username; //用户名
    public String intro; //内容
    public String title; //没用，忽略掉

    public ItemBeanCommunication(int portraitRes, int imageResId, String time, String username, String intro, String title) {
        this.portraitRes = portraitRes;
        this.imageResId = imageResId;
        this.time = time;
        this.username = username;
        this.intro = intro;
        this.title = title;
    }


}
