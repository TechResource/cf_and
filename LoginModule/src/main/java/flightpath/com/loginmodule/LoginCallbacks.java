package flightpath.com.loginmodule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
public interface LoginCallbacks {

    void onLogin(String login, String password);
    void onServerChanged();
    void onPermissionResult();

}
