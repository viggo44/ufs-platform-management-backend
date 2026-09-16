package ru.sbrf.platformmanagement.web.mapper;

import ru.sbrf.platformmanagement.domain.model.UserEntity;
import ru.sbrf.platformmanagement.ufs.api.model.UserCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.UserDto;
import ru.sbrf.platformmanagement.ufs.api.model.UserPatchDto;

public final class UserMapper {

    private UserMapper() {
    }

    /**
     * {@code UserDto.id}/{@code UserDto.tabNum} здесь всегда равны — публичный API
     * адресует пользователя по табельному номеру (согласовано, в {@code createUser} по
     * ТЗ нет поля {@code userId}). Внутренний суррогатный {@code UserEntity.id} наружу
     * не отдаётся вообще — это чисто FK-якорь в БД.
     */
    public static UserDto toDto(UserEntity entity) {
        return new UserDto(
                entity.getTabNum(),
                entity.getTabNum(),
                entity.getLastName(),
                entity.getFirstName(),
                entity.getMiddleName(),
                entity.getLogin());
    }

    public static UserEntity fromCreateRequest(UserCreateDto request) {
        UserEntity entity = new UserEntity();
        entity.setTabNum(request.getTabNum());
        entity.setLastName(request.getLastName());
        entity.setFirstName(request.getFirstName());
        entity.setMiddleName(request.getMiddleName());
        return entity;
    }

    /**
     * {@code tabNum} здесь намеренно никогда не применяется — это уникальный бизнес-ключ,
     * по которому сервисный слой резолвит пользователя, менять его через {@code patchUser}
     * не предполагается. Поля, которые заполняет только логин ({@code login},
     * {@code username} и т.д.), тоже не трогаются — это ручное редактирование карточки,
     * а не синхронизация.
     */
    public static void applyPatch(UserEntity entity, UserPatchDto request) {
        if (request.getLastName() != null) {
            entity.setLastName(request.getLastName());
        }
        if (request.getFirstName() != null) {
            entity.setFirstName(request.getFirstName());
        }
        if (request.getMiddleName() != null) {
            entity.setMiddleName(request.getMiddleName());
        }
    }
}
