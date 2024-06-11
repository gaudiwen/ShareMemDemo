package com.vivo.hybrid.game.sharememdemo.remote;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.vivo.hybrid.game.sharememdemo.R;
import com.vivo.hybrid.game.sharememdemo.utils.BitmapUtil;

import java.io.IOException;
import java.io.InputStream;

public class MemFetchService extends Service implements RemoteContract.FromClient {
    private static final String TAG = MemFetchService.class.getSimpleName();

    private RemoteContract.ToClient toClient;

    public MemFetchService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        IBinder iBinder = new MemFetchStub(this);
        if (iBinder instanceof RemoteContract.ToClient) {
            toClient = (RemoteContract.ToClient) iBinder;
        }
        return iBinder;
    }

    @Override
    public void fromClientMessage(String json) {
        Log.d(TAG, "fromClientMessage: " + json);
        if (toClient == null) {
            return;
        }
        if ("takeSnapshot".endsWith(json)) {

            Log.e("Gaudi","111");
            Bitmap bitmap = takeSnapshot();
            Log.e("Gaudi","222");

            if (bitmap != null) {
                toClient.onSnapshotCallback(bitmap);
            }
        } if ("takeSnapshot2".endsWith(json)) {
            Bitmap bitmap = takeSnapshot2();
            if (bitmap != null) {
                toClient.onSnapshotCallback(bitmap);
            }
        } else if ("getMessage".endsWith(json)) {
            String message = getServiceMessage();
            if (!TextUtils.isEmpty(message)) {
                toClient.sendToClientMessage(message);
            }
        }else if ("takeSnapshot3".endsWith(json)) {

            byte[] takeSnapshot3 = takeSnapshot3();
            toClient.onSnapshotCallbackMyBytes(takeSnapshot3);
        }
    }

    private Bitmap takeSnapshot() {

        return BitmapFactory.decodeResource(getResources(), R.drawable.tt);
    }

    private Bitmap takeSnapshot2() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.aa);
    }

    private byte[] takeSnapshot3() {

        InputStream open;
        try{
            open = getResources().getAssets().open("IMG.JPG");
            byte[] buffer = new byte[open.available()];
            open.read(buffer);
            return buffer;
        }catch (Exception e){

            e.printStackTrace();

            return new byte[1024*1024];
        }finally {

        }
    }

    private String getServiceMessage() {
        return "from service message";
    }

}
