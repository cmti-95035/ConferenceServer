/*
   Copyright (c) 2012 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.conference.presentations.server.impl;

import com.conference.presentations.server.ResearchField;
import com.conference.presentations.server.User;
import com.conference.presentations.server.ds.ConferenceDataService;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.CreateResponse;
import com.linkedin.restli.server.RestLiServiceException;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.Action;
import com.linkedin.restli.server.annotations.ActionParam;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.resources.CollectionResourceTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestLiCollection(name = "user", namespace = "com.conference.presentations.server")

public class UserResource extends CollectionResourceTemplate<Integer, User> {
    Logger _log = LoggerFactory.getLogger(UserResource.class);
    private static ConferenceDataService dataService = null;

    //  initialize the dataService
    static {
        dataService = ServerUtils.initPoviDataService(dataService);
    }

    @Override
    public User get(Integer userId) {
        // first check whether a proper token is attached to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders(), "Get User");

        return dataService.getUser(userId);
    }

    @Override
    public CreateResponse create(User account) {
        // create is the first it register for a user so meanwhile a token is not yet issued
        // don't need to check the token

        if(!account.hasEmail()){
            _log.warn("an email has to be provided!");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "missing email in the request");
        }

        User existingUser = dataService.getUserFromEmail(account.getEmail());
        if(existingUser != null)
        {
            _log.warn(account.getEmail() + " is already registered!");
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, account.getEmail() + " is already registered!");
        }

        Integer userId = dataService.addUser(account);
        if(userId == null) {
            _log.error("failed to add user: " + account);
            throw new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR, "failed to add user");
        }

        return new CreateResponse(userId);
    }

    @Override
    public UpdateResponse delete(Integer userId) {
        // first check whether a proper token is attached to the request as a header
        ServerUtils.checkHeader(getContext().getRequestHeaders(), "Delete User");

        if(!dataService.deleteUser(userId)) {
            _log.error("failed to delete user: " + userId);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }

        // since the email is the foreign key in the events table so there's no way to insert an event
        // related to the user deletion. In fact there's no real use case from the client to remove an existing user
        return new UpdateResponse(HttpStatus.S_200_OK);
    }

    @Override
    public UpdateResponse update(Integer userId, User account) {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders(), "Update User");

        if(!dataService.updateUser(account, userId)) {
            _log.error("failed to update user: " + userId + " with details: " + account);
            return new UpdateResponse(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        }

        return new UpdateResponse(HttpStatus.S_200_OK);
    }

    @Action(name = "getUserFromEmail")
    public User getUserFromEmail(@ActionParam("email") String email) {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders(), "getUserFromEmail");

        if(email == null || email.isEmpty()){
            _log.warn("Invalid email: " + email);
            throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "Invalid email: " + email);
        }

        return dataService.getUserFromEmail(email);
    }

    @Action(name = "getAllUsers")
    public User[] getAllUsers() {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders(), "getAllUsers");

        List<User> users = dataService.getAllUsers();
        if(users != null){
            return users.toArray(new User[users.size()]);
        } else {
            return new User[0];
        }

    }

    @Action(name = "getAllResearchFields")
    public ResearchField[] getAllResearchFields() {
        // first check whether a proper token is attached to the request as a header
        String token = ServerUtils.checkHeader(getContext().getRequestHeaders(), "getAllResearchFields");

        List<ResearchField> fields = dataService.getAllResearchFields();
        if(fields != null) {
            return fields.toArray(new ResearchField[fields.size()]);
        } else {
            return new ResearchField[0];
        }
    }
}
