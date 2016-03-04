# GoogleAppEngineOkHttp
An implementation of Call.Factory that allows OkHttp to be used on Google App Engine. 

I've tested this library against OkHttp 3.0.1 and with Retrofit 2.0.0-beta4 running on Google App Engine SDK 1.9.30.

## Usage
    return new Retrofit.Builder()
        .baseUrl("http://api.example.com/")
        .callFactory(new Call.Factory() {
            @Override
            public Call newCall(Request request) {
                return new AppEngineCallFactory(request);
            }
        })
        .build();

## Features
+ Supports GET and POST requests
+ Supports HTTP and HTTPS

## Roadmap
+ Support for other HTTP Methods (e.g. DELETE, PUT, PATCH).
+ Support for binary responses.

## Gotchas
+ Call.enqueue() will throw a Runtime Exception, on App Engine you should be using Tasks and Queues to perform Async calls. Use these with the Call.execute() method instead.

## Pull Requests
In order to make this library more useful I welcome pull requests with new features 
or bug fixes, at the moment I'm adding features as I need them for my own personal projects.