package com.flightpathcore.objects;

import com.flightpathcore.objects.jobs.LooseItem;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
public class BaseWidgetObject {
    public String type;
    @SerializedName("property")
    public String jsonProperty;
    public String hint;
    public String value;
    @SerializedName("is_required")
    public boolean isRequired = false;
    @SerializedName("is_editable")
    protected boolean isEditable = true;
    protected Integer viewTag = null;
    @SerializedName("input_type")
    public String inputType;

    public boolean[] selectedLooseItems = null;
    public String selection = "0";
    public List<LooseItem> looseItems = null;

    public BaseWidgetObject() {
    }

    public BaseWidgetObject(BaseWidgetObject base) {
        this.type = base.type;
        this.jsonProperty = base.jsonProperty;
        this.hint = base.hint;
        this.value = base.value;
        this.isRequired =  base.isRequired;
        this.isEditable = base.isEditable;
        this.viewTag = base.viewTag;
        this.inputType = base.inputType;
        this.selectedLooseItems = base.selectedLooseItems;
        this.selection = base.selection;
        this.looseItems = base.looseItems;
    }

    public Integer getViewTag(){
        return viewTag;
    }

    public boolean isEditable(){
        return isEditable;
    }
}
