package ru.sbrf.platformmanagement.web.controller;

import jakarta.validation.Valid;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sbrf.platformmanagement.domain.service.UserService;
import ru.sbrf.platformmanagement.ufs.api.model.GroupDto;
import ru.sbrf.platformmanagement.ufs.api.model.UfsPageListRs;
import ru.sbrf.platformmanagement.ufs.api.model.UfsPageRequest;
import ru.sbrf.platformmanagement.ufs.api.model.UserCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.UserDto;
import ru.sbrf.platformmanagement.ufs.api.model.UserFlagDto;
import ru.sbrf.platformmanagement.ufs.api.model.UserPatchDto;
import ru.sbrf.platformmanagement.web.mapper.RequireParam;

import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Component
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @POST
    public UserDto createUser(@Valid UserCreateDto request) {
        return userService.createUser(request);
    }

    @PATCH
    public UserDto patchUser(@QueryParam("id") String id, @Valid UserPatchDto request) {
        return userService.patchUser(RequireParam.notNull(id, "id"), request);
    }

    @GET
    public UfsPageListRs<UserDto> getUsers(@QueryParam("page") Integer page,
                                            @QueryParam("limit") Integer limit,
                                            @QueryParam("sort") String sort,
                                            @QueryParam("lastName") String lastName,
                                            @QueryParam("firstName") String firstName,
                                            @QueryParam("tabNum") String tabNum) {
        UfsPageRequest pageRequest = new UfsPageRequest(
                RequireParam.notNull(page, "page"), RequireParam.notNull(limit, "limit"));
        return userService.getUsers(pageRequest, sort, lastName, firstName, tabNum);
    }

    @GET
    @Path("/groups")
    public List<GroupDto> getUserGroups(@QueryParam("id") String id) {
        return userService.getUserGroups(RequireParam.notNull(id, "id"));
    }

    @GET
    @Path("/flags")
    public List<UserFlagDto> getUserFlags(@QueryParam("id") String id,
                                           @QueryParam("customOnly") @DefaultValue("false") boolean customOnly) {
        return userService.getUserFlags(RequireParam.notNull(id, "id"), customOnly);
    }

    @DELETE
    public boolean deleteUser(@QueryParam("id") String id) {
        return userService.deleteUser(RequireParam.notNull(id, "id"));
    }
}
