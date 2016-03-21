package com.redcandle.androidapi;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.unity3d.player.UnityPlayerActivity;

import java.io.File;

public class RedCandleAndroidAPI extends UnityPlayerActivity {

    public static String ShareVideo(String path) {
       /* File file = new File(path);

        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.TITLE, "My video title");
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, file.getAbsolutePath());
        Uri data = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Title");
        intent.setType("video/mp4");
        intent.putExtra(Intent.EXTRA_STREAM, data);
        startActivity(Intent.createChooser(intent, "Upload video via:"));*/

        Log.d("Unity", "ShareVideo: success");

        return "";
    }
}
