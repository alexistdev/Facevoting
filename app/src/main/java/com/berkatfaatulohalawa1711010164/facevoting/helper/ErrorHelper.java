package com.berkatfaatulohalawa1711010164.facevoting.helper;

import com.berkatfaatulohalawa1711010164.facevoting.model.ErrorModel;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorHelper {
    public static ErrorModel parseError(Response<?> response) {
        Converter<ResponseBody, ErrorModel> converter =
                ServiceError.retrofit
                        .responseBodyConverter(ErrorModel.class, new Annotation[0]);
        ErrorModel error ;
        try {
            if(response.errorBody() != null) {
                error = converter.convert(response.errorBody());
            }  else {
                error = null;
            }
        } catch (IOException e) {
            return new ErrorModel("error");
        }
        return error;
    }
}
