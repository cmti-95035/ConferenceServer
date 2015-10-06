package com.conference.presentations.server.impl;

import com.conference.presentations.server.IncomingSMS;
import com.conference.presentations.server.IncomingSMS;
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

@RestLiCollection(name = "incomingSMS", namespace = "com.conference.presentations.server")
public class IncomingSMSResource extends CollectionResourceTemplate<Integer, IncomingSMS> {
    Logger _log = LoggerFactory.getLogger(IncomingSMSResource.class);
    private static ConferenceDataService dataService = null;

    //  initialize the dataService
    static {
        dataService = ServerUtils.initPoviDataService(dataService);
    }

    @Override
    public IncomingSMS get(Integer incomingSMSId) {
        // first check whether a proper token is attached to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders(), "Get IncomingSMS");

        return dataService.getIncomingSMS(incomingSMSId);
    }

    @Override
    public CreateResponse create(IncomingSMS incomingSMS) {
        // create is the first it register for a incomingSMS so meanwhile a token is not yet issued
        // don't need to check the token

        if (!incomingSMS.hasValues()) {
            _log.warn("values of a request has to be provided!");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "missing values in the request");
        }

        Integer incomingSMSId = dataService.addIncomingSMS(incomingSMS);
        if (incomingSMSId == null) {
            _log.error("failed to add incomingSMS: " + incomingSMS);
            throw new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR, "failed to add incomingSMS");
        }

        return new CreateResponse(incomingSMSId);
    }
}