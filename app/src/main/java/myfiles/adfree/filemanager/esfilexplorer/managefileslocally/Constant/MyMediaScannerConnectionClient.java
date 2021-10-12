package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

public class MyMediaScannerConnectionClient implements MediaScannerConnection.MediaScannerConnectionClient{

    private final String mFilename;
    private String mMimetype;
    private final MediaScannerConnection mConn;

    public MyMediaScannerConnectionClient
            (Context ctx,File file,String mimetype) {
        this.mFilename = file.getAbsolutePath();
        mConn = new MediaScannerConnection(ctx, this);
        mConn.connect();
    }
    @Override
    public void onMediaScannerConnected() {
        mConn.scanFile(mFilename, mMimetype);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mConn.disconnect();
    }
}
