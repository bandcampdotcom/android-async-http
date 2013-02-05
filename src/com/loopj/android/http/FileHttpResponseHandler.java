package com.loopj.android.http;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;

import java.io.*;

public class FileHttpResponseHandler extends AsyncHttpResponseHandler {

    private File file;
    private static final int BUFFER_SIZE = 16384;

    public FileHttpResponseHandler(File outputFile) {
        this.file = file;
    }

    public void onProgress(int expectedLength, int downloadedLength) {}

    // Interface to AsyncHttpRequest
    @Override
    void sendResponseMessage(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        int expectedLength = Integer.parseInt(response.getFirstHeader("Content-Length").getValue());
        try {
            InputStream inputStream = response.getEntity().getContent();
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
        }
        catch(IOException e) {
            sendFailureMessage(e, (byte[])null);
        }

        if(status.getStatusCode() >= 300) {
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), (byte[])null);
        }
        else {
            sendSuccessMessage(status.getStatusCode(), file.getAbsolutePath());
        }
    }
}
