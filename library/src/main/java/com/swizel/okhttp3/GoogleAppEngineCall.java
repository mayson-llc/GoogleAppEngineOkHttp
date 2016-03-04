package com.swizel.okhttp3;

import okhttp3.*;
import okio.Buffer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * Created by Andy on 4/03/2016.
 */
public class GoogleAppEngineCall implements Call {

    private static final String NO_ASYNC_MESSAGE = "Async callbacks should be performed using tasks/queues on App Engine along with the execute() instead of enqueue() method.";
    private Request mRequest;
    private boolean mExecuted = false;
    private boolean mCancelled = false;

    GoogleAppEngineCall(Request request) {
        mRequest = request;
    }

    @Override
    public Request request() {
        return mRequest;
    }

    @Override
    public Response execute() throws IOException {
        synchronized (this) {
            if (mExecuted) {
                throw new IllegalStateException("Already Executed");
            }
            mExecuted = true;
        }

        Response.Builder builder;
        if ("GET".equalsIgnoreCase(mRequest.method())) {
            builder = sendGet(mRequest);

        } else if ("POST".equalsIgnoreCase(mRequest.method())) {
            builder = sendPost(mRequest);

        } else {
            throw new RuntimeException("Unsupported HTTP method : " + mRequest.method());
        }

        return builder.build();
    }

    @Override
    public void enqueue(Callback responseCallback) {
        throw new RuntimeException(NO_ASYNC_MESSAGE);
    }

    @Override
    public void cancel() {
        mCancelled = true;
    }

    @Override
    public boolean isExecuted() {
        return mExecuted;
    }

    @Override
    public boolean isCanceled() {
        return mCancelled;
    }

    private Response.Builder sendGet(Request request) throws IOException {

        String url = request.url().url().toString();

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        applySSLFix(request, obj, con);

        setHeaders(request, con);

        return parseResponse(con);
    }

    private Response.Builder sendPost(Request request) throws IOException {

        String url = request.url().url().toString();

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");

        applySSLFix(request, obj, con);

        setHeaders(request, con);

        Buffer sink = new Buffer();
        request.body().writeTo(sink);

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(sink.readString(Charset.forName("UTF-8")));
        wr.flush();
        wr.close();

        return parseResponse(con);
    }

    private void setHeaders(Request request, URLConnection con) {
        Headers headers = request.headers();
        for (String header : headers.names()) {
            // TODO: Support multiple values/header
            con.setRequestProperty(header, headers.get(header));
        }
    }

    /**
     * HttpsUrlConnection isn't supported on App Engine, so add a new Header to fix that.
     *
     * @param request
     * @param url
     * @param connection
     */
    private void applySSLFix(Request request, URL url, URLConnection connection) {
        if (request.isHttps()) {
            int port = url.getPort();
            if (port == -1) {
                port = 443;
            }
            connection.setRequestProperty("Host", url.getHost() + ":" + port);
        }
    }

    private Response.Builder parseResponse(HttpURLConnection connection) throws IOException {
        Response.Builder builder = new Response.Builder();
        builder.request(request());
        builder.protocol(Protocol.HTTP_1_1);
        builder.code(connection.getResponseCode());

        InputStream in = connection.getInputStream();
        if (in != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String inputLine;
            StringBuilder response = new StringBuilder();

            // FIXME: We can't assume all requests will return text, what about binary data?
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            builder.body(ResponseBody.create(MediaType.parse(connection.getContentType()), response.toString()));
        }

        return builder;
    }

}
