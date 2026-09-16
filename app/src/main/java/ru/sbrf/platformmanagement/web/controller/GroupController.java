package ru.sbrf.platformmanagement.web.controller;

import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sbrf.platformmanagement.domain.service.GroupService;
import ru.sbrf.platformmanagement.ufs.api.model.model.GroupCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.GroupFlagDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.GroupPatchDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.GroupDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.UfsPageListRs;
import ru.sbrf.platformmanagement.ufs.api.model.model.UfsPageRequest;
import ru.sbrf.platformmanagement.ufs.api.model.model.UserDto;
import ru.sbrf.platformmanagement.web.mapper.RequireParam;
import ru.sbrf.ufs.platform.core.jaxrs.response.RpcHandler;
import ru.sbrf.ufs.platform.core.jaxrs.response.model.BaseResponse;

import java.util.List;

@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
@Component
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @POST
    public BaseResponse<GroupDto> createGroup(@Valid GroupCreateDto request) {
        return RpcHandler.get(() -> groupService.createGroup(request));
    }

    @PATCH
    @Path("/{id}")
    public BaseResponse<GroupDto> patchGroup(@PathParam("id") Long id, @Valid GroupPatchDto request) {
        return RpcHandler.get(() -> groupService.patchGroup(id, request));
    }

    @GET
    public BaseResponse<UfsPageListRs<GroupDto>> getGroups(@QueryParam("page") Integer page,
                                                             @QueryParam("limit") Integer limit,
                                                             @QueryParam("sort") String sort,
                                                             @QueryParam("name") String name) {
        return RpcHandler.get(() -> {
            UfsPageRequest pageRequest = new UfsPageRequest(
                    RequireParam.notNull(page, "page"), RequireParam.notNull(limit, "limit"));
            return groupService.getGroups(pageRequest, sort, name);
        });
    }

    @GET
    @Path("/users")
    public BaseResponse<UfsPageListRs<UserDto>> getGroupUsers(@QueryParam("id") Long id,
                                                                @QueryParam("page") Integer page,
                                                                @QueryParam("limit") Integer limit,
                                                                @QueryParam("sort") String sort,
                                                                @QueryParam("lastName") String lastName,
                                                                @QueryParam("firstName") String firstName,
                                                                @QueryParam("tabNum") String tabNum) {
        return RpcHandler.get(() -> {
            UfsPageRequest pageRequest = new UfsPageRequest(
                    RequireParam.notNull(page, "page"), RequireParam.notNull(limit, "limit"));
            return groupService.getGroupUsers(RequireParam.notNull(id, "id"), pageRequest, sort, lastName, firstName, tabNum);
        });
    }

    @POST
    @Path("/{id}/users")
    public BaseResponse<Boolean> addGroupUsers(@PathParam("id") Long id, List<String> userIds) {
        return RpcHandler.get(() -> groupService.addGroupUsers(id, userIds));
    }

    @POST
    @Path("/{id}/users/remove")
    public BaseResponse<Boolean> removeGroupUsers(@PathParam("id") Long id, List<String> userIds) {
        return RpcHandler.get(() -> groupService.removeGroupUsers(id, userIds));
    }

    @GET
    @Path("/flags")
    public BaseResponse<List<GroupFlagDto>> getGroupFlags(@QueryParam("id") Long id) {
        return RpcHandler.get(() -> groupService.getGroupFlags(RequireParam.notNull(id, "id")));
    }

    @DELETE
    @Path("/{id}")
    public BaseResponse<Boolean> deleteGroup(@PathParam("id") Long id) {
        return RpcHandler.get(() -> groupService.deleteGroup(id));
    }
}
