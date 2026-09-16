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
import ru.sbrf.platformmanagement.domain.service.FlagService;
import ru.sbrf.platformmanagement.ufs.api.model.model.FlagCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.FlagDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.FlagInfoDeleteDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.FlagInfoDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.FlagInfoPatchDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.FlagPatchDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.UfsPageListRs;
import ru.sbrf.platformmanagement.ufs.api.model.model.UfsPageRequest;
import ru.sbrf.platformmanagement.web.mapper.RequireParam;
import ru.sbrf.ufs.platform.core.jaxrs.response.RpcHandler;
import ru.sbrf.ufs.platform.core.jaxrs.response.model.BaseResponse;

@Path("/flags")
@Produces(MediaType.APPLICATION_JSON)
@Component
@RequiredArgsConstructor
public class FlagController {

    private final FlagService flagService;

    @POST
    public BaseResponse<FlagDto> createFlag(@Valid FlagCreateDto request) {
        return RpcHandler.get(() -> flagService.createFlag(request));
    }

    @PATCH
    @Path("/{id}")
    public BaseResponse<FlagDto> patchFlag(@PathParam("id") Long id, @Valid FlagPatchDto request) {
        return RpcHandler.get(() -> flagService.patchFlag(id, request));
    }

    @GET
    public BaseResponse<UfsPageListRs<FlagDto>> getFlags(@QueryParam("page") Integer page,
                                                           @QueryParam("limit") Integer limit,
                                                           @QueryParam("sort") String sort,
                                                           @QueryParam("name") String name) {
        return RpcHandler.get(() -> {
            UfsPageRequest pageRequest = new UfsPageRequest(
                    RequireParam.notNull(page, "page"), RequireParam.notNull(limit, "limit"));
            return flagService.getFlags(pageRequest, sort, name);
        });
    }

    @GET
    @Path("/info")
    public BaseResponse<FlagInfoDto> getFlagInfo(@QueryParam("id") Long id) {
        return RpcHandler.get(() -> flagService.getFlagInfo(RequireParam.notNull(id, "id")));
    }

    @PATCH
    @Path("/{id}/info")
    public BaseResponse<FlagInfoDto> patchFlagInfo(@PathParam("id") Long id, FlagInfoPatchDto request) {
        return RpcHandler.get(() -> flagService.patchFlagInfo(id, request));
    }

    @DELETE
    @Path("/{id}/info")
    public BaseResponse<Boolean> deleteFlagInfo(@PathParam("id") Long id, FlagInfoDeleteDto request) {
        return RpcHandler.get(() -> flagService.deleteFlagInfo(id, request));
    }

    @DELETE
    @Path("/{id}")
    public BaseResponse<Boolean> deleteFlag(@PathParam("id") Long id) {
        return RpcHandler.get(() -> flagService.deleteFlag(id));
    }
}
