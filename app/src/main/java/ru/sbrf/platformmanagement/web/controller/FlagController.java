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
import ru.sbrf.platformmanagement.domain.service.FlagService;
import ru.sbrf.platformmanagement.ufs.api.model.FlagCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.FlagDto;
import ru.sbrf.platformmanagement.ufs.api.model.FlagInfoDeleteDto;
import ru.sbrf.platformmanagement.ufs.api.model.FlagInfoDto;
import ru.sbrf.platformmanagement.ufs.api.model.FlagInfoPatchDto;
import ru.sbrf.platformmanagement.ufs.api.model.FlagPatchDto;
import ru.sbrf.platformmanagement.ufs.api.model.UfsPageListRs;
import ru.sbrf.platformmanagement.ufs.api.model.UfsPageRequest;
import ru.sbrf.platformmanagement.web.mapper.RequireParam;

@Path("/flags")
@Produces(MediaType.APPLICATION_JSON)
@Component
@RequiredArgsConstructor
public class FlagController {

    private final FlagService flagService;

    @POST
    public FlagDto createFlag(@Valid FlagCreateDto request) {
        return flagService.createFlag(request);
    }

    @PATCH
    public FlagDto patchFlag(@QueryParam("id") Long id, @Valid FlagPatchDto request) {
        return flagService.patchFlag(RequireParam.notNull(id, "id"), request);
    }

    @GET
    public UfsPageListRs<FlagDto> getFlags(@QueryParam("page") Integer page,
                                            @QueryParam("limit") Integer limit,
                                            @QueryParam("sort") String sort,
                                            @QueryParam("name") String name) {
        UfsPageRequest pageRequest = new UfsPageRequest(
                RequireParam.notNull(page, "page"), RequireParam.notNull(limit, "limit"));
        return flagService.getFlags(pageRequest, sort, name);
    }

    @GET
    @Path("/info")
    public FlagInfoDto getFlagInfo(@QueryParam("id") Long id) {
        return flagService.getFlagInfo(RequireParam.notNull(id, "id"));
    }

    @PATCH
    @Path("/info")
    public FlagInfoDto patchFlagInfo(@QueryParam("id") Long id, FlagInfoPatchDto request) {
        return flagService.patchFlagInfo(RequireParam.notNull(id, "id"), request);
    }

    @DELETE
    @Path("/info")
    public boolean deleteFlagInfo(@QueryParam("id") Long id, FlagInfoDeleteDto request) {
        return flagService.deleteFlagInfo(RequireParam.notNull(id, "id"), request);
    }

    @DELETE
    public boolean deleteFlag(@QueryParam("id") Long id) {
        return flagService.deleteFlag(RequireParam.notNull(id, "id"));
    }
}
