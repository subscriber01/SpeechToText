package cris.connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import org.json.*;

import javax.net.ssl.HttpsURLConnection;

/*
     * This class demonstrates how to get a valid O-auth token from
     * Azure Data Market.
     * Contact unicorn@roboy.org to get an access
     * the Microsoft's Custom Recognition Intelligent Service (CRIS) [www.cris.ai]
     */
public class Authentification
{
    public static final String AccessTokenUri = "https://oxford-speech.cloudapp.net:443/token/issueToken";

    private String clientId;
    private String clientSecret;
    private String request;
    private AccessToken token;
    private Timer accessTokenRenewer;

    //Access token expires every 10 minutes. Renew it every 9 minutes only.
    private final int RefreshTokenDuration = 9 * 60 * 1000;
    private final String charsetName = "utf-8";
    private TimerTask nineMinitesTask = null;

    public Authentification(String clientId, String clientSecret)
    {
        this.clientId = clientId;
        this.clientSecret = clientSecret;

            /*
             * If clientid or client secret has special characters, encode before sending request
             */
        try{
        this.request = String.format("grant_type=client_credentials&client_id=%s&client_secret=%s&scope=%s",
                URLEncoder.encode(clientId,charsetName),
                URLEncoder.encode(clientSecret, charsetName),
                URLEncoder.encode("https://speech.platform.bing.com",charsetName));

        }catch (Exception e){
            e.printStackTrace();
        }
        this.token = HttpPost(AccessTokenUri, this.request);

        // renew the token every specified minutes
        accessTokenRenewer = new Timer();
        nineMinitesTask = new TimerTask(){
            public void run(){
                RenewAccessToken();
            }
        };

        accessTokenRenewer.schedule(nineMinitesTask, 0, RefreshTokenDuration);
    }

    public AccessToken GetAccessToken()
    {
        return this.token;
    }

    private void RenewAccessToken()
    {
        AccessToken newAccessToken = HttpPost(AccessTokenUri, this.request);
        //swap the new token with old one
        //Note: the swap is thread unsafe
        System.out.println("new access token: " + newAccessToken.access_token);
        this.token = newAccessToken;
    }

    private AccessToken HttpPost(String AccessTokenUri, String requestDetails)
    {
        InputStream inSt = null;
        HttpsURLConnection webRequest = null;

        //Prepare OAuth request
        try{
            webRequest = (HttpsURLConnection) ((new URL(AccessTokenUri).openConnection()));
            webRequest.setDoInput(true);
            webRequest.setDoOutput(true);
            webRequest.setConnectTimeout(5000);
            webRequest.setReadTimeout(5000);
            webRequest.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            webRequest.setRequestMethod("POST");

            byte[] bytes = requestDetails.getBytes();
            webRequest.setRequestProperty("content-length", String.valueOf(bytes.length));
            webRequest.connect();

            DataOutputStream dop = new DataOutputStream(webRequest.getOutputStream());
            dop.write(bytes);
            dop.flush();
            dop.close();

            inSt = webRequest.getInputStream();
            InputStreamReader in = new InputStreamReader(inSt);
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuffer strBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                strBuffer.append(line);
            }

            bufferedReader.close();
            in.close();
            inSt.close();
            webRequest.disconnect();

            // parse the access token from the json format
            String result = strBuffer.toString();
            JSONObject obj = new JSONObject(result);
            AccessToken token = new AccessToken();
            
            token.access_token = obj.getString("access_token");
            token.token_type =  obj.getString("token_type");
            token.expires_in =  obj.getString("expires_in");
            token.scope =  obj.getString("scope");

            return token;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
