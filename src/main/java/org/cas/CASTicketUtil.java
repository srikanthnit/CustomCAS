package org.cas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.security.sasl.AuthenticationException;

public class CASTicketUtil {

    public static String getServiceTicket(String serviceUrl, String user, String pass, String casUrl) throws IOException {

        String TGT = getTicketGrantingTicket(user, pass, casUrl);

        // get SGT
        return getServiceGrantingTicket(TGT, serviceUrl, casUrl);

    }

    /**
     * With the TGT location and service url this will get the SGT
     * 
     * @param tgtLocation
     * @param serviceUrl
     * @return
     * @throws IOException
     */
    public static String getServiceGrantingTicket(String TGT, String serviceUrl, String casUrl) throws IOException {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("service", serviceUrl);
        params.put("method", "POST");

        HttpURLConnection conn = new Client().post(casUrl + "/v1/tickets" + "/" + TGT, params);
        StringBuilder responseBuilder = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String input;
        while ((input = in.readLine()) != null) {
            responseBuilder.append(input);
        }
        in.close();
        String response = responseBuilder.toString();

        return response;
    }

    /**
     * Gets the TGT for the given username and password
     * 
     * @param password
     * @param username
     * @param username
     * @param password
     * @param casUrl
     * @return
     * @throws IOException
     */
    public static String getTicketGrantingTicket(String username, String password, String casUrl) throws IOException {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("username", username);
        params.put("password", password);
        HttpURLConnection conn = new Client().post(casUrl + "/v1/tickets", params);

        if (conn.getResponseCode() == 400) {
            throw new AuthenticationException("bad username or password");
        }
        String location = conn.getHeaderField("Location");

        return location.substring(location.indexOf("TGT"));
    }

}
