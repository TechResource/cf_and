package com.connectedfleet.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.connectedfleet.R;
import com.connectedfleet.fragments.ClosePeriodStep2Fragment;
import com.connectedfleet.fragments.ClosePeriodStep2Fragment_;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.network.FPModel;
import com.flightpathcore.network.MyCallback;
import com.flightpathcore.objects.ListItem;
import com.flightpathcore.objects.LoaderObject;
import com.flightpathcore.objects.PeriodResponse;
import com.flightpathcore.utilities.Utilities;
import com.flightpathcore.widgets.ProgressListItem;
import com.flightpathcore.widgets.ProgressListItem_;
import com.flightpathcore.widgets.SimpleDividerItemDecoration;
import com.flightpathcore.widgets.SinglePeriodItem;
import com.flightpathcore.widgets.SinglePeriodItem_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-12-11.
 */
@EActivity(R.layout.activity_close_period)
public class ClosePeriodActivity extends CFBaseActivity implements HeaderFragment.HeaderCallback{

    @FragmentById
    protected HeaderFragment headerFragment;
    @ViewById
    protected RecyclerView list;
    @ViewById
    protected Button open2Step;
    @Inject
    protected FPModel fpModel;

    private Adapter adapter;
    private ClosePeriodStep2Fragment closePeriodStep2Fragment;
    private boolean fragmentShowing = false;
    private PeriodResponse periodResponse;
    private boolean ready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        di().inject(this);
    }

    @AfterViews
    protected void init(){
        headerFragment.setViewType(HeaderFragment.ViewType.CLOSE_PERIOD);
        Utilities.setOswaldTypeface(getAssets(), open2Step);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        list.setLayoutManager(lm);
        list.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        adapter = new Adapter(this);
        list.setAdapter(adapter);
        adapter.addItem(new LoaderObject());

        fpModel.fpApi.periodTrips(new MyCallback<PeriodResponse>() {
            @Override
            public void onSuccess(PeriodResponse response) {
                periodResponse = response;
                adapter.setItems(response.trips);
                ready = true;
            }

            @Override
            public void onError(String error) {
                if (!ClosePeriodActivity.this.isFinishing())
                    Utilities.styleAlertDialog(new AlertDialog.Builder(ClosePeriodActivity.this, R.style.BlueAlertDialog)
                            .setTitle("Error")
                            .setMessage(error)
                            .setPositiveButton("OK", (dialog, which) -> ClosePeriodActivity.this.finish())
                            .show());
            }
        });
    }

    @Click
    protected void open2Step(){
        if(ready) {
            closePeriodStep2Fragment = ClosePeriodStep2Fragment_.builder().build();
            closePeriodStep2Fragment.setData(periodResponse);
            findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
            fragmentShowing = true;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, closePeriodStep2Fragment)
                    .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom)
                    .commit();
            open2Step.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if(fragmentShowing){
            getSupportFragmentManager().beginTransaction()
                    .remove(closePeriodStep2Fragment)
                    .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom)
                    .commit();
            findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
            fragmentShowing = false;
            closePeriodStep2Fragment = null;
            open2Step.setVisibility(View.VISIBLE);
        }else {
            super.onBackPressed();
        }
    }

    public void onPeriodClosed() {
        finish();
    }

    @Override
    public void onHeaderLeftBtnClick() {
        onBackPressed();
    }

    @Override
    public void onHeaderRightBtnClick() {

    }

    @Override
    public void onMenuBtnClick() {

    }

    public class Adapter extends RecyclerView.Adapter<Adapter.Holder>{

        protected List<ListItem> items = new ArrayList<>();
        private Context context;

        public Adapter(Context context) {
            this.context = context;
        }

        public void setItems(List<PeriodResponse.Trip> items){
            this.items.clear();
            this.items.addAll(items);
            notifyDataSetChanged();
        }

        public void addItem(ListItem item){
            this.items.add(item);
            notifyItemChanged(items.indexOf(item));
        }

        @Override
        public int getItemViewType(int position) {
            if(items.get(position) instanceof PeriodResponse.Trip){
                return 0;
            }else if(items.get(position) instanceof LoaderObject){
                return 1;
            }else{
                throw new IllegalArgumentException("wrong item type:"+items.get(position).toString());
            }
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == 0) {
                return new Holder(SinglePeriodItem_.build(context));
            }else if(viewType == 1){
                return new Holder(ProgressListItem_.build(context));
            }else{
                throw new IllegalArgumentException("wrong item type:");
            }
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            if(getItemViewType(position) == 0) {
                holder.periodItem.setData((PeriodResponse.Trip) items.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class Holder extends RecyclerView.ViewHolder{
            private SinglePeriodItem periodItem;
            private ProgressListItem progressListItem;

            public Holder(SinglePeriodItem itemView) {
                super(itemView);
                this.periodItem = itemView;
                periodItem.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            }

            public Holder(ProgressListItem item){
                super(item);
                this.progressListItem = item;
            }
        }
    }

}
