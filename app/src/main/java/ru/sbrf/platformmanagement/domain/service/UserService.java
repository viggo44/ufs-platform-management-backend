package ru.sbrf.platformmanagement.domain.service;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sbrf.platformmanagement.domain.model.UserEntity;
import ru.sbrf.platformmanagement.domain.repository.FlagResolutionRepository;
import ru.sbrf.platformmanagement.domain.repository.FlagUserValueRepository;
import ru.sbrf.platformmanagement.domain.repository.Specifications;
import ru.sbrf.platformmanagement.domain.repository.UserFlagResolutionRow;
import ru.sbrf.platformmanagement.domain.repository.UserGroupRepository;
import ru.sbrf.platformmanagement.domain.repository.UserRepository;
import ru.sbrf.platformmanagement.domain.repository.UserSpecifications;
import ru.sbrf.platformmanagement.ufs.api.model.model.FlagType;
import ru.sbrf.platformmanagement.ufs.api.model.model.GroupDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.UfsPageListRs;
import ru.sbrf.platformmanagement.ufs.api.model.model.UfsPageRequest;
import ru.sbrf.platformmanagement.ufs.api.model.model.UserCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.UserDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.UserFlagDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.UserPatchDto;
import ru.sbrf.platformmanagement.web.mapper.CommonMapper;
import ru.sbrf.platformmanagement.web.mapper.GroupMapper;
import ru.sbrf.platformmanagement.web.mapper.SortResolver;
import ru.sbrf.platformmanagement.web.mapper.UserMapper;

import java.util.List;

/**
 * Публичный контракт по-прежнему адресует пользователя по {@code tabNum} (согласовано —
 * в {@code createUser} по ТЗ нет поля {@code userId}). Внутри БД PK — суррогатный
 * {@code id}, поэтому каждый метод сначала резолвит {@code tabNum -> UserEntity} и уже
 * из неё берёт {@code entity.getId()} для запросов в связанные таблицы.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final FlagUserValueRepository flagUserValueRepository;
    private final FlagResolutionRepository flagResolutionRepository;

    @Transactional
    public UserDto createUser(UserCreateDto request) {
        if (userRepository.existsByTabNum(request.getTabNum())) {
            throw new BadRequestException("User with tabNum already exists: " + request.getTabNum());
        }
        UserEntity entity = UserMapper.fromCreateRequest(request);
        return UserMapper.toDto(userRepository.save(entity));
    }

    @Transactional
    public UserDto patchUser(String id, UserPatchDto request) {
        UserEntity entity = getOrThrow(id);
        UserMapper.applyPatch(entity, request);
        return UserMapper.toDto(userRepository.save(entity));
    }

    public UfsPageListRs<UserDto> getUsers(UfsPageRequest pageRequest, String sort,
                                            String lastName, String firstName, String tabNum) {
        Sort resolvedSort = SortResolver.resolve(sort, UserSpecifications.SORT_WHITELIST, "id");
        Pageable pageable = CommonMapper.map(pageRequest, resolvedSort);
        Specification<UserEntity> spec = Specifications.allOf(
                UserSpecifications.lastNameContains(lastName),
                UserSpecifications.firstNameContains(firstName),
                UserSpecifications.tabNumEquals(tabNum));
        Page<UserEntity> result = userRepository.findAll(spec, pageable);
        return UfsPageListRs.of(result.getContent().stream().map(UserMapper::toDto).toList(),
                pageRequest, result.getTotalPages(), result.getTotalElements());
    }

    public List<GroupDto> getUserGroups(String id) {
        UserEntity entity = getOrThrow(id);
        return userGroupRepository.findAllByMemberUserId(entity.getId()).stream()
                .map(GroupMapper::toDto)
                .toList();
    }

    /** Один native-запрос, см. {@link FlagResolutionRepository#resolveForUser}. */
    public List<UserFlagDto> getUserFlags(String id, boolean customOnly) {
        UserEntity entity = getOrThrow(id);
        return flagResolutionRepository.resolveForUser(entity.getId(), customOnly).stream()
                .map(this::toUserFlagDto)
                .toList();
    }

    @Transactional
    public boolean deleteUser(String id) {
        UserEntity entity = userRepository.findByTabNum(id).orElse(null);
        if (entity == null) {
            return false;
        }
        flagUserValueRepository.deleteAllByUserId(entity.getId());
        userGroupRepository.deleteAllByUserId(entity.getId());
        userRepository.delete(entity);
        return true;
    }

    private UserFlagDto toUserFlagDto(UserFlagResolutionRow row) {
        return new UserFlagDto(
                row.getId(),
                row.getName(),
                row.getDescription(),
                FlagType.fromWireValue(row.getType()),
                row.getValue());
    }

    private UserEntity getOrThrow(String id) {
        return userRepository.findByTabNum(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }
}
