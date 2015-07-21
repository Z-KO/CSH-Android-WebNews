package edu.csh.cshwebnews.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.csh.cshwebnews.R;
import edu.csh.cshwebnews.database.WebNewsContract;
import edu.csh.cshwebnews.models.AccessToken;
import edu.csh.cshwebnews.models.User;
import edu.csh.cshwebnews.models.WebNewsAccount;
import edu.csh.cshwebnews.network.ServiceGenerator;
import edu.csh.cshwebnews.network.WebNewsService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivity extends AccountAuthenticatorActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String PARAM_USER_PASS = "USER_PASS";

    private String clientId;
    private String clientSecret;

    AccountManager accountManager;
    WebView loginWebView;
    ImageView mImageView;
    TextView mTextview;
    TextView mErrorTextView;
    Button mRefreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_signed_in),false)) {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        } else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.black));
            }
            accountManager  = AccountManager.get(getBaseContext());
            loginWebView = (WebView) findViewById(R.id.web_oauth);
            mImageView = (ImageView) findViewById(R.id.csh_logo);
            mTextview = (TextView) findViewById(R.id.loading_textview);
            mErrorTextView = (TextView) findViewById(R.id.error_textview);
            mRefreshButton = (Button) findViewById(R.id.refresh_button);
            mRefreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mErrorTextView.setVisibility(View.GONE);
                    mRefreshButton.setVisibility(View.GONE);
                    loginWebView.loadUrl(WebNewsService.BASE_URL + "/oauth/authorize" +
                            "?client_id=" + clientId + "&redirect_uri=" + WebNewsService.REDIRECT_URI + "&response_type=code");
                }
            });

            mImageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
            loginWebView.setVisibility(View.GONE);


            createAuthWebView();
        }
    }

    /**
     * Creates the webview for authentication
     */
    private void createAuthWebView() {
        loginWebView.getSettings().setJavaScriptEnabled(true);
        loginWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
                mTextview.setVisibility(View.VISIBLE);
                if (url != null && url.startsWith(WebNewsService.REDIRECT_URI)) {
                    getAccessToken(url);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.setVisibility(View.VISIBLE);
                mImageView.clearAnimation();
                mImageView.setVisibility(View.GONE);
                mTextview.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("");
                view.setVisibility(View.GONE);
                mErrorTextView.setVisibility(View.VISIBLE);
                mRefreshButton.setVisibility(View.VISIBLE);
                mErrorTextView.setText("Error loading page...\n" + description);
            }
        });

        loginWebView.loadUrl(WebNewsService.BASE_URL + "/oauth/authorize" +
                "?client_id=" + clientId + "&redirect_uri=" + WebNewsService.REDIRECT_URI + "&response_type=code");
    }

    /**
     * Gets an access token using code from the url callback
     *
     * @param url the callback url which contains the code
     */
    private void getAccessToken(final String url) {
        //Get auth code from callback uri
        String code = Uri.parse(url).getQueryParameter("code");

        if (code != null) {
            WebNewsService generator = ServiceGenerator.createService(WebNewsService.class,
                    WebNewsService.BASE_URL, null, null);

            //Get an access token
            generator.getAccessToken("authorization_code", code, WebNewsService.REDIRECT_URI, clientId, clientSecret,
                    new Callback<AccessToken>() {
                        @Override
                        public void success(final AccessToken accessToken, Response response) {
                            final Intent result = new Intent();

                            WebNewsService webNewsService= ServiceGenerator.createService(WebNewsService.class,
                                    WebNewsService.BASE_URL, accessToken.getAccessToken(), accessToken.getTokenType());
                            Utility.webNewsService = webNewsService;
                            // Get user data
                            webNewsService.getUser(new Callback<User>() {

                                @Override
                                public void success(User user, Response response) {
                                    //Put user data in the db
                                    ContentValues userValues = new ContentValues();
                                    userValues.put(WebNewsContract.UserEntry._ID,1);
                                    userValues.put(WebNewsContract.UserEntry.USERNAME,user.getUserName());
                                    userValues.put(WebNewsContract.UserEntry.DISPLAY_NAME,user.getDisplayName());
                                    userValues.put(WebNewsContract.UserEntry.EMAIL,user.getUserName()+"@csh.rit.edu");
                                    userValues.put(WebNewsContract.UserEntry.AVATAR_URL,user.getAvatarUrl());
                                    userValues.put(WebNewsContract.UserEntry.IS_ADMIN,user.isAdmin());
                                    userValues.put(WebNewsContract.UserEntry.CREATED_AT,user.getCreatedAt());
                                    getBaseContext().getContentResolver().insert(WebNewsContract.UserEntry.CONTENT_URI,userValues);

                                    result.putExtra(AccountManager.KEY_ACCOUNT_NAME, user.getUserName());
                                    result.putExtra(AccountManager.KEY_AUTHTOKEN, accessToken.getAccessToken());
                                    result.putExtra(PARAM_USER_PASS, accessToken.getRefreshToken());
                                    result.putExtra(AccountManager.KEY_ACCOUNT_TYPE, WebNewsAccount.ACCOUNT_TYPE);
                                    finishLogin(result);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Toast.makeText(getBaseContext(),"FAILED TO GET USER",Toast.LENGTH_SHORT).show();
                                }
                            });

                            loginWebView.destroy();
                            loginWebView = null;
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getBaseContext(), "Error getting access token, please try signing in again", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void finishLogin(Intent intent) {
        mImageView.clearAnimation();
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        accountManager.addAccountExplicitly(account, accountPassword, null);
        accountManager.setAuthToken(account, WebNewsAccount.AUTHTOKEN_TYPE, intent.getStringExtra(AccountManager.KEY_AUTHTOKEN));

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean(getString(R.string.pref_signed_in),true).apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
