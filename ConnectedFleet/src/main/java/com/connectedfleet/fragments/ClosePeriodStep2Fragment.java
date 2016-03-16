package com.connectedfleet.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.connectedfleet.activity.ClosePeriodActivity;
import com.connectedfleet.di.DI;
import com.flightpathcore.R;
import com.flightpathcore.base.BaseFragment;
import com.flightpathcore.network.FPModel;
import com.flightpathcore.network.MyCallback;
import com.flightpathcore.objects.PeriodResponse;
import com.flightpathcore.utilities.Utilities;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-12-15.
 */
@EFragment(R.layout.fragment_close_period_step2)
public class ClosePeriodStep2Fragment extends BaseFragment {

    @ViewById
    protected EditText agentEmailAddress, periodEndMileage, periodEndDate;
    @ViewById
    protected TextView periodStartMileage, periodStartDate;
    @ViewById
    protected Button closePeriodBtn;
    @ViewById
    protected CheckBox confirmCheckBox;
    @Inject
    protected FPModel fpModel;

    private PeriodResponse periodResponse;
    private int year, month, dayOfMonth;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DI.di().injections().inject(this);
    }

    @AfterViews
    protected void init() {
        Utilities.setOswaldTypeface(getContext().getAssets(), closePeriodBtn);
        if (periodResponse != null) {
            fillView();
        }
        closePeriodBtn.setOnClickListener(v -> closePeriodBtn());
    }

    private void fillView() {
        periodStartMileage.setText(getString(R.string.period_start_mileage) + periodResponse.period.startMileage);
        periodStartDate.setText(getString(R.string.period_start_date) + periodResponse.period.startDate);
        confirmCheckBox.setText(periodResponse.confirmText);
    }

    public void setData(PeriodResponse periodResponse) {
        this.periodResponse = periodResponse;
        if (agentEmailAddress != null) {
            fillView();
        }
    }

    private boolean validate() {
        boolean isValid = true;
        if (!confirmCheckBox.isChecked()) {
            confirmCheckBox.setError("Please confirm.");
            isValid = false;
        }
        if (agentEmailAddress.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(agentEmailAddress.getText().toString()).matches()) {
            agentEmailAddress.setError("Entered email is not valid.");
            isValid = false;
        }
        if (periodEndDate.getText().toString().isEmpty()) {
            periodEndDate.setError("Field can't be empty.");
            isValid = false;
        }
//        try {
//            if (Integer.parseInt(periodEndMileage.getText().toString()) < periodResponse.period.startMileage) {
//                periodEndMileage.setError("End mileage can't be lower than Start mileage.");
//                isValid = false;
//            }
//        } catch (NumberFormatException e) {
//            periodEndMileage.setError("Wrong number format.");
//            isValid = false;
//        }

        return isValid;
    }

    @CheckedChange
    protected void confirmCheckBox() {
        confirmCheckBox.setError(null);
    }

    @TextChange
    protected void periodEndMileage() {
        periodEndMileage.setError(null);
    }

    @TextChange
    protected void agentEmailAddress() {
        agentEmailAddress.setError(null);
    }

    @Click
    protected void closePeriodBtn() {
        if (!validate()) {
            return;
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Closing period...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FPModel fpModel = new FPModel(getActivity());
        fpModel.changeServerAddress(getActivity(), periodResponse.url);
        fpModel.fpApi.closePeriod(periodResponse.period.closingToken, periodEndDate.getText().toString(), periodEndMileage.getText().toString(),
                agentEmailAddress.getText().toString(), new MyCallback<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                        progressDialog.dismiss();
                        Utilities.styleAlertDialog(new AlertDialog.Builder(getActivity(), R.style.BlueAlertDialog)
                                .setTitle("Period closed")
                                .setMessage(R.string.period_closed_message)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        ((ClosePeriodActivity) getActivity()).onPeriodClosed();
                                    }
                                })
                                .show());
                    }

                    @Override
                    public void onError(String error) {
                        progressDialog.dismiss();
                        Utilities.styleAlertDialog(new AlertDialog.Builder(getActivity(), R.style.BlueAlertDialog)
                                .setTitle("Error")
                                .setMessage(error)
                                .setPositiveButton("OK", null)
                                .show());
                    }
                });
    }

    @Click
    protected void periodEndDate() {
        createDialogWithDateAndTimePicker();
    }

    private void createDialogWithDateAndTimePicker() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.date_and_time_picker_layout);
        dialog.setCancelable(true);
        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date d = null;
        try {
            d = sdf.parse(periodResponse.period.startDate);
            if(d.getTime() < System.currentTimeMillis()) {
                dp.setMinDate(d.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dp.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                ClosePeriodStep2Fragment.this.year = year;
                ClosePeriodStep2Fragment.this.month = monthOfYear;
                ClosePeriodStep2Fragment.this.dayOfMonth = dayOfMonth;
            }
        });

        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(new Date(year - 1900, month, dayOfMonth));
                ClosePeriodStep2Fragment.this.periodEndDate.setText(date);
                periodEndDate.setError(null);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
