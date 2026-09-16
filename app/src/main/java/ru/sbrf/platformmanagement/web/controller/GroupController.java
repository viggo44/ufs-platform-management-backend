package ru.sbrf.platformmanagement.web.controller;

import jakarta.validation.Valid;
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
import ru.sbrf.platformmanagement.domain.service.GroupService;
import ru.sbrf.platformmanagement.ufs.api.model.GroupCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.GroupFlagDto;
import ru.sbrf.platformmanagement.ufs.api.model.GroupPatchDto;
import ru.sbrf.platformmanagement.ufs.api.model.GroupDto;
import ru.sbrf.platformmanagement.ufs.api.model.UfsPageListRs;
import ru.sbrf.platformmanagement.ufs.api.model.UfsPageRequest;
import ru.sbrf.platformmanagement.ufs.api.model.UserDto;
import ru.sbrf.platformmanagement.web.mapper.RequireParam;

import java.util.List;

@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
@Component
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @POST
    public GroupDto createGroup(@Valid GroupCreateDto request) {
        return groupService.createGroup(request);
    }

    @PATCH
    public GroupDto patchGroup(@QueryParam("id") Long id, @Valid GroupPatchDto request) {
        return groupService.patchGroup(RequireParam.notNull(id, "id"), request);
    }

    @GET
    public UfsPageListRs<GroupDto> getGroups(@QueryParam("page") Integer page,
                                              @QueryParam("limit") Integer limit,
                                              @QueryParam("sort") String sort,
                                              @QueryParam("name") String name) {
        UfsPageRequest pageRequest = new UfsPageRequest(
                RequireParam.notNull(page, "page"), RequireParam.notNull(limit, "limit"));
        return groupService.getGroups(pageRequest, sort, name);
    }

    @GET
    @Path("/users")
    public UfsPageListRs<UserDto> getGroupUsers(@QueryParam("id") Long id,
                                                 @QueryParam("page") Integer page,
                                                 @QueryParam("limit") Integer limit,
                                                 @QueryParam("sort") String sort,
                                                 @QueryParam("lastName") String lastName,
                                                 @QueryParam("firstName") String firstName,
                                                 @QueryParam("tabNum") String tabNum) {
        UfsPageRequest pageRequest = new UfsPageRequest(
                RequireParam.notNull(page, "page"), RequireParam.notNull(limit, "limit"));
        return groupService.getGroupUsers(RequireParam.notNull(id, "id"), pageRequest, sort, lastName, firstName, tabNum);
    }

    @POST
    @Path("/users")
    public boolean addGroupUsers(@QueryParam("id") Long id, List<String> userIds) {
        return groupService.addGroupUsers(RequireParam.notNull(id, "id"), userIds);
    }

    @POST
    @Path("/users/remove")
    public boolean removeGroupUsers(@QueryParam("id") Long id, List<String> userIds) {
        return groupService.removeGroupUsers(RequireParam.notNull(id, "id"), userIds);
    }

    @GET
    @Path("/flags")
    public List<GroupFlagDto> getGroupFlags(@QueryParam("id") Long id) {
        return groupService.getGroupFlags(RequireParam.notNull(id, "id"));
    }

    @DELETE
    public boolean deleteGroup(@QueryParam("id") Long id) {
        return groupService.deleteGroup(RequireParam.notNull(id, "id"));
    }
}
