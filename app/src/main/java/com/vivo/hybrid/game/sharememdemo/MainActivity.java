package com.vivo.hybrid.game.sharememdemo;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.vivo.hybrid.game.sharememdemo.client.MemFetchClient;
import com.vivo.hybrid.game.sharememdemo.remote.MemFetchService;
import com.vivo.hybrid.game.sharememdemo.utils.BitmapUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MemFetchClient client;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = findViewById(R.id.iv);
        findViewById(R.id.btn_send_msg).setOnClickListener(this);
        findViewById(R.id.btn_take_snapshot).setOnClickListener(this);
        findViewById(R.id.btn_take_snapshot2).setOnClickListener(this);
        findViewById(R.id.btn_take_snapshot3).setOnClickListener(this);
        client = MemFetchClient.getInstance();

        bind();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_send_msg) {
            client.registerMsgCallback(new MemFetchClient.OnMessageCb() {
                @Override
                public void callback(String json) {
                    Toast.makeText(MainActivity.this, json, Toast.LENGTH_LONG).show();
                }
            });
            client.sendTestMessageForCallback();
        } else if (id == R.id.btn_take_snapshot) {

            Log.e("Gaudi","点击");

            client.takeSnapshot(new MemFetchClient.OnTakeSnapshotCb() {
                @Override
                public void callback(Bitmap bitmap) {
                    if (bitmap != null) {
                        iv.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(MainActivity.this, "bitmap is null", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else if (id == R.id.btn_take_snapshot2) {
            client.takeSnapshot2(new MemFetchClient.OnTakeSnapshotCb() {
                @Override
                public void callback(Bitmap bitmap) {
                    if (bitmap != null) {
                        iv.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(MainActivity.this, "bitmap is null", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else if (id == R.id.btn_take_snapshot3) {

            Log.e("Gaudi","点击3");

                client.takeSnapshot3(new MemFetchClient.OnTakeSnapshotCbBytes() {
                    @Override
                    public void callback(byte[] bytes) {

                        if (bytes != null) {
                            iv.setImageBitmap(byteToBitmap(bytes));
                        } else {
                            Toast.makeText(MainActivity.this, "bitmap is null", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
    }


    public static Bitmap byteToBitmap(byte[] imgByte) {
        InputStream input = null;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        input = new ByteArrayInputStream(imgByte);
        SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(
                input, null, options));
        bitmap = (Bitmap) softRef.get();
        if (imgByte != null) {
            imgByte = null;
        }
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        unbind();
        super.onDestroy();
    }

    private void bind() {
        Intent intent = new Intent(MainActivity.this, MemFetchService.class);
        bindService(intent, client.getClientConnection(), Service.BIND_AUTO_CREATE);
    }

    private void unbind() {
        unbindService(client.getClientConnection());
    }


}
