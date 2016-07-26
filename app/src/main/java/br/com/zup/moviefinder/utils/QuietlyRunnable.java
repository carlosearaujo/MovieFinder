package br.com.zup.moviefinder.utils;

import br.com.thindroid.commons.log.RemoteLog;

/**
 * Created by carlos on 09/06/16.
 */
public abstract class QuietlyRunnable implements Runnable {

    public abstract void runQuietly() throws Exception;

    @Override
    public void run() {
        try{
            runQuietly();
        }
        catch (Exception ex){
            RemoteLog.i(QuietlyRunnable.class.getSimpleName(), ex);
        }
    }
}
