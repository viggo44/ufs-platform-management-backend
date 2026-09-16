package ru.sbrf.platformmanagement.web.controller;

import jakarta.validation.Valid;
import jakarta.ws.rs.DefaultValue;
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
import ru.sbrf.platformmanagement.domain.service.UserService;
import ru.sbrf.platformmanagement.ufs.api.model.model.GroupDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.UfsPageListRs;
import ru.sbrf.platformmanagement.ufs.api.model.model.UfsPageRequest;
import ru.sbrf.platformmanagement.ufs.api.model.model.UserCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.UserDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.UserFlagDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.UserPatchDto;
import ru.sbrf.platformmanagement.web.mapper.RequireParam;
import ru.sbrf.ufs.platform.core.jaxrs.response.RpcHandler;
import ru.sbrf.ufs.platform.core.jaxrs.response.model.BaseResponse;

import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Component
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @POST
    public BaseResponse<UserDto> createUser(@Valid UserCreateDto request) {
        return RpcHandler.get(() -> userService.createUser(request));
    }

    @PATCH
    @Path("/{id}")
    public BaseResponse<UserDto> patchUser(@PathParam("id") String id, @Valid UserPatchDto request) {
        return RpcHandler.get(() -> userService.patchUser(id, request));
    }

    @GET
    public BaseResponse<UfsPageListRs<UserDto>> getUsers(@QueryParam("page") Integer page,
                                                           @QueryParam("limit") Integer limit,
                                                           @QueryParam("sort") String sort,
                                                           @QueryParam("lastName") String lastName,
                                                           @QueryParam("firstName") String firstName,
                                                           @QueryParam("tabNum") String tabNum) {
        return RpcHandler.get(() -> {
            UfsPageRequest pageRequest = new UfsPageRequest(
                    RequireParam.notNull(page, "page"), RequireParam.notNull(limit, "limit"));
            return userService.getUsers(pageRequest, sort, lastName, firstName, tabNum);
        });
    }

    @GET
    @Path("/groups")
    public BaseResponse<List<GroupDto>> getUserGroups(@QueryParam("id") String id) {
        return RpcHandler.get(() -> userService.getUserGroups(RequireParam.notNull(id, "id")));
    }

    @GET
    @Path("/flags")
    public BaseResponse<List<UserFlagDto>> getUserFlags(@QueryParam("id") String id,
                                                          @QueryParam("customOnly") @DefaultValue("false") boolean customOnly) {
        return RpcHandler.get(() -> userService.getUserFlags(RequireParam.notNull(id, "id"), customOnly));
    }

    @DELETE
    @Path("/{id}")
    public BaseResponse<Boolean> deleteUser(@PathParam("id") String id) {
        return RpcHandler.get(() -> userService.deleteUser(id));
    }
}
