package com.flightpathcore.di.components;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScope {}
