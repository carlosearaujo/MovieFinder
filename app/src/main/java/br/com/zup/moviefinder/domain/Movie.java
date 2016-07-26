package br.com.zup.moviefinder.domain;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.sql.SQLException;
import java.util.List;

import br.com.thindroid.annotations.AlarmTask;
import br.com.thindroid.commons.Application;
import br.com.thindroid.commons.database.ChangeType;
import br.com.thindroid.commons.database.GenericDao;
import br.com.thindroid.commons.utils.DataMerge;
import br.com.thindroid.commons.utils.Mergeable;
import br.com.zup.moviefinder.R;
import br.com.zup.moviefinder.domain.dataresolver.DataResolver;
import br.com.zup.moviefinder.domain.dataresolver.DataResolverFactory;

/**
 * Created by Carlos on 23/07/2016.
 */
@DatabaseTable
@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie extends GenericDao<Movie> {

    private static DataResolver mDataResolver;

    private static final String FIELD_TITLE = "title";
    private static final String FIELD_REMOTE_DATA_RECEIVED = "remoteDataReceived";
    private static final String FIELD_IMAGE_REQUEST_ID = "imageRequestId";

    @DatabaseField(generatedId = true)
    @Mergeable
    private long id;
    @Mergeable
    @JsonProperty("Title")
    @DatabaseField(columnName = FIELD_TITLE)
    private String title;
    @JsonProperty("Director")
    @DatabaseField
    private String director;
    @DatabaseField
    @JsonProperty("Genre")
    private String genre;
    @DatabaseField
    @JsonProperty("Country")
    private String country;
    @DatabaseField
    @JsonProperty("imdbRating")
    private float rating;
    @DatabaseField
    @JsonProperty("Runtime")
    private int duration;
    @Mergeable
    @DatabaseField(columnName = FIELD_IMAGE_REQUEST_ID)
    private long imageRequestId;
    @Mergeable
    @DatabaseField(columnName = FIELD_REMOTE_DATA_RECEIVED)
    private boolean remoteDataReceived;
    @DatabaseField
    @Mergeable
    private String imagePath;

    @JsonProperty("Poster")
    private String remoteImagePath;

    public Movie() {
        super(Movie.class);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRemoteImagePath(String remoteImagePath) {
        if(remoteImagePath.contains("http")) {
            this.remoteImagePath = remoteImagePath;
        }
    }

    public String getImagePath() {
        if(imagePath != null && imagePath.matches(" *")){
            return null;
        }
        return imagePath;
    }

    public String getDirector() {
        return director;
    }

    public String getCountry() {
        return country;
    }

    public String getGenre() {
        return genre;
    }

    public float getRating() {
        return rating;
    }

    public float ratingBase() {
        return getDataResolver().getBaseRating();
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void createOrUpdate() {
        super.createOrUpdate();
        notifyDataChange(ChangeType.CREATE);
    }

    public void requestImage() {
        if(remoteImagePath != null) {
            DownloadManager downloadManager = (DownloadManager) Application.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(remoteImagePath));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            imageRequestId = downloadManager.enqueue(request);
        }
    }

    public static String constraintViolation(Movie newMovie) {
        Dao<Movie, ?> dao = GenericDao.getDao(Movie.class);
        try {
            if(!dao.queryForEq(FIELD_TITLE, newMovie.getTitle()).isEmpty()){
                return Application.getContext().getString(R.string.title);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AlarmTask(interval = AlarmTask.FIVE_MINUTES)
    public void updateMoviesWithoutRemoteDataReceived() throws SQLException {
        List<Movie> moviesWithoutRemoteDataReceived = getDao().queryForEq(FIELD_REMOTE_DATA_RECEIVED, false);
        for(Movie movie : moviesWithoutRemoteDataReceived){
            movie.refreshDataOnServer();
        }
    }

    public long getId() {
        return id;
    }

    public void refreshDataOnServer(){
        if(!isRemoteDataReceived()) {
            DataResolver resolver = getDataResolver();
            Movie remoteMovieData = resolver.recoveryData(this);
            DataMerge.merge(remoteMovieData, this);
            remoteMovieData.setRemoteDataReceived();
            remoteMovieData.requestImage();
            remoteMovieData.createOrUpdate();
        }
    }

    public boolean isRemoteDataReceived() {
        return remoteDataReceived;
    }

    private void setRemoteDataReceived() {
        this.remoteDataReceived = true;
    }

    public static Movie findByEnqueueId(long id) {
        Dao<Movie, ?> dao = getDao(Movie.class);
        try {
            return dao.queryForFirst(dao.queryBuilder().where().eq(FIELD_IMAGE_REQUEST_ID, id).prepare());
        } catch (SQLException e) {
            return null;
        }
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public static List<Movie> findByTitle(String title) {
        Dao<Movie, ?> dao = getDao(Movie.class);
        try {
            return dao.queryBuilder().where().like(FIELD_TITLE, "%" + title + "%").query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static DataResolver getDataResolver(){
        if(mDataResolver == null){
            mDataResolver = new DataResolverFactory().getDataResolver();
        }
        return mDataResolver;
    }
}

