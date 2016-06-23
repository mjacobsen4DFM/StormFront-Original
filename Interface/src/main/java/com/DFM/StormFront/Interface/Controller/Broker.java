package com.DFM.StormFront.Interface.Controller;

import com.DFM.StormFront.Interface.Handler.PublisherHandler;
import com.DFM.StormFront.Interface.Handler.WordPressHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Created by Mick on 2/3/2016.
 */

@Path("/broker")
public class Broker {
    @POST
    @Path("/publisher/ngps/contentsync")
    //@Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    //@HeaderParam("user-agent")
    public Response Publisher_NGPS_ContentSync(@HeaderParam("RedisType") String redisType,
                                               InputStream incomingData) {
        if(null == redisType || redisType.equals("")) { redisType = "default"; }
        return PublisherHandler.NGPS_ContentSync(redisType, incomingData, false);
    }


    @POST
    @Path("/publisher/ngps/contentsync/delete")
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response Publisher_NGPS_ContentSync_Delete(@HeaderParam("RedisType") String redisType,
                                                      InputStream incomingData) {
        if(null == redisType || redisType.equals("")) { redisType = "default"; }
        return PublisherHandler.NGPS_ContentSync(redisType, incomingData, true);
    }


    @POST
    @Path("/publisher/saxotech/mwc")
    //@Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    //@HeaderParam("user-agent")
    public Response Publisher_Saxotech_MWC(@HeaderParam("RedisType") String redisType,
                                                InputStream incomingData) {
        if(null == redisType || redisType.equals("")) { redisType = "default"; }
        return PublisherHandler.Saxotech_MWC(redisType, incomingData);
    }


    @PUT
    @Path("/publisher/wordpress/hubsync")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //@HeaderParam("user-agent")
    public Response Publisher_Wordpress_HubSync(@HeaderParam("RedisType") String redisType,
                                                InputStream incomingData) {
        if(null == redisType || redisType.equals("")) { redisType = "default"; }
        return PublisherHandler.WordPress_HubSync(redisType, incomingData);
    }


    @POST
    @Path("/wordpress/posts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //@HeaderParam("user-agent")
    public Response PostPost(@HeaderParam("RedisType") String redisType,
                             @HeaderParam("RedisKey") String redisKey,
                             @HeaderParam("remoteEndpoint") String remoteEndpoint,
                             InputStream incomingData) {
        if(null == redisType || redisType.equals("")) { redisType = "default"; }
        return WordPressHandler.PostPost(redisType, redisKey, remoteEndpoint, incomingData);
    }

    @GET
    @Path("/wordpress/posts")
    @Produces(MediaType.APPLICATION_JSON)
    //@HeaderParam("user-agent")
    public Response GetPost(@HeaderParam("RedisType") String redisType,
                            @HeaderParam("RedisKey") String redisKey,
                            @HeaderParam("remoteEndpoint") String remoteEndpoint) {
        if(null == redisType || redisType.equals("")) { redisType = "default"; }
        return WordPressHandler.GetPost(redisType, redisKey, remoteEndpoint);
    }

    @POST
    @Path("/wordpress/posts/attributes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //@HeaderParam("user-agent")
    public Response PostAttributes(@HeaderParam("RedisType") String redisType,
                                   @HeaderParam("RedisKey") String redisKey,
                                   @HeaderParam("remoteEndpoint") String remoteEndpoint,
                                   InputStream incomingData) {
        if(null == redisType || redisType.equals("")) { redisType = "default"; }
        return WordPressHandler.PostPost(redisType, redisKey, remoteEndpoint, incomingData);
    }

    @PUT
    @Path("/wordpress/posts/attributes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //@HeaderParam("user-agent")
    public Response PutAttributes(@HeaderParam("RedisType") String redisType,
                                   @HeaderParam("RedisKey") String redisKey,
                                   @HeaderParam("remoteEndpoint") String remoteEndpoint,
                                   InputStream incomingData) {
        if(null == redisType || redisType.equals("")) { redisType = "default"; }
        return WordPressHandler.PutPost(redisType, redisKey, remoteEndpoint, incomingData);
    }



    @DELETE
    @Path("/wordpress/posts/attributes")
    @Produces(MediaType.APPLICATION_JSON)
    //@HeaderParam("user-agent")
    public Response DeleteAttributes(@HeaderParam("RedisType") String redisType,
                                     @HeaderParam("RedisKey") String redisKey,
                                     @HeaderParam("remoteEndpoint") String remoteEndpoint) {
        if(null == redisType || redisType.equals("")) { redisType = "default"; }
        return WordPressHandler.DeleteAttributes(redisType, redisKey, remoteEndpoint);
    }


    @POST
    @Path("/wordpress/posts/media")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //@HeaderParam("user-agent")
    public Response PostMedia(@HeaderParam("RedisType") String redisType,
                              @HeaderParam("RedisKey") String redisKey,
                              @HeaderParam("remoteEndpoint") String remoteEndpoint,
                              InputStream incomingData) {
        if(null == redisType || redisType.equals("")) { redisType = "default"; }
        return WordPressHandler.PostMedia(redisType, redisKey, remoteEndpoint, incomingData);
    }


    @POST
    @Path("/wordpress/posts/defaultauthor")
    @Produces(MediaType.APPLICATION_JSON)
    //@HeaderParam("user-agent")
    public Response PostAuthor(@HeaderParam("RedisType") String redisType,
                               @HeaderParam("RedisKey") String redisKey,
                               @HeaderParam("remoteEndpoint") String remoteEndpoint) {
        if(null == redisType || redisType.equals("")) { redisType = "default"; }
        return WordPressHandler.PostDefaultAuthor(redisType, redisKey, remoteEndpoint);
    }


    @PUT
    @Path("/deadend")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //@HeaderParam("user-agent")
    public Response DeadEnd(InputStream incomingData) {
        return null;
    }



}
