package br.com.zup.moviefinder.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.ui.utils.inject.Inject;
import com.android.ui.utils.inject.InjectView;

import java.util.ArrayList;
import java.util.List;

import br.com.zup.moviefinder.R;
import br.com.zup.moviefinder.domain.Movie;

/**
 * Created by Carlos on 23/07/2016.
 */
public class MovieAdapter extends RecyclerView.Adapter implements Filterable {

    private List<Movie> movies;

    public MovieAdapter(List<Movie> movies) {
        if(movies == null){
            movies = new ArrayList<>();
        }
        this.movies = movies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        ViewHolder mHolder = (ViewHolder) holder;
        setImage(movie, mHolder);
        mHolder.title.setText(formatMovieAttr(movie, movie.getTitle()));
        mHolder.country.setText(formatMovieAttr(movie, movie.getCountry()));
        mHolder.director.setText(formatMovieAttr(movie, movie.getDirector()));
        mHolder.genre.setText(formatMovieAttr(movie, movie.getGenre()));
        mHolder.duration.setText(formatMovieAttr(movie, movie.getDuration() > 0 ? movie.getDuration() + " min" : ""));
        mHolder.rating.setRating((movie.getRating() * mHolder.rating.getNumStars()) / movie.ratingBase());
    }

    private void setImage(Movie movie, ViewHolder mHolder) {
        if(movie.getImagePath() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(movie.getImagePath());
            mHolder.movieImage.setImageBitmap(bitmap);
        }
        else{
            mHolder.movieImage.setImageResource(R.drawable.no_image);
        }
    }

    private String formatMovieAttr(Movie movie, String attr) {
        if(attr == null || attr.matches(" *")){
            if(movie.isRemoteDataReceived()){
                return "N/A";
            }
            return "-";
        }
        return attr;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                filterResults.values = Movie.findByTitle(charSequence.toString());
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                movies = (List<Movie>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.movie_image)
        ImageView movieImage;
        @InjectView(R.id.movie_title)
        TextView title;
        @InjectView(R.id.movie_country)
        TextView country;
        @InjectView(R.id.movie_director)
        TextView director;
        @InjectView(R.id.movie_genre)
        TextView genre;
        @InjectView(R.id.movie_duration)
        TextView duration;
        @InjectView(R.id.movie_rating)
        AppCompatRatingBar rating;

        public ViewHolder(View itemView) {
            super(itemView);
            Inject.inject(this, itemView);
        }
    }
}
