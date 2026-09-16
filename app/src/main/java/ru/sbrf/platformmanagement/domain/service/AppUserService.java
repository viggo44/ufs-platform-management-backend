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
import ru.sbrf.platformmanagement.domain.model.AppUserEntity;
import ru.sbrf.platformmanagement.domain.repository.AppUserRepository;
import ru.sbrf.platformmanagement.domain.repository.AppUserSpecifications;
import ru.sbrf.platformmanagement.domain.repository.FlagResolutionRepository;
import ru.sbrf.platformmanagement.domain.repository.FlagUserValueRepository;
import ru.sbrf.platformmanagement.domain.repository.Specifications;
import ru.sbrf.platformmanagement.domain.repository.UserFlagResolutionRow;
import ru.sbrf.platformmanagement.domain.repository.UserGroupMemberRepository;
import ru.sbrf.platformmanagement.domain.repository.UserGroupRepository;
import ru.sbrf.platformmanagement.ufs.api.model.FlagType;
import ru.sbrf.platformmanagement.ufs.api.model.GroupDto;
import ru.sbrf.platformmanagement.ufs.api.model.UfsPageListRs;
import ru.sbrf.platformmanagement.ufs.api.model.UfsPageRequest;
import ru.sbrf.platformmanagement.ufs.api.model.UserCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.UserDto;
import ru.sbrf.platformmanagement.ufs.api.model.UserFlagDto;
import ru.sbrf.platformmanagement.ufs.api.model.UserPatchDto;
import ru.sbrf.platformmanagement.web.mapper.CommonMapper;
import ru.sbrf.platformmanagement.web.mapper.GroupMapper;
import ru.sbrf.platformmanagement.web.mapper.SortResolver;
import ru.sbrf.platformmanagement.web.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserGroupMemberRepository userGroupMemberRepository;
    private final FlagUserValueRepository flagUserValueRepository;
    private final FlagResolutionRepository flagResolutionRepository;

    @Transactional
    public UserDto createUser(UserCreateDto request) {
        if (appUserRepository.existsById(request.getTabNum())) {
            throw new BadRequestException("User with tabNum already exists: " + request.getTabNum());
        }
        AppUserEntity entity = UserMapper.fromCreateRequest(request);
        return UserMapper.toDto(appUserRepository.save(entity));
    }

    @Transactional
    public UserDto patchUser(String id, UserPatchDto request) {
        AppUserEntity entity = getOrThrow(id);
        UserMapper.applyPatch(entity, request);
        return UserMapper.toDto(appUserRepository.save(entity));
    }

    public UfsPageListRs<UserDto> getUsers(UfsPageRequest pageRequest, String sort,
                                            String lastName, String firstName, String tabNum) {
        Sort resolvedSort = SortResolver.resolve(sort, AppUserSpecifications.SORT_WHITELIST, "id");
        Pageable pageable = CommonMapper.map(pageRequest, resolvedSort);
        Specification<AppUserEntity> spec = Specifications.allOf(
                AppUserSpecifications.lastNameContains(lastName),
                AppUserSpecifications.firstNameContains(firstName),
                AppUserSpecifications.tabNumEquals(tabNum));
        Page<AppUserEntity> result = appUserRepository.findAll(spec, pageable);
        return UfsPageListRs.of(result.getContent().stream().map(UserMapper::toDto).toList(),
                pageRequest, result.getTotalPages(), result.getTotalElements());
    }

    public List<GroupDto> getUserGroups(String id) {
        requireExists(id);
        return userGroupRepository.findAllByMemberUserId(id).stream()
                .map(GroupMapper::toDto)
                .toList();
    }

    /** Один native-запрос, см. {@link FlagResolutionRepository#resolveForUser}. */
    public List<UserFlagDto> getUserFlags(String id, boolean customOnly) {
        requireExists(id);
        return flagResolutionRepository.resolveForUser(id, customOnly).stream()
                .map(this::toUserFlagDto)
                .toList();
    }

    @Transactional
    public boolean deleteUser(String id) {
        if (!appUserRepository.existsById(id)) {
            return false;
        }
        flagUserValueRepository.deleteAllByUserId(id);
        userGroupMemberRepository.deleteAllByUserId(id);
        appUserRepository.deleteById(id);
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

    private AppUserEntity getOrThrow(String id) {
        return appUserRepository.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }

    private void requireExists(String id) {
        if (!appUserRepository.existsById(id)) {
            throw new NotFoundException("User " + id + " not found");
        }
    }
}
