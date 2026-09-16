package ru.sbrf.platformmanagement.web.mapper;

import ru.sbrf.platformmanagement.domain.model.UserGroupEntity;
import ru.sbrf.platformmanagement.ufs.api.model.model.GroupCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.GroupDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.GroupPatchDto;

public final class GroupMapper {

    private GroupMapper() {
    }

    public static GroupDto toDto(UserGroupEntity entity) {
        return new GroupDto(entity.getId(), entity.getName(), entity.getDescription());
    }

    public static UserGroupEntity fromCreateRequest(GroupCreateDto request) {
        UserGroupEntity entity = new UserGroupEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDesc());
        return entity;
    }

    public static void applyPatch(UserGroupEntity entity, GroupPatchDto request) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getDesc() != null) {
            entity.setDescription(request.getDesc());
        }
    }
}
