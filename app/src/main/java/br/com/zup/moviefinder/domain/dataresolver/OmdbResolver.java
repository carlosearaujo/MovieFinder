package br.com.zup.moviefinder.domain.dataresolver;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

import br.com.thindroid.commons.web.request.HttpClient;
import br.com.zup.moviefinder.domain.Movie;

/**
 * Created by Carlos on 25/07/2016.
 */

class OmdbResolver implements DataResolver {

    private static final String BASE_PATH = "http://www.omdbapi.com";
    private static final int BASE_RATING = 10;

    @Override
    public Movie recoveryData(Movie movie) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("t", movie.getTitle()));
        HttpClient httpClient = new HttpClient();
        try {
            return httpClient.executeJSONResponse(BASE_PATH, HttpMethod.GET, params, null, MovieDTO.class);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int getBaseRating() {
        return BASE_RATING;
    }
}
class MovieDTO extends Movie{
    @JsonProperty("Runtime")
    public void setDuration(String duration){
        try {
            setDuration(Integer.valueOf(duration.replaceAll("\\D", "")));
        }
        catch (Exception ex){
            //Ignore - No webservice pattern
        }
    }

    @JsonProperty("imdbRating")
    public void setRating(String rating){
        try{
            setRating(Float.valueOf(rating));
        }
        catch (Exception ex){

        }
    }
}
