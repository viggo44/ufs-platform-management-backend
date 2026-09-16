package ru.sbrf.platformmanagement.web.mapper;

import ru.sbrf.platformmanagement.domain.model.AppUserEntity;
import ru.sbrf.platformmanagement.ufs.api.model.UserCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.UserDto;
import ru.sbrf.platformmanagement.ufs.api.model.UserPatchDto;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(AppUserEntity entity) {
        return new UserDto(
                entity.getTabNum(),
                entity.getTabNum(),
                entity.getLastName(),
                entity.getFirstName(),
                entity.getMiddleName(),
                entity.getUserId());
    }

    public static AppUserEntity fromCreateRequest(UserCreateDto request) {
        AppUserEntity entity = new AppUserEntity();
        entity.setTabNum(request.getTabNum());
        entity.setLastName(request.getLastName());
        entity.setFirstName(request.getFirstName());
        entity.setMiddleName(request.getMiddleName());
        return entity;
    }

    /**
     * {@code tabNum} — табельный номер, не обязательно чисто числовой строкой (может
     * содержать буквы/префиксы), поэтому хранится как {@link String}.
     *
     * <p>{@code tabNum} здесь намеренно никогда не применяется — это первичный ключ
     * ({@code app_user.tab_num}), на который ссылаются внешние ключи остальных таблиц.
     * Поля, которые заполняет только логин ({@code userId}, {@code username} и т.д.),
     * тоже не трогаются — это ручное редактирование карточки, а не синхронизация.
     */
    public static void applyPatch(AppUserEntity entity, UserPatchDto request) {
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
