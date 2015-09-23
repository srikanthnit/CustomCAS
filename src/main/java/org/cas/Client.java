package org.cas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class Client {

    public URL createUrl(String url) throws MalformedURLException {
        return new URL(url);
    }

    public HttpURLConnection get(String url) throws IOException {

        URL connectionUrl = createUrl(url);
        return (HttpURLConnection) connectionUrl.openConnection();

    }

    /**
     * Helper Method to post data to the given url and with the given params
     * 
     * @param url
     * @param data2
     * @return
     * @throws IOException
     */
    public HttpURLConnection post(String url, Map<String, Object> data) throws IOException {

        URL connectionUrl = createUrl(url);

        byte[] postDataBytes = convertParamMapToBytes(data);
        HttpURLConnection conn = (HttpURLConnection) connectionUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        return conn;
    }

    /**
     * Helper method to convert a map to POST bytes
     * 
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    private byte[] convertParamMapToBytes(Map<String, Object> params) throws UnsupportedEncodingException {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0)
                postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        return postData.toString().getBytes("UTF-8");
    }

    public String getResponse(HttpURLConnection con) throws IOException {
        StringBuilder content = new StringBuilder();
        if (con != null) {

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String input;
            while ((input = br.readLine()) != null) {
                content.append(input);
            }
            br.close();

        }
        return content.toString();

    }

}
