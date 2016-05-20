package com.runway.activity;

import android.os.Bundle;
import android.widget.Toast;

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
import com.runway.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;

import javax.inject.Inject;

import flightpath.com.loginmodule.LoginCallbacks;
import flightpath.com.loginmodule.LoginFragment;
import flightpath.com.loginmodule.UpdateHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
@EActivity(R.layout.login_activity)
public class LoginActivity extends RunwayBaseActivity implements LoginCallbacks {
    @FragmentByTag
    protected HeaderFragment headerFragment;
    @FragmentByTag
    protected LoginFragment loginFragment;
    @Inject
    protected FPModel fpModel;

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
        fpModel.fpApi.login(
                new LoginRequest(login, password), new MyCallback<UserObject>() {
                    @Override
                    public void onSuccess(UserObject response) {
                        response.email = login;
                        response.password = password;
                        loginFragment.setLoginBtnEnabled(false);
                        DBHelper.getInstance().updateOrInsert(new DriverTable(), new DriverTable().getContentValues(response), DriverTable.HELPER_ID + "");
                        SPHelper.saveData(LoginActivity.this, SPHelper.USER_SESSION_KEY, new Gson().toJson(response));
                        loginFragment.setLoginBtnEnabled(true);
                        if (response.update != null && response.update.url != null){
                            askUpdateApp(response.update);
                        }else {
                            loginSuccess();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        loginFragment.setLoginBtnEnabled(true);
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

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
        // TODO: 20.05.2016
    }

    @Override
    public void onBackPressed() {
        SplashScreenActivity_.intent(this).start();
        finish();
        overridePendingTransition(com.flightpathcore.R.anim.abc_fade_in, com.flightpathcore.R.anim.abc_slide_out_bottom);
    }
}
