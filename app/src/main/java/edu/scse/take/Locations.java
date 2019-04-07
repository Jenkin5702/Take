package edu.scse.take;

public enum Locations {
    JIAOXUELOU(117.066185,39.242539, "教学楼"),
    TUSHUGUAN (117.065637,39.244551, "图书馆"),
    DAHUO (117.062502,39.246004, "大活"),
    XIQUSUSHE (117.057849,39.248016, "西区宿舍"),
    DONGQUSUSHE (117.073228,39.247199, "东区宿舍"),
    DONGQUSHITANG (117.071521,39.245508, "东区食堂"),
    SHIYANLOU (117.065053,39.240786, "实验楼"),
    XIJIAOXUEYUANLOU(117.06349,39.239962, "西教学院楼"),
    HENGSHUNGUANGCHANG(117.072006,39.240073, "亨顺广场"),
    XIQUSHITANG (117.059538,39.245606, "西区食堂"),
    JIAOSHIGONGYU365 (117.056609,39.242113, "教师公寓365"),
    XIAOYIYUAN (117.056232,39.24569, "校医院"),
    XIQULANQIUCHANG (117.062215,39.247841, "西区篮球场"),
    TIYUZHONGXIN(117.065502,39.247087, "体育中心"),
    XINGZHENGLOU (117.068629,39.241331, "行政楼"),
    DEFAULT(0,0,"位置不明");


    public double lng;
    public double lat;
    public String name;

    Locations(double lat,double lng,String name){
        this.lng=lng;
        this.lat=lat;
        this.name=name;
    }



}
