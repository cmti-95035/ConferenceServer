package com.conference.presentations.server.impl;

import com.conference.presentations.server.Presentation;
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

@RestLiCollection(name = "presentation", namespace = "com.conference.presentations.server")
public class PresentationResource extends CollectionResourceTemplate<Integer, Presentation> {
    Logger _log = LoggerFactory.getLogger(PresentationResource.class);
    private static ConferenceDataService dataService = null;

    //  initialize the dataService
    static {
        dataService = ServerUtils.initPoviDataService(dataService);
    }

    @Override
    public Presentation get(Integer presentationId) {
        // first check whether a proper token is attached to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders(), "Get Presentation");

        return dataService.getPresentation(presentationId);
    }

    @Override
    public CreateResponse create(Presentation presentation) {
        // create is the first it register for a presentation so meanwhile a token is not yet issued
        // don't need to check the token

        if (!presentation.hasTitle()) {
            _log.warn("a title has to be provided!");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "missing title in the request");
        }

        Integer presentationId = dataService.addPresentation(presentation);
        if (presentationId == null) {
            _log.error("failed to add presentation: " + presentation);
            throw new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR, "failed to add presentation");
        }

        return new CreateResponse(presentationId);
    }

    @Override
    public UpdateResponse delete(Integer presentationId) {
        // first check whether a proper token is attached to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders(), "Delete Presentation");

        if (!dataService.deletePresentation(presentationId)) {
            _log.error("failed to delete presentation: " + presentationId);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }

        // since the email is the foreign key in the events table so there's no way to insert an event
        // related to the presentation deletion. In fact there's no real use case from the client to remove an existing presentation
        return new UpdateResponse(HttpStatus.S_200_OK);
    }

    @Override
    public UpdateResponse update(Integer presentationId, Presentation presentation) {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders(), "Update Presentation");

        if (!dataService.updatePresentation(presentation, presentationId)) {
            _log.error("failed to update presentation: " + presentationId + " with details: " + presentation);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }

        return new UpdateResponse(HttpStatus.S_200_OK);
    }


    @Action(name = "getAllPresentations")
    public Presentation[] getAllPresentations() {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders(), "getAllPresentations");

        List<Presentation> presentations = dataService.getAllPresentations();
        if(presentations != null){
            return presentations.toArray(new Presentation[presentations.size()]);
        } else {
            return new Presentation[0];
        }

    }
}

