package flightpath.com.loginmodule;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.base.BaseFragment;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.utilities.ServerChooser;
import com.flightpathcore.utilities.Utilities;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-19.
 */

@EFragment(resName = "fragment_login")
public class LoginFragment extends BaseFragment implements HeaderFragment.HeaderCallback {

    public static final int PERMISSION_REQUEST = 2;

    public static final String[] permissionNeed = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @ViewById
    protected EditText login, password;
    @ViewById
    protected Button loginBtn;

    private String securityCode;
    private LoginCallbacks loginCallback;
    private String email = null;

    @AfterViews
    protected void init() {
        Utilities.setOswaldTypeface(getContext().getAssets(), loginBtn);
        securityCode = getString(R.string.security_code);
        if( ((BaseApplication)getActivity().getApplication()).isDebug(getContext()) ){
            login.setText("radek@appsvisio.com");
            password.setText("radek123");
        }
        if(email != null)
            login.setText(email);
    }

    public void setCallback(LoginCallbacks loginCallbacks){
        this.loginCallback = loginCallbacks;
    }

    @Click
    protected void loginBtn() {
        Utilities.hideSoftKeyboard(getActivity());
        loginCallback.onLogin(login.getText().toString().trim(), password.getText().toString().trim());
        loginBtn.setEnabled(false);
    }

    @AfterTextChange(resName = {"login", "password"})
    protected void onTextChanged() {
        if (!login.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            loginBtn.setEnabled(true);
        } else {
            loginBtn.setEnabled(false);
        }
    }

    AlertDialog securityCodeDialog = null;

    @Override
    public void onHeaderLeftBtnClick() {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_with_edit_text, null);
        ((EditText) v.findViewById(R.id.securityCode)).addTextChangedListener(passwordWatcher);
        securityCodeDialog = new AlertDialog.Builder(getActivity(), R.style.BlueAlertDialog)
                .setView(v)
                .setNegativeButton(R.string.cancel_text, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
        Utilities.styleAlertDialog(securityCodeDialog);
    }

    public void setEmail(String email){
        if(login != null)
            login.setText(email);
        else this.email = email;
    }

    @Override
    public void onHeaderRightBtnClick() {

    }

    @Override
    public void onMenuBtnClick() {

    }

    private TextWatcher passwordWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().equals(securityCode)) {
                showHostAddressesDialog();
                if (securityCodeDialog != null) {
                    securityCodeDialog.dismiss();
                    securityCodeDialog = null;
                }
            }
        }
    };

    private void showHostAddressesDialog() {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_with_edit_text, null);
        final EditText et = (EditText) v.findViewById(R.id.securityCode);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setText(ServerChooser.getHostAddress(getContext()));
        et.setSelection(et.length());
        TextInputLayout til = (TextInputLayout) v.findViewById(R.id.til);
        til.setHint(getString(R.string.server_address_label));
        Utilities.styleAlertDialog(new AlertDialog.Builder(getContext())
                .setTitle(R.string.server_address_label)
                .setView(v)
                .setPositiveButton(R.string.ok_label, (dialog, which) -> {
                    String typedHost = et.getText().toString();
                    if (!typedHost.isEmpty()) {
                        ServerChooser.setHostAddress(getContext(), typedHost);
                        loginCallback.onServerChanged();
                    }
                })
                .setItems(ServerChooser.getAllHostAddresses(getActivity()), (dialog1, which1) -> {
                    ServerChooser.setHostAddress(getContext(), ServerChooser.getAllHostAddresses(getContext())[which1]);
                    loginCallback.onServerChanged();
                })
                .show());
    }

    @Override
    public String getTitle() {
        return null;
    }

    public void setLoginBtnEnabled(boolean enable) {
        loginBtn.setEnabled(enable);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public boolean checkPermissions(boolean requestPermission) {
        List<String> permissionsRequest = new ArrayList<>();

        for (String p : permissionNeed) {
            if (ContextCompat.checkSelfPermission(getActivity(), p) != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(p);
            }
        }

        if (permissionsRequest.size() == 0) {
            return true;
        } else {
            if(requestPermission) {
                ActivityCompat.requestPermissions(getActivity(), permissionsRequest.toArray(new String[permissionsRequest.size()]), PERMISSION_REQUEST);
            }
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        permissions[i].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if(loginCallback != null){
                        loginCallback.onPermissionResult();
                    }
                    return;
                }else{
                    if(loginCallback != null){
                        loginCallback.onPermissionResult();
                    }
                }
            }
        }
    }

    public void requestFocusOnPassword() {
        password.postDelayed(() -> {
            login.clearFocus();
            password.requestFocus();
            InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.showSoftInput(password, InputMethodManager.SHOW_IMPLICIT);

        },250);
    }
}
