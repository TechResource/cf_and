package com.flightpathcore.database.tables;

import android.content.ContentValues;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-07.
 */
public interface AbstractTable<T> {

    String getTableName();
    String getWhereClause(String id);
    String getOutputClassName();
    String[] getAllColumns();
    String getIdColumn();
    ContentValues getContentValues(T object);

}
