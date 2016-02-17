package com.flightpathcore.objects;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

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
    }

    public Integer getViewTag(){
        return viewTag;
    }

    public boolean isEditable(){
        return isEditable;
    }
}
