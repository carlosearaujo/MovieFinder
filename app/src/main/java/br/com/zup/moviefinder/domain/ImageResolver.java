package br.com.zup.moviefinder.domain;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

/**
 * Created by carlos on 26/07/16.
 */
public class ImageResolver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            DownloadData downloadData = getDownloadData(context, intent);
            updateMovieImagePath(downloadData);
        }
    }

    private void updateMovieImagePath(DownloadData downloadData) {
        if (downloadData != null && downloadData.getStatus() == DownloadManager.STATUS_SUCCESSFUL) {
            Movie movie = Movie.findByEnqueueId(downloadData.getId());
            if (movie != null) {
                movie.setImagePath(downloadData.getFilePath());
                movie.createOrUpdate();
            }
        }
    }

    private DownloadData getDownloadData(Context context, Intent intent) {
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = dm.query(query);
        if (cursor.moveToFirst()) {
            DownloadData downloadData = new DownloadData();
            downloadData.setId(downloadId);
            downloadData.setStatus(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)));
            downloadData.setFilePath(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)));
            return downloadData;
        }
        return null;
    }

    private class DownloadData {
        private long id;
        private int status;
        private String filePath;

        public void setId(long id) {
            this.id = id;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public long getId() {
            return id;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getFilePath() {
            return filePath;
        }
    }
}
