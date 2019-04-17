package edu.scse.take;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class BackgroundSrc {
    public static Bitmap backgroundOrignal;
    public static Bitmap background;
    public static BitmapDrawable bitmapDrawable;
    public static void setBackground(Bitmap bitmap,boolean blur){
        backgroundOrignal=bitmap;
        if(blur){
            background=FastBlur.fastblur(bitmap,18);
            bitmapDrawable = new BitmapDrawable(background);
        }else{
            background=bitmap;
            bitmapDrawable = new BitmapDrawable(background);
        }

    }
}
