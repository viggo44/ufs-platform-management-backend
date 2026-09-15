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
import ru.sbrf.platformmanagement.domain.service.AppUserService;
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

    private final AppUserService appUserService;

    @POST
    public UserDto createUser(@Valid UserCreateDto request) {
        return appUserService.createUser(request);
    }

    @PATCH
    public UserDto patchUser(@QueryParam("id") Long id, @Valid UserPatchDto request) {
        return appUserService.patchUser(RequireParam.notNull(id, "id"), request);
    }

    @GET
    public UfsPageListRs<UserDto> getUsers(@QueryParam("page") Integer page,
                                            @QueryParam("limit") Integer limit,
                                            @QueryParam("sort") String sort,
                                            @QueryParam("lastName") String lastName,
                                            @QueryParam("firstName") String firstName,
                                            @QueryParam("tabNum") Long tabNum) {
        UfsPageRequest pageRequest = new UfsPageRequest(
                RequireParam.notNull(page, "page"), RequireParam.notNull(limit, "limit"));
        return appUserService.getUsers(pageRequest, sort, lastName, firstName, tabNum);
    }

    @GET
    @Path("/groups")
    public List<GroupDto> getUserGroups(@QueryParam("id") Long id) {
        return appUserService.getUserGroups(RequireParam.notNull(id, "id"));
    }

    @GET
    @Path("/flags")
    public List<UserFlagDto> getUserFlags(@QueryParam("id") Long id,
                                           @QueryParam("customOnly") @DefaultValue("false") boolean customOnly) {
        return appUserService.getUserFlags(RequireParam.notNull(id, "id"), customOnly);
    }

    @DELETE
    public boolean deleteUser(@QueryParam("id") Long id) {
        return appUserService.deleteUser(RequireParam.notNull(id, "id"));
    }
}
