package ru.sbrf.platformmanagement.web.mapper;

import ru.sbrf.platformmanagement.domain.model.FlagEntity;
import ru.sbrf.platformmanagement.ufs.api.model.model.FlagCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.FlagDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.FlagPatchDto;

public final class FlagMapper {

    private FlagMapper() {
    }

    public static FlagDto toDto(FlagEntity entity) {
        return new FlagDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isDefaultValue(),
                entity.isWithCustomization());
    }

    public static FlagEntity fromCreateRequest(FlagCreateDto request) {
        FlagEntity entity = new FlagEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDesc());
        entity.setDefaultValue(request.getDefaultValue() != null ? request.getDefaultValue() : false);
        entity.setWithCustomization(request.getWithCustomization() != null ? request.getWithCustomization() : true);
        return entity;
    }

    public static void applyPatch(FlagEntity entity, FlagPatchDto request) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getDesc() != null) {
            entity.setDescription(request.getDesc());
        }
        if (request.getDefaultValue() != null) {
            entity.setDefaultValue(request.getDefaultValue());
        }
        if (request.getWithCustomization() != null) {
            entity.setWithCustomization(request.getWithCustomization());
        }
    }
}
