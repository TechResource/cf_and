package com.flightpathcore.objects;

import android.support.annotation.StringDef;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-02-05.
 */
@StringDef({
    InspectionWidgetTypes.SPINNER,
    InspectionWidgetTypes.SECTION_HEADER,
    InspectionWidgetTypes.INPUT,
    InspectionWidgetTypes.DAMAGES,
    InspectionWidgetTypes.LOOSE_ITEMS,
    InspectionWidgetTypes.CHECK_BOX,
    InspectionWidgetTypes.DAMAGES_WITH_SQUASHED_FROG
})
public @interface InspectionWidgetTypes {
    String SPINNER = "spinner";
    String SECTION_HEADER = "section_header";
    String INPUT = "input";
    String DAMAGES = "damages";
    String LOOSE_ITEMS = "loose_items";
    String CHECK_BOX = "check_box";
    String DAMAGES_WITH_SQUASHED_FROG = "damagesWithSquashedFrog";
}
