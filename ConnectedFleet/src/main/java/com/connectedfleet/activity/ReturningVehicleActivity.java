package com.connectedfleet.activity;

import android.content.Intent;
import android.text.Html;
import android.widget.Button;

import com.connectedfleet.R;
import com.connectedfleet.widgets.ExpandableView;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.DriverTable;
import com.flightpathcore.objects.UserObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by tomaszszafran on 02/09/16.
 */
@EActivity(R.layout.activity_returning_activity)
public class ReturningVehicleActivity extends CFBaseActivity {

    @ViewById
    protected ExpandableView vehicleCondition, service, validMot, keys, fuelLevel, doorMirrors,
            windowTitle, lamps, tyres, overall, damageFree;
    @ViewById
    protected Button send;

    @AfterViews
    protected void init(){

    }

    @Click
    protected void send(){
        UserObject user = (UserObject) DBHelper.getInstance().get(new DriverTable(), DriverTable.HELPER_ID+"");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Returning Vehicle: " + (user != null ? user.vehicleRegistration : "") );
        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(new StringBuilder()
                .append("<div>")
                .append(vehicleCondition.buildHtmlTag())
                .append(service.buildHtmlTag())
                .append(validMot.buildHtmlTag())
                .append(keys.buildHtmlTag())
                .append(fuelLevel.buildHtmlTag())
                .append(doorMirrors.buildHtmlTag())
                .append(windowTitle.buildHtmlTag())
                .append(lamps.buildHtmlTag())
                .append(tyres.buildHtmlTag())
                .append(overall.buildHtmlTag())
                .append(damageFree.buildHtmlTag())
                .append("</div>")
                .toString()));

        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
