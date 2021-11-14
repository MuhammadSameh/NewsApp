package com.androiddevs.mvvmnewsapp.network

/**
 * this sealed class is used to wrap our response and makes the code concise
 * **/
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    //These are the classes that can inherit from our Resource class

    //If the request is successful then we will need the data returned from the api
    class Success<T>(data: T): Resource<T>(data)

    //If the request isn't successful then we need the error message
    class Error<T>(data: T? = null, message: String?): Resource<T>(data, message)

    //If the request is still loading then will show the progressBar, and we don't need neither the
    //data nor the error message
    class Loading<T>(): Resource<T>()

}