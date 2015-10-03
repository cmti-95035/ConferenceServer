package com.conference.presentations.server.impl;

import com.conference.presentations.server.Conference;
import com.conference.presentations.server.User;
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

@RestLiCollection(name = "conference", namespace = "com.conference.presentations.server")
public class ConferenceResource extends CollectionResourceTemplate<Integer, Conference> {
    Logger _log = LoggerFactory.getLogger(ConferenceResource.class);
    private static ConferenceDataService dataService = null;

    //  initialize the dataService
    static {
        dataService = ServerUtils.initPoviDataService(dataService);
    }

    @Override
    public Conference get(Integer conferenceId) {
        // first check whether a proper token is attached to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders(), "Get Conference");

        return dataService.getConference(conferenceId);
    }

    @Override
    public CreateResponse create(Conference conference) {
        // create is the first it register for a conference so meanwhile a token is not yet issued
        // don't need to check the token

        if (!conference.hasVenue()) {
            _log.warn("a venue has to be provided!");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "missing venue in the request");
        }

        Integer conferenceId = dataService.addConference(conference);
        if (conferenceId == null) {
            _log.error("failed to add conference: " + conference);
            throw new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR, "failed to add conference");
        }

        return new CreateResponse(conferenceId);
    }

    @Override
    public UpdateResponse delete(Integer conferenceId) {
        // first check whether a proper token is attached to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders(), "Delete Conference");

        if (!dataService.deleteConference(conferenceId)) {
            _log.error("failed to delete conference: " + conferenceId);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }

        // since the email is the foreign key in the events table so there's no way to insert an event
        // related to the conference deletion. In fact there's no real use case from the client to remove an existing conference
        return new UpdateResponse(HttpStatus.S_200_OK);
    }

    @Override
    public UpdateResponse update(Integer conferenceId, Conference conference) {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders(), "Update Conference");

        if (!dataService.updateConference(conference, conferenceId)) {
            _log.error("failed to update conference: " + conferenceId + " with details: " + conference);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }

        return new UpdateResponse(HttpStatus.S_200_OK);
    }


    @Action(name = "getAllConferences")
    public Conference[] getAllConferences() {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders(), "getAllConferences");

        List<Conference> conferences = dataService.getAllConferences();
        if(conferences != null){
            return conferences.toArray(new Conference[conferences.size()]);
        } else {
            return new Conference[0];
        }

    }
}
