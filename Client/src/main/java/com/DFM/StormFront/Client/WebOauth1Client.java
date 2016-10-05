package com.DFM.StormFront.Client;

import com.DFM.StormFront.Util.LogUtil;
import com.DFM.StormFront.Util.StringUtil;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.imageio.ImageIO;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

//import org.glassfish.jersey.media.multipart.MultiPartFeature;


/**
 * Created by Mick on 12/21/2015.
 */
public class WebOauth1Client {
    public String PROPERTY_ACCESS_TOKEN;
    public String PROPERTY_ACCESS_TOKEN_SECRET;
    public String PROPERTY_CONSUMER_KEY;
    public String PROPERTY_CONSUMER_SECRET;


    public WebOauth1Client() {
    }

    public WebOauth1Client(String property_access_token, String property_access_token_secret, String property_consumer_key, String property_consumer_secret) {
        this.PROPERTY_ACCESS_TOKEN = property_access_token;
        this.PROPERTY_ACCESS_TOKEN_SECRET = property_access_token_secret;
        this.PROPERTY_CONSUMER_KEY = property_consumer_key;
        this.PROPERTY_CONSUMER_SECRET = property_consumer_secret;
    }

    public String get(String uri) throws IOException {
        Client client = buildClient();

        // make requests to protected resources
        Response response = client.target(uri).request().get();
        return response.readEntity(String.class);
    }

    public HashMap<String, String> post(String uri) throws IOException {
        return post(uri, "{}");
    }

    public HashMap<String, String> post(String uri, String json) throws IOException {
        Client client = buildClient();

        // make requests to protected resources
        if (2 == 12) showAccessTokens(uri);
        Entity entity = Entity.json(json);
        Response response = client.target(uri).request().post(entity);
        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put("code", Integer.toString(response.getStatus()));
        resultMap.put("location", String.valueOf(response.getLocation()));
        resultMap.put("result", response.readEntity(String.class));
        client.close();
        //LogUtil.log(resultMap);
        return resultMap;
    }

    public HashMap<String, String> put(String uri, String json) throws IOException {
        Client client = buildClient();

        // make requests to protected resources
        if (2 == 12) showAccessTokens(uri);
        Entity entity = Entity.json(json);
        Response response = client.target(uri).request().put(entity);
        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put("code", Integer.toString(response.getStatus()));
        resultMap.put("location", String.valueOf(response.getLocation()));
        resultMap.put("result", response.readEntity(String.class));
        client.close();
        //LogUtil.log(resultMap);
        return resultMap;
    }

    public HashMap<String, String> uploadImage(String uri, BufferedImage image, String imageType, String imageName) throws IOException, ParseException {
        return uploadImage(uri, image, imageType, imageName, "");
    }

        public HashMap<String, String> uploadImage(String uri, BufferedImage image, String imageType, String imageName, String customDisposition) throws IOException, ParseException {
        Client client = buildClient();

        WebTarget target = client.target(uri);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        String formatName = (imageType.contains("/")) ? imageType.split("/")[1] : imageType;
        ImageIO.write(image, formatName, baos);

        String contentDisposition = String.format("attachment; filename=%s", imageName);

        if(StringUtil.isNotNullOrEmpty(customDisposition)){
            contentDisposition = customDisposition;
        }

        byte[] bits = baos.toByteArray();

        Response response = target.request()
                .header("Content-Type", imageType)
                .header("Content-Disposition", contentDisposition)
                .header("Accept", "application/json")
                .post(Entity.entity(bits, MediaType.APPLICATION_OCTET_STREAM_TYPE));

        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put("code", Integer.toString(response.getStatus()));
        resultMap.put("result", response.readEntity(String.class));
        //LogUtil.log(resultMap);

        return resultMap;
    }

    public HashMap<String, String> delete(String uri) throws IOException {
        Client client = buildClient();

        // make requests to protected resources
        Response response = client.target(uri).request()
                .header("force", "true")
                .delete();
        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put("code", Integer.toString(response.getStatus()));
        resultMap.put("result", response.readEntity(String.class));
        client.close();
        return resultMap;
    }

    public Client buildClient() {
        //Access tokens are already available from last execution
        ConsumerCredentials consumerCredentials = new ConsumerCredentials(PROPERTY_CONSUMER_KEY, PROPERTY_CONSUMER_SECRET);
        AccessToken storedToken = new AccessToken(PROPERTY_ACCESS_TOKEN, PROPERTY_ACCESS_TOKEN_SECRET);
        Feature filterFeature;
        // build a new filter feature from the stored consumer credentials and access token
        filterFeature = OAuth1ClientSupport.builder(consumerCredentials).feature()
                .accessToken(storedToken).build();

        //ClientConfig configuration = new ClientConfig();
        // configuration = configuration.property(ClientProperties.ASYNC_THREADPOOL_SIZE, 1000);

        // create a new Jersey client and register filter feature that will add OAuth signatures and
        // JacksonFeature that will process returned json data.
        return ClientBuilder.newBuilder()
                .register(filterFeature)
                .register(JacksonFeature.class)
                //.register(MultiPartFeature.class)
                //.withConfig(configuration)
                .build();
    }

    private void showAccessTokens(String uri) {
        LogUtil.log("WebOauth1Client uri: " + uri);
        LogUtil.log("WebOauth1Client PROPERTY_ACCESS_TOKEN: " + PROPERTY_ACCESS_TOKEN);
        LogUtil.log("WebOauth1Client PROPERTY_ACCESS_TOKEN_SECRET: " + PROPERTY_ACCESS_TOKEN_SECRET);
        LogUtil.log("WebOauth1Client PROPERTY_CONSUMER_KEY: " + PROPERTY_CONSUMER_KEY);
        LogUtil.log("WebOauth1Client PROPERTY_CONSUMER_SECRET: " + PROPERTY_CONSUMER_SECRET);
    }
}
