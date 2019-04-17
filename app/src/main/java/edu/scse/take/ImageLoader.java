package edu.scse.take;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 7/11/2017.
 */

public class ImageLoader {

    private LruCache<String,Bitmap> cache;

    public ImageLoader(){
        long maxMemory=Runtime.getRuntime().maxMemory();
        long cacheSize=maxMemory/4;
        cache=new LruCache<String, Bitmap>((int) cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    private void addBitmapToCache(String key, Bitmap value){
        if (getBitmapFromCache(key)==null){
            cache.put(key,value);
        }
    }

    private Bitmap getBitmapFromCache(String key){
        return cache.get(key);
    }

    public void showImageByAsyncTask(ImageView imageView, final String url){
        Bitmap bitmap=getBitmapFromCache(url);
        if (bitmap==null){
            new LoadImageTask(imageView,url).execute(url);
        }else {
            imageView.setImageBitmap(bitmap);
        }
    }

    class LoadImageTask extends AsyncTask<String,Void,Bitmap> {

        private ImageView imageView;
        private String url;
        LoadImageTask(ImageView imageView, String url){
            this.imageView=imageView;
            this.url=url;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            String url=strings[0];
            Bitmap bitmap=loadImage(url);
            if (bitmap!=null){
                addBitmapToCache(url,bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (imageView.getTag().equals(url)){
                if(bitmap!=null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
    private Bitmap loadImage(String urlString){
        Bitmap bitmap=null;
        InputStream is=null;
        try {
            URL url=new URL(urlString);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            is=new BufferedInputStream(connection.getInputStream());
            bitmap=BitmapFactory.decodeStream(is);
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (is!=null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}



