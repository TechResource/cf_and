package com.flightpath.clm.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.flightpath.clm.R;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.DriverTable;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.network.FPModel;
import com.flightpathcore.network.MyCallback;
import com.flightpathcore.network.requests.LoginRequest;
import com.flightpathcore.objects.UpdateAppObject;
import com.flightpathcore.objects.UserObject;
import com.flightpathcore.utilities.SPHelper;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;

import javax.inject.Inject;

import flightpath.com.loginmodule.LoginCallbacks;
import flightpath.com.loginmodule.LoginFragment;
import flightpath.com.loginmodule.UpdateHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-21.
 */
@EActivity(R.layout.login_activity)
public class LoginActivity extends CLMBaseActivity implements LoginCallbacks{
    @FragmentByTag
    protected HeaderFragment headerFragment;
    @FragmentByTag
    protected LoginFragment loginFragment;
    @Inject
    protected FPModel fpModel;

    private UserObject response = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        di().inject(this);
    }

    @AfterViews
    protected void init(){
        headerFragment.setHeaderCallback(loginFragment);
        headerFragment.setViewType(HeaderFragment.ViewType.LOGIN_ACTIVITY);
        loginFragment.setCallback(this);
    }

    @Override
    public void onLogin(String login, String password) {
        fpModel.fpApi.login(new LoginRequest(login, password), loginCallback);
    }

    private MyCallback loginCallback =  new MyCallback<UserObject>() {
        @Override
        public void onSuccess(UserObject response) {
            LoginActivity.this.response = response;
            loginFragment.setLoginBtnEnabled(false);
            DBHelper.getInstance().updateOrInsert(new DriverTable(), new DriverTable().getContentValues(response), DriverTable.HELPER_ID + "");
            SPHelper.saveData(LoginActivity.this, SPHelper.USER_SESSION_KEY, new Gson().toJson(response));

            if (response.update != null && response.update.url != null){
                if(loginFragment.checkPermissions(true)) {
                    askUpdateApp(response.update);
                }else{
                    return;
                }
            }else {
                loginSuccess();
            }
        }

        @Override
        public void onError(String error) {
            loginFragment.setLoginBtnEnabled(true);
            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
        }
    };

    private void loginSuccess(){
        navigator.loginSuccessfully();
    }

    public void askUpdateApp(UpdateAppObject updateAppObject) {
        new UpdateHelper(LoginActivity.this, updateAppObject, (dialog, which) -> loginSuccess()).askUpdateApp();
    }

    @Override
    public void onServerChanged(){
        fpModel.changeServerAddress(this);
    }

    @Override
    public void onPermissionResult() {
        if(loginFragment.checkPermissions(false)){
            if(response != null)
                loginCallback.onSuccess(response);
        }else{
            if(response != null){
                response.update = null; // dont ask about update once again
                loginCallback.onSuccess(response);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (loginFragment != null) {
            loginFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        SplashScreenActivity_.intent(this).start();
        finish();
        overridePendingTransition(com.flightpathcore.R.anim.abc_fade_in, com.flightpathcore.R.anim.abc_slide_out_bottom);
    }
}
