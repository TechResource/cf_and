package flightpath.com.inspectionmodule.widgets.objects;


import com.flightpathcore.objects.BaseWidgetObject;
import com.flightpathcore.objects.jobs.LooseItem;

import java.util.List;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
public class LooseItemsObject extends BaseWidgetObject {
    public List<LooseItem> looseItems = null;
    public boolean[] selectedLooseItems = null;

    public LooseItemsObject(String jsonProperty, Integer viewTag) {
        this.jsonProperty = jsonProperty;
        this.viewTag = viewTag;
    }

    public String[] getLooseItemsAsArray() {
        if(looseItems == null){
            return new String[0];
        }
        String[] a = new String[looseItems.size()];
        for (int i=0;i<looseItems.size();i++){
            a[i] = looseItems.get(i).name;
        }
        return a;
    }
}
