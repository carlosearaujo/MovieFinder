package br.com.zup.moviefinder.domain.dataresolver;

import br.com.zup.moviefinder.domain.Movie;

/**
 * Created by Carlos on 25/07/2016.
 */

public interface DataResolver {
    Movie recoveryData(Movie title);
    int getBaseRating();
}
