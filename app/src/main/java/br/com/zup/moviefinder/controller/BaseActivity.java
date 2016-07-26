package br.com.zup.moviefinder.controller;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.android.ui.utils.inject.Inject;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * Created by Carlos on 23/07/2016.
 */
@Aspect
public class BaseActivity extends AppCompatActivity {
    protected View getView(){
        return ((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);
    }

    @Before("execution(private * br.com.zup.moviefinder.controller.*.initComponents(..)) && this(thiz)")
    public void injectComponents(BaseActivity thiz){
        Inject.inject(thiz, thiz.getView());
    }
}
