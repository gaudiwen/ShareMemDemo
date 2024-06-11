package com.vivo.hybrid.game.sharememdemo.client;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import com.vivo.hybrid.game.sharememdemo.IMemAIDL;
import com.vivo.hybrid.game.sharememdemo.IMemCallback;
import com.vivo.hybrid.game.sharememdemo.IMsgCallback;
import com.vivo.hybrid.game.sharememdemo.utils.BitmapUtil;
import com.vivo.hybrid.game.sharememdemo.utils.MemFileUtils;

import java.io.IOException;

import static android.system.OsConstants.PROT_READ;
import static android.system.OsConstants.PROT_WRITE;

/**
 * [description]
 * author: yifei
 * created at 2019/8/10 下午8:38
 */
public class MemFetchClient {
    private static final String TAG = MemFetchClient.class.getSimpleName();

    private static final MemFetchClient ourInstance = new MemFetchClient();

    public static MemFetchClient getInstance() {
        return ourInstance;
    }

    private MemFetchClient() {
    }

    private IMemAIDL iMemoStub;
    private ServiceConnection clientConnection;
    private OnTakeSnapshotCb onTakeSnapshotCb;
    private OnTakeSnapshotCbBytes onTakeSnapshotCbBytes;
    private OnMessageCb onMessageCb;

    public void sendMessage(String json) {
        if (iMemoStub != null) {
            try {
                iMemoStub.sendMessage(json);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void takeSnapshot(OnTakeSnapshotCb onTakeSnapshotCb) {
        if (iMemoStub != null) {

            Log.e("Gaudi","takeSnapshot(OnTakeSnapshotCb onTakeSnapshotCb)");

            try {
                this.onTakeSnapshotCb = onTakeSnapshotCb;
                iMemoStub.takeSnapshot(new IMemCallback.Stub() {
                    @Override
                    public void onSnapshotCallback(ParcelFileDescriptor data, int length) throws RemoteException {

                        Log.e("Gaudi","onSnapshotCallback");

                        snapshotCallback(data, length);
                    }
                });
                iMemoStub.sendMessage("takeSnapshot");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void takeSnapshot2(OnTakeSnapshotCb onTakeSnapshotCb) {
        if (iMemoStub != null) {
            try {
                this.onTakeSnapshotCb = onTakeSnapshotCb;
                iMemoStub.takeSnapshot(new IMemCallback.Stub() {
                    @Override
                    public void onSnapshotCallback(ParcelFileDescriptor data, int length) throws RemoteException {
                        snapshotCallback(data, length);
                    }
                });
                iMemoStub.sendMessage("takeSnapshot2");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void takeSnapshot3(OnTakeSnapshotCbBytes onTakeSnapshotCbBytes) {

        if (iMemoStub != null) {
            try {
                this.onTakeSnapshotCbBytes = onTakeSnapshotCbBytes;
                iMemoStub.takeSnapshot(new IMemCallback.Stub() {
                    @Override
                    public void onSnapshotCallback(ParcelFileDescriptor data, int length) throws RemoteException {
                        snapshotCallbackMyBitmap(data, length);
                    }
                });
                iMemoStub.sendMessage("takeSnapshot3");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendTestMessageForCallback() {
        sendMessage("getMessage");
    }

    public void registerMsgCallback(OnMessageCb cb) {
        if (iMemoStub != null) {
            try {
                this.onMessageCb = cb;
                iMemoStub.onMessageCallback(new IMsgCallback.Stub() {
                    @Override
                    public void onMsgCallback(String data) throws RemoteException {
                        onMessageCb.callback(data);
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public ServiceConnection getClientConnection() {
        if (clientConnection == null) {
            clientConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Log.d(TAG, "onServiceConnected");
                    iMemoStub = IMemAIDL.Stub.asInterface(service);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Log.d(TAG, "onServiceDisconnected");
                    iMemoStub = null;
                }
            };
        }
        return clientConnection;
    }

    private void snapshotCallback(ParcelFileDescriptor data, int length) {

        Log.e("Gaudi","111snapshotCallback");

        MemoryFile remoteFile = MemFileUtils.openMemoryFile(data, length, PROT_READ | PROT_WRITE);
        if (remoteFile != null) {
            byte[] buffer = new byte[length];
            try {
                remoteFile.readBytes(buffer, 0, 0, buffer.length);
                Bitmap bitmap = BitmapUtil.Bytes2Bitmap(buffer);
                if (onTakeSnapshotCb != null) {
                    onTakeSnapshotCb.callback(bitmap);
                }
                remoteFile.close();
            } catch (IOException e) {
                Log.e(TAG, "snapshotCallback", e);
            }
        }
    }

    private void snapshotCallbackMyBitmap(ParcelFileDescriptor data, int length) {

        Log.e("Gaudi","111snapshotCallbackMybm");

        MemoryFile remoteFile = MemFileUtils.openMemoryFile(data, length, PROT_READ | PROT_WRITE);
        if (remoteFile != null) {
            byte[] buffer = new byte[length];
            try {
                remoteFile.readBytes(buffer, 0, 0, buffer.length);

                if (onTakeSnapshotCbBytes != null) {
                    onTakeSnapshotCbBytes.callback(buffer);
                }
                remoteFile.close();
            } catch (IOException e) {
                Log.e(TAG, "snapshotCallback", e);
            }
        }
    }



    public interface OnTakeSnapshotCb {
        void callback(Bitmap bitmap);
    }

    public interface OnTakeSnapshotCbBytes {
        void callback(byte[] bytes);
    }

    public interface OnMessageCb {
        void callback(String json);
    }

}
