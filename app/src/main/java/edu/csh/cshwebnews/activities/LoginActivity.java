package edu.csh.cshwebnews.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import edu.csh.cshwebnews.R;
import edu.csh.cshwebnews.models.AccessToken;
import edu.csh.cshwebnews.models.User;
import edu.csh.cshwebnews.network.ServiceGenerator;
import edu.csh.cshwebnews.network.WebNewsService;
import mehdi.sakout.fancybuttons.FancyButton;
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
    ImageView logo;
    FancyButton button;
    TextView webNewsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountManager = AccountManager.get(getBaseContext());
        logo         = (ImageView) findViewById(R.id.image_logo);
        button       = (FancyButton) findViewById(R.id.btn_login);
        webNewsText  = (TextView) findViewById(R.id.textView);
        loginWebView = (WebView) findViewById(R.id.web_oauth);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show the webview to sign in and "remove" everything else
                loginWebView.loadUrl(WebNewsService.BASE_URL + "/oauth/authorize" +
                        "?client_id=" + clientId + "&redirect_uri=" + WebNewsService.REDIRECT_URI + "&response_type=code");
                logo.clearAnimation();
                logo.setVisibility(View.INVISIBLE);
                button.clearAnimation();
                button.setVisibility(View.INVISIBLE);
                webNewsText.clearAnimation();
                webNewsText.setVisibility(View.INVISIBLE);
                loginWebView.setVisibility(View.VISIBLE);
            }
        });

        introAnimation();

        createAuthWebView();
    }

    /**
     * Displays the splash screen animation.
     */
    void introAnimation() {
        button.setVisibility(View.GONE);
        webNewsText.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.translate);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                button.setVisibility(View.VISIBLE);
                webNewsText.setVisibility(View.VISIBLE);
                Animation animFade = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade);
                button.startAnimation(animFade);
                webNewsText.startAnimation(animFade);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        logo.startAnimation(anim);
    }

    /**
     * Creates the webview for authentication
     */
    private void createAuthWebView() {
        loginWebView.getSettings().setJavaScriptEnabled(true);
        loginWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith(WebNewsService.REDIRECT_URI)) {
                    getAccessToken(url);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getBaseContext(), "An error occurred, please try signing in again", Toast.LENGTH_LONG).show();
                loginWebView.setVisibility(View.INVISIBLE);
                logo.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                webNewsText.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Gets an access token using code from the url callback
     *
     * @param url the callback url which contains the code
     */
    private void getAccessToken(String url) {
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
                            // Get user data
                            webNewsService.getUser(new Callback<User>() {

                                @Override
                                public void success(User user, Response response) {
                                    result.putExtra(AccountManager.KEY_ACCOUNT_NAME, user.getUserName());
                                    result.putExtra(AccountManager.KEY_AUTHTOKEN, accessToken.getAccessToken());
                                    result.putExtra(PARAM_USER_PASS, accessToken.getRefreshToken());
                                    result.putExtra(AccountManager.KEY_ACCOUNT_TYPE, "edu.csh.cshwebnews");
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
                            loginWebView.setVisibility(View.INVISIBLE);
                            logo.setVisibility(View.VISIBLE);
                            button.setVisibility(View.VISIBLE);
                            webNewsText.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        accountManager.addAccountExplicitly(account, accountPassword, null);
        accountManager.setAuthToken(account, "Bearer", intent.getStringExtra(AccountManager.KEY_AUTHTOKEN));

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

}
