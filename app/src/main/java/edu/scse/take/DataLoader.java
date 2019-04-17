package edu.scse.take;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.util.Base64;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MyLocationData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class DataLoader {

    public static Locations locations=Locations.DEFAULT;
    public static MyLocationData locData;
    public static String username;
    public static boolean receiving=false;

    public static void zanMinus(String username, String time) {
        String urlstr= SERVER+"zan_minus?";
        urlstr+="username="+username;
        urlstr+="&time="+time;
        String resultStr="";
        try {
            URL url=new URL(urlstr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            resultStr=br.readLine();
            br.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location != null) {
                locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        .direction(location.getDirection())
                        .latitude(location.getLatitude())// 此处设置开发者获取到的方向信息，顺时针0-360
                        .longitude(location.getLongitude())
                        .build();
                locations= IMWhere(location.getLongitude(), location.getLatitude());
            }
        }
    }


    private static final String SERVER="http://192.168.43.145:8000/";
    public static List<ItemBeanCommunication> loadCommunicate(){
        String urlstr= SERVER+"load_commu?load=1";
        String result="";
        try {
            URL url=new URL(urlstr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            result=br.readLine();
            br.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<ItemBeanCommunication> list=new ArrayList<>();
        JSONObject jsonObject;
        Iterator<String> it;
        try {
            //TODO 不联网的时候把这里改成jsonStr
            jsonObject = new JSONObject(result);
            it=jsonObject.keys();
            while(it.hasNext()){
                String key = it.next();
                JSONObject value = null;
                try {
                    value = jsonObject.getJSONObject(key);
                    list.add(new ItemBeanCommunication(R.drawable.ss, R.drawable.bg,
                            value.getString("time"),value.getString("userName"),
                            value.getString("content")," ",Integer.parseInt(value.getString("zan"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void zanPlus(String username, String time){
        String urlstr= SERVER+"zan_plus?";
        urlstr+="username="+username;
        urlstr+="&time="+time;
        String resultStr="";
        try {
            URL url=new URL(urlstr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            resultStr=br.readLine();
            br.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ItemBeanMessage> loadDelegateByLocation(String location){
        String urlstr= SERVER+"load_delegate_by_location?location="+location;
        String result="";
        List<ItemBeanMessage> list=new ArrayList<>();
        try {
            URL url=new URL(urlstr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            result+=br.readLine();

            br.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject=null;
        Iterator<String> it=null;
        try {
            //TODO 不联网的时候把这里改成jsonStr
            jsonObject = new JSONObject(result);
            it=jsonObject.keys();
            while(it.hasNext()){
                String key = it.next();
                JSONObject value = null;
                try {
                    value = jsonObject.getJSONObject(key);
                    list.add(new ItemBeanMessage(R.drawable.ss, value.getString("time"),
                            value.getString("userName"),value.getString("lat"),value.getString("content")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<ItemBeanMessage> loadDelegate(String username){
        String urlstr= SERVER+"load_delegate?username="+username;
        String result="";
        String jsonStr="{\"0\": {\"userName\": \"jenkin\", \"time\": \"201903060858\", \"lng\": \"155.223\", \"lat\": \"123.221\", \"content\": \"this_is_a_coent\"}, \"1\": {\"userName\": \"jenkin\", \"time\": \"201903070858\", \"lng\": \"155.223\", \"lat\": \"123.221\", \"content\": \"this_is_a_coent\"}, \"2\": {\"userName\": \"jenkin\", \"time\": \"201905120858\", \"lng\": \"155.223\", \"lat\": \"123.221\", \"content\": \"this_is_a_coent\"}, \"3\": {\"userName\": \"jenkin\", \"time\": \"201905120858\", \"lng\": \"155.223\", \"lat\": \"123.221\", \"content\": \"this_is_a_coent\"}, \"4\": {\"userName\": \"jenkin\", \"time\": \"201905120858\", \"lng\": \"155.223\", \"lat\": \"123.221\", \"content\": \"this_is_a_coent\"}}";
        List<ItemBeanMessage> list=new ArrayList<>();
        try {
            URL url=new URL(urlstr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            result=br.readLine();
            br.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject=null;
        Iterator<String> it=null;
        try {
            //TODO 不联网的时候把这里改成jsonStr
            jsonObject = new JSONObject(result);
            it=jsonObject.keys();
            while(it.hasNext()){
                String key = it.next();
                JSONObject value = null;
                try {
                    value = jsonObject.getJSONObject(key);
                    list.add(new ItemBeanMessage(R.drawable.ss, value.getString("time"),
                            value.getString("userName"),value.getString("lng"),value.getString("content")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static String register(String username,String password,String stuid,String realname){
        String urlstr= SERVER+"register?";
        urlstr+="username="+username;
        urlstr+="&password="+password;
        urlstr+="&stuid="+stuid;
        urlstr+="&realname="+realname;
        String resultStr="";
        try {
            URL url=new URL(urlstr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            resultStr=br.readLine();
            br.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultStr;
    }


    public static String newMessageOccurs(){
        String urlstr= SERVER+"msg_after?";
        urlstr+="location="+locations.name;
        String resultStr="";
        try {
            URL url=new URL(urlstr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            resultStr=br.readLine();
            br.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultStr;
    }

    public static boolean delDelegate(String username,String time){
        String urlstr= SERVER+"del_delegate?";

        urlstr+="username="+username;
        urlstr+="&time="+time;
        String resultStr="";
        try {
            URL url=new URL(urlstr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            resultStr=br.readLine();
            br.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultStr.equals("OK");
    }

    public static boolean newDelegate(String username,String time,String lat,String lng,String content){
        String urlstr= SERVER+"new_delegate?";
        urlstr+="username="+username;
        urlstr+="&time="+time;
        urlstr+="&lat="+lat;
        urlstr+="&lng="+lng;
        urlstr+="&content="+content;
        boolean succeed=false;
        try {
            URL url=new URL(urlstr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result=br.readLine();
            succeed=(result.equals("OK"));
            br.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return succeed;
    }
    public static boolean newCommunicate(String username,String time,String lat,String lng,String content){
        String urlstr= SERVER+"pub_commu?";
        urlstr+="username="+username;
        urlstr+="&time="+time;
        urlstr+="&lat="+lat;
        urlstr+="&lng="+lng;
        urlstr+="&content="+content;
        boolean succeed=false;
        try {
            URL url=new URL(urlstr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result=br.readLine();
            succeed=(result.equals("OK"));
            br.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return succeed;
    }
    public static boolean login(String username,String password){
        String urlstr= SERVER+"login?";
        urlstr+="username="+username;
        boolean succeed=false;
        try {
            URL url=new URL(urlstr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result=br.readLine();
            if(result==null){
                return false;
            }
            String pwdStr="{'pasword': '"+password+"'}";
            succeed=(result.equals(pwdStr));
            br.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return succeed;
    }
    public static Locations IMWhere(double lat,double lng){
        Locations loc=Locations.DEFAULT;
        double minDistance=1000.0;
        for(Locations l:Locations.values()){
            double distance=Math.pow(l.lat-lat,2)+Math.pow(l.lng-lng,2);
            if(distance<minDistance){
                minDistance=distance;
                loc=l;
            }
        }
        return loc;
    }
    public static void join(Locations locations){
        String joinStr=SERVER+"join?location="+locations.name;
        boolean succeed=false;
        try {
            URL url=new URL(joinStr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result=br.readLine();
            System.out.println(result);
            succeed=(result.equals("OK"));
            br.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void leave(Locations locations){
        String joinStr=SERVER+"leave?location="+locations.name;
        boolean succeed=false;
        try {
            URL url=new URL(joinStr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result=br.readLine();
            System.out.println(result);
            succeed=(result.equals("OK"));
            br.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int propleNum(Locations locations){
        String joinStr=SERVER+"peoplenum?location="+locations.name;
        int num=0;
        try {
            URL url=new URL(joinStr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result=br.readLine();
            System.out.println(result);
            num=Integer.valueOf(result);
            br.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return num;
    }

//////////////////////////////////

    private RequestParams params = new RequestParams();

    public void encodeImagetoString(final String imgPath) {
        new AsyncTask<Void, Void, String>() {
            Bitmap bitmap;
            String encodedString;
            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(imgPath,
                        options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // 压缩图片
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Base64图片转码为String
                encodedString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                // 将转换后的图片添加到上传的参数中
                params.put("image", encodedString);
                params.put("filename", "123");
                // 上传图片
                 String url = SERVER+":8000/upload";
                        com.loopj.android.http.AsyncHttpClient client = new AsyncHttpClient();
                        client.post(url, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            }
                        });
            }
        }.execute(null, null, null);
    }
    public static Bitmap getURLimage(String imgName) {
        String url="192.168.43.145:8080/media/img/"+imgName;
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }
    /**
     * 文件上传
     * @param files:需上传的文件集合
     * @return
     * @throws IOException
     */
    public static String upLoadFilePost( Map<String, File> files) throws IOException {
        String actionUrl=SERVER+"upload";
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        URL uri = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(5 * 1000);
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false);
        conn.setRequestMethod("POST"); // Post方式
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                + ";boundary=" + BOUNDARY);

        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        // 发送文件数据
        if (files != null)
            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                        + file.getKey() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());
                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }

                is.close();
                outStream.write(LINEND.getBytes());
            }

        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();

        // 得到响应码
        int res = conn.getResponseCode();
        if (res == 200) {
            InputStream in = conn.getInputStream();
            InputStreamReader isReader = new InputStreamReader(in);
            BufferedReader bufReader = new BufferedReader(isReader);
            String line = "";
            String data = "";
            while ((line = bufReader.readLine()) != null) {
                data += line;
            }
            outStream.close();
            conn.disconnect();
            return data;
        }
        outStream.close();
        conn.disconnect();
        return "";
    }
}
