package com.connectedfleet.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.connectedfleet.R;
import com.flightpathcore.base.AppCore;
import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.network.FPModel;
import com.flightpathcore.network.MyCallback;
import com.flightpathcore.network.requests.LoginRequest;
import com.flightpathcore.objects.UpdateAppObject;
import com.flightpathcore.objects.UserObject;
import com.flightpathcore.utilities.SPHelper;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import flightpath.com.loginmodule.LoginFragment;
import flightpath.com.loginmodule.SplashScreenFragment;
import flightpath.com.loginmodule.UpdateHelper;
import retrofit.Callback;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
@EActivity(R.layout.splash_screen_activity)
public class SplashScreenActivity extends CFBaseActivity implements SplashScreenFragment.SplashScreenCallback{

    @FragmentByTag
    protected SplashScreenFragment splashScreenFragment;
    @Inject
    protected FPModel model;
    private UserObject userObject;
    private UserObject response = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        di().inject(this);
    }

    @AfterViews
    protected void init(){
        userObject = SPHelper.getUserSession(SplashScreenActivity.this);
        if (userObject != null && userObject.email != null && !userObject.email.isEmpty()) {
            model.fpApi.login(new LoginRequest(userObject.email, userObject.password), loginCallback);
        } else {
            splashScreenFragment.showPager(this);
        }
    }

    private MyCallback<UserObject> loginCallback = new MyCallback<UserObject>() {
        @Override
        public void onSuccess(UserObject response) {
            response.email = userObject.email;
            response.password = userObject.password;
            SplashScreenActivity.this.response = response;

            SPHelper.saveData(SplashScreenActivity.this, SPHelper.USER_SESSION_KEY, new Gson().toJson(response));
            if (response.update != null && response.update.url != null ){
                if(checkPermissions(true)) {
                    askUpdateApp(response.update);
                }else{
                    return;
                }
            }else {
                navigator.loginSuccessfully();
            }
        }

        @Override
        public void onError(String error) {
            navigator.loginSuccessfully();
        }
    };

    private boolean checkPermissions(boolean requestPermission) {
        List<String> permissionsRequest = new ArrayList<>();

        for (String p : LoginFragment.permissionNeed) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(p);
            }
        }

        if (permissionsRequest.size() == 0) {
            return true;
        } else {
            if(requestPermission) {
                ActivityCompat.requestPermissions(this, permissionsRequest.toArray(new String[permissionsRequest.size()]), LoginFragment.PERMISSION_REQUEST);
            }
            return false;
        }
    }

    public void askUpdateApp(UpdateAppObject updateAppObject) {
        new UpdateHelper(SplashScreenActivity.this, updateAppObject, (dialog, which) -> navigator.loginSuccessfully()).askUpdateApp();
    }

    @Override
    public void onLoginBtnClick() {
        navigator.loginActivity();
    }

    public void onPermissionResult() {
        if(checkPermissions(false)){
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
        if (requestCode == LoginFragment.PERMISSION_REQUEST) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        permissions[i].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    onPermissionResult();
                    return;
                }else{
                    onPermissionResult();
                }
            }
        }
    }

}
