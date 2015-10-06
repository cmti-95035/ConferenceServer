package com.conference.presentations.server.impl;

import com.conference.presentations.server.impl.ServerUtils;
import com.conference.presentations.server.UnicefRequest;
import com.conference.presentations.server.ds.ConferenceDataService;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.CreateResponse;
import com.linkedin.restli.server.RestLiServiceException;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.Action;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.resources.CollectionResourceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestLiCollection(name = "unicefRequest", namespace = "com.conference.presentations.server")
public class UnicefRequestResource extends CollectionResourceTemplate<Integer, UnicefRequest> {
    Logger _log = LoggerFactory.getLogger(UnicefRequestResource.class);
    private static ConferenceDataService dataService = null;

    //  initialize the dataService
    static {
        dataService = ServerUtils.initPoviDataService(dataService);
    }

    @Override
    public UnicefRequest get(Integer unicefRequestId) {
        // first check whether a proper token is attached to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders(), "Get UnicefRequest");

        return dataService.getUnicefRequest(unicefRequestId);
    }

    @Override
    public CreateResponse create(UnicefRequest unicefRequest) {
        // create is the first it register for a unicefRequest so meanwhile a token is not yet issued
        // don't need to check the token

        if (!unicefRequest.hasDetail()) {
            _log.warn("detail of a request has to be provided!");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "missing details in the request");
        }

        Integer unicefRequestId = dataService.addUnicefRequest(unicefRequest);
        if (unicefRequestId == null) {
            _log.error("failed to add unicefRequest: " + unicefRequest);
            throw new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR, "failed to add unicefRequest");
        }

        return new CreateResponse(unicefRequestId);
    }

    @Override
    public UpdateResponse delete(Integer unicefRequestId) {
        // first check whether a proper token is attached to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders(), "Delete UnicefRequest");

        if (!dataService.deleteUnicefRequest(unicefRequestId)) {
            _log.error("failed to delete unicefRequest: " + unicefRequestId);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }

        // since the email is the foreign key in the events table so there's no way to insert an event
        // related to the unicefRequest deletion. In fact there's no real use case from the client to remove an existing unicefRequest
        return new UpdateResponse(HttpStatus.S_200_OK);
    }

    @Override
    public UpdateResponse update(Integer unicefRequestId, UnicefRequest unicefRequest) {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders(), "Update UnicefRequest");

        if (!dataService.updateUnicefRequest(unicefRequest, unicefRequestId)) {
            _log.error("failed to update unicefRequest: " + unicefRequestId + " with details: " + unicefRequest);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }

        return new UpdateResponse(HttpStatus.S_200_OK);
    }


    @Action(name = "getAllUnicefRequests")
    public UnicefRequest[] getAllUnicefRequests() {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders(), "getAllUnicefRequests");

        List<UnicefRequest> unicefRequests = dataService.getAllUnicefRequests();
        if(unicefRequests != null){
            return unicefRequests.toArray(new UnicefRequest[unicefRequests.size()]);
        } else {
            return new UnicefRequest[0];
        }

    }

    @Action(name = "getAllUnicefInprogressRequests")
    public UnicefRequest[] getAllUnicefInprogressRequests() {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders(), "getAllUnicefInprogressRequests");

        List<UnicefRequest> unicefRequests = dataService.getAllUnicefInprogressRequests();
        if(unicefRequests != null){
            return unicefRequests.toArray(new UnicefRequest[unicefRequests.size()]);
        } else {
            return new UnicefRequest[0];
        }

    }

    @Action(name = "getAllUnicefIncomingRequests")
         public UnicefRequest[] getAllUnicefIncomingRequests() {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders(), "getAllUnicefIncomingRequests");

        List<UnicefRequest> unicefRequests = dataService.getAllUnicefIncomingRequests();
        if(unicefRequests != null){
            return unicefRequests.toArray(new UnicefRequest[unicefRequests.size()]);
        } else {
            return new UnicefRequest[0];
        }

    }

    @Action(name = "getAllUnicefCompletedRequests")
    public UnicefRequest[] getAllUnicefCompletedRequests() {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders(), "getAllUnicefCompletedRequests");

        List<UnicefRequest> unicefRequests = dataService.getAllUnicefCompletedRequests();
        if(unicefRequests != null){
            return unicefRequests.toArray(new UnicefRequest[unicefRequests.size()]);
        } else {
            return new UnicefRequest[0];
        }

    }
}
