package com.flightpathcore.network;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-05-19.
 */
public abstract class MyCallback<T> implements Callback<T> {

    public abstract void onSuccess(T response);
    public abstract void onError(String error);

    @Override
    public void success(T t, Response response) {
        onSuccess(t);
    }

    @Override
    public void failure(RetrofitError error) {
        if(error.getKind().equals(RetrofitError.Kind.CONVERSION)) {
            onError(error.getMessage().replace("retrofit.converter.ConversionException: ", ""));
        }else{
            switch (error.getKind()){
                case NETWORK:
                    onError("Network is unreachable. Please check your internet connection.");
                    break;
                case CONVERSION:
                    onError(error.getMessage());
                    break;
                case HTTP:
                    onError(error.getMessage());
                    break;
            }
        }

    }
}
