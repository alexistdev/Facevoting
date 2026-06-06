package com.berkatfaatulohalawa1711010164.facevoting.helper

import com.berkatfaatulohalawa1711010164.facevoting.model.ErrorModel
import retrofit2.Response
import java.io.IOException
import java.lang.annotation.Annotation

object ErrorHelper {
    fun parseError(response: Response<*>): ErrorModel {
        val converter = ServiceError.retrofit
            .responseBodyConverter<ErrorModel>(ErrorModel::class.java, arrayOfNulls<Annotation>(0))
        return try {
            if (response.errorBody() != null) {
                converter.convert(response.errorBody()!!) ?: ErrorModel("error")
            } else {
                ErrorModel("error")
            }
        } catch (e: IOException) {
            ErrorModel("error")
        }
    }
}
