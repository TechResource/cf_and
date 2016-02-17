package com.flightpathcore.objects.jobs;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
public class LooseItem {

    public int id;
    public String name;

    public LooseItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
