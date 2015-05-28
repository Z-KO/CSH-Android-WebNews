package edu.csh.cshwebnews.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import edu.csh.cshwebnews.authentication.WebNewsAccountAuthenticator;

/**
 * Created by zko on 5/28/15.
 */
public class WebNewsAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new WebNewsAccountAuthenticator(this).getIBinder();
    }
}
