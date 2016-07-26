package br.com.zup.moviefinder.domain.dataresolver;

/**
 * Created by Carlos on 25/07/2016.
 */

public class DataResolverFactory {
    public static DataResolver getDataResolver(){
        return new OmdbResolver();
    }
}
