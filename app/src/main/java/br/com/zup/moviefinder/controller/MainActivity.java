package br.com.zup.moviefinder.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.ui.utils.inject.InjectView;

import br.com.thindroid.commons.Application;
import br.com.thindroid.commons.database.GenericDao;
import br.com.zup.moviefinder.R;
import br.com.zup.moviefinder.controller.utils.AfterTextChangedTextWatcher;
import br.com.zup.moviefinder.domain.Movie;
import br.com.zup.moviefinder.utils.QuietlyRunnable;

public class MainActivity extends BaseActivity {

    BroadcastReceiver dataChangeReceiver;

    @InjectView(R.id.local_movies_recycler)
    private RecyclerView mLocalMovies;
    @InjectView(R.id.btn_add_new)
    private FloatingActionButton wgtBtnAddNewMovie;
    @InjectView(R.id.edt_add_new)
    private EditText wgtEdtNewMovieTitle;
    @InjectView(R.id.find_on_gallery)
    private EditText wgtBtnFildOnGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    private void initComponents() {
        validations();
        wgtBtnAddNewMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMovie();
            }
        });
        wgtBtnAddNewMovie.setClickable(false);
        buildList();
        configFilter();
    }

    private void configFilter() {
        wgtBtnFildOnGallery.addTextChangedListener(new AfterTextChangedTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                refreshListApplyingFilter();
            }
        });
    }

    private void buildList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLocalMovies.setLayoutManager(layoutManager);
        mLocalMovies.setAdapter(new MovieAdapter(GenericDao.getAll(Movie.class)));
    }

    private void refreshListApplyingFilter() {
        ((MovieAdapter)mLocalMovies.getAdapter()).getFilter().filter(wgtBtnFildOnGallery.getText());
    }

    private void addNewMovie() {
        final Movie newMovie = new Movie();
        newMovie.setTitle(wgtEdtNewMovieTitle.getText().toString());
        String constraintViolation = Movie.constraintViolation(newMovie);
        if(constraintViolation == null) {
            newMovie.createOrUpdate();
            requestDataImmediately(newMovie);
            Toast.makeText(this, R.string.add_success, Toast.LENGTH_LONG).show();
            wgtEdtNewMovieTitle.setText("");
        }
        else{
            new AlertDialog.Builder(MainActivity.this).
                    setTitle(getString(R.string.gallery_add_error)).
                    setMessage(String.format(getString(R.string.constraint_violation_message), constraintViolation)).show();
        }
    }

    private void requestDataImmediately(final Movie newMovie) {
        new Thread(new QuietlyRunnable() {
            @Override
            public void runQuietly() throws Exception {
                newMovie.refreshDataOnServer();
            }
        }).start();
    }

    private void validations() {
        wgtEdtNewMovieTitle.addTextChangedListener(new AfterTextChangedTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                boolean isClicable = !s.toString().matches(" *");
                if(isClicable) {
                    wgtBtnAddNewMovie.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                }
                else{
                    wgtBtnAddNewMovie.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
                }
                wgtBtnAddNewMovie.setClickable(isClicable);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Application.getContext().unregisterReceiver(getDataChangeReceiver());
        }
        catch (IllegalArgumentException ex){
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GenericDao.getContentChangeFilter(Movie.class));
        Application.getContext().registerReceiver(getDataChangeReceiver(), intentFilter);
    }

    public BroadcastReceiver getDataChangeReceiver(){
        if(dataChangeReceiver == null) {
            dataChangeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    refreshListApplyingFilter();
                }
            };
        }
        return dataChangeReceiver;
    }
}
