# GoogleAppEngineOkHttp
An implementation of the OkHttp3 __Call.Factory__ interface that allows OkHttp to be used on Google App Engine.

I've tested this library against OkHttp 3.3.1 and with Retrofit 2.0.2 running on Google App Engine SDK 1.9.48.

+ [https://github.com/square/okhttp](https://github.com/square/okhttp)
+ [https://github.com/square/retrofit](https://github.com/square/retrofit)
+ [https://cloud.google.com/appengine/downloads#Google_App_Engine_SDK_for_Java](https://cloud.google.com/appengine/downloads#Google_App_Engine_SDK_for_Java)

## Usage
    return new Retrofit.Builder()
        .baseUrl("http://api.example.com/")
        .callFactory(new GoogleAppEngineOkHttpClient())
        .build();

## Features
+ Supports GET, POST, PUT, PATCH and DELETE requests
+ Supports HTTP and HTTPS
+ Support for text and binary responses.

## Roadmap
+ Upload into jcenter for inclusion in Gradle builds.

## Gotchas
+ Call.enqueue() will throw a RuntimeException, on App Engine you should be using Tasks and Queues to perform Async calls. Use these with the Call.execute() method instead.

## Pull Requests
In order to make this library more useful I welcome pull requests with new features 
or bug fixes, at the moment I'm adding features as I need them for my own personal projects.