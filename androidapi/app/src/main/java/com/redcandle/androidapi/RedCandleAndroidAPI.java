package com.redcandle.androidapi;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;


import com.coremedia.iso.PropertyBoxParserImpl;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.unity3d.player.UnityPlayerActivity;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class RedCandleAndroidAPI extends UnityPlayerActivity {

    public String ShareVideo(String path) {
       File file = new File(path);

        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.TITLE, "My video title");
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, file.getAbsolutePath());
        Uri data = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Title");
        intent.setType("video/mp4");
        intent.putExtra(Intent.EXTRA_STREAM, data);
        startActivity(Intent.createChooser(intent, "Upload video via:"));

        Log.d("Unity", "ShareVideo: success");

        return "";
    }

    public String MergeVideo(String video,String audio,String target)
    {
        PropertyBoxParserImpl._activity = this;

        Log.d("Unity", "Try Merge: " + video + ":" + audio + ":" + target);

        Boolean success = mux(video,audio,target);


        String result = target;

        if(success)
        {
            result = target;
            Log.d("Unity", "Merge Success");
        }
        else
        {
            result = video;
            Log.d("Unity", "Merge Failed");
        }


        File file = new File(result);



        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.TITLE, "My video title");
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, file.getAbsolutePath());
        Uri data = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Title");
        intent.setType("video/mp4");
        intent.putExtra(Intent.EXTRA_STREAM, data);
        startActivity(Intent.createChooser(intent, "Upload video via:"));


        return "";
    }

    public boolean mux(String videoFile, String audioFile, String outputFile) {

        Movie video;
        try {
            video = new MovieCreator().build(videoFile);
            Log.d("ATTENTION","On a fini le try du montage de video");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        Movie audio;
        try {
            audio = new MovieCreator().build(audioFile);
            Log.d("ATTENTION","On a fini le try de montage de audio");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }

        Log.d("TAILLE",String.valueOf(video.getTracks().size()));
        Track audioTrack = audio.getTracks().get(0);

        Track videoAudioTrack = video.getTracks().get(1);
        Movie result = new Movie();

        result.addTrack(audioTrack);
        result.addTrack(videoAudioTrack);


        Container out = new DefaultMp4Builder().build(result);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        BufferedWritableFileByteChannel byteBufferByteChannel = new BufferedWritableFileByteChannel(fos);
        try {
            out.writeContainer(byteBufferByteChannel);
            byteBufferByteChannel.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Log.d("ATTENTION","On a finis la conversion");
        return true;
    }

    private static class BufferedWritableFileByteChannel implements WritableByteChannel {
        private static final int BUFFER_CAPACITY = 1000000;

        private boolean isOpen = true;
        private final OutputStream outputStream;
        private final ByteBuffer byteBuffer;
        private final byte[] rawBuffer = new byte[BUFFER_CAPACITY];

        private BufferedWritableFileByteChannel(OutputStream outputStream) {
            this.outputStream = outputStream;
            this.byteBuffer = ByteBuffer.wrap(rawBuffer);
        }

        @Override
        public int write(ByteBuffer inputBuffer) throws IOException {
            int inputBytes = inputBuffer.remaining();

            if (inputBytes > byteBuffer.remaining()) {
                dumpToFile();
                byteBuffer.clear();

                if (inputBytes > byteBuffer.remaining()) {
                    throw new BufferOverflowException();
                }
            }

            byteBuffer.put(inputBuffer);

            return inputBytes;
        }

        @Override
        public boolean isOpen() {
            return isOpen;
        }

        @Override
        public void close() throws IOException {
            dumpToFile();
            isOpen = false;
        }
        private void dumpToFile() {
            try {
                outputStream.write(rawBuffer, 0, byteBuffer.position());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
