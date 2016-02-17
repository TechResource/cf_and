package com.flightpathcore.objects;

import com.flightpathcore.objects.ListItem;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-07.
 */
public class SimpleListItem implements ListItem {
    public String leftText, rightText;

    public SimpleListItem(String leftText, String rightText) {
        this.leftText = leftText;
        this.rightText = rightText;
    }
}
