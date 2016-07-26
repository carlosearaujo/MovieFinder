package br.com.zup.moviefinder.domain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import br.com.thindroid.commons.Application;
import br.com.thindroid.commons.database.DefaultRepository;

/**
 * Created by Carlos on 23/07/2016.
 */
@br.com.thindroid.annotations.Repository(value = Repository.REPOSITORY_NAME, managedClassList = {Movie.class})
public class Repository extends DefaultRepository {

    private static final int DATABASE_VERSION = 2;
    public static final String REPOSITORY_NAME = "default-database.db";

    public Repository() {
        super(Application.getContext(), REPOSITORY_NAME, null, DATABASE_VERSION);
    }
}
