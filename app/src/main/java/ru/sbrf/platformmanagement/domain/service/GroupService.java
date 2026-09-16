package ru.sbrf.platformmanagement.domain.service;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sbrf.platformmanagement.domain.model.AppUserEntity;
import ru.sbrf.platformmanagement.domain.model.FlagEntity;
import ru.sbrf.platformmanagement.domain.model.FlagGroupValueEntity;
import ru.sbrf.platformmanagement.domain.model.UserGroupEntity;
import ru.sbrf.platformmanagement.domain.model.UserGroupMemberEntity;
import ru.sbrf.platformmanagement.domain.repository.AppUserRepository;
import ru.sbrf.platformmanagement.domain.repository.AppUserSpecifications;
import ru.sbrf.platformmanagement.domain.repository.FlagGroupValueRepository;
import ru.sbrf.platformmanagement.domain.repository.FlagRepository;
import ru.sbrf.platformmanagement.domain.repository.GroupSpecifications;
import ru.sbrf.platformmanagement.domain.repository.Specifications;
import ru.sbrf.platformmanagement.domain.repository.UserGroupMemberRepository;
import ru.sbrf.platformmanagement.domain.repository.UserGroupRepository;
import ru.sbrf.platformmanagement.ufs.api.model.FlagDto;
import ru.sbrf.platformmanagement.ufs.api.model.GroupCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.GroupFlagDto;
import ru.sbrf.platformmanagement.ufs.api.model.GroupPatchDto;
import ru.sbrf.platformmanagement.ufs.api.model.GroupDto;
import ru.sbrf.platformmanagement.ufs.api.model.UfsPageListRs;
import ru.sbrf.platformmanagement.ufs.api.model.UfsPageRequest;
import ru.sbrf.platformmanagement.ufs.api.model.UserDto;
import ru.sbrf.platformmanagement.web.mapper.CommonMapper;
import ru.sbrf.platformmanagement.web.mapper.FlagMapper;
import ru.sbrf.platformmanagement.web.mapper.GroupMapper;
import ru.sbrf.platformmanagement.web.mapper.SortResolver;
import ru.sbrf.platformmanagement.web.mapper.UserMapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final UserGroupRepository userGroupRepository;
    private final AppUserRepository appUserRepository;
    private final UserGroupMemberRepository userGroupMemberRepository;
    private final FlagGroupValueRepository flagGroupValueRepository;
    private final FlagRepository flagRepository;

    @Transactional
    public GroupDto createGroup(GroupCreateDto request) {
        if (userGroupRepository.existsByName(request.getName())) {
            throw new BadRequestException("Group name already exists: " + request.getName());
        }
        UserGroupEntity entity = GroupMapper.fromCreateRequest(request);
        return GroupMapper.toDto(userGroupRepository.save(entity));
    }

    @Transactional
    public GroupDto patchGroup(Long id, GroupPatchDto request) {
        UserGroupEntity entity = getOrThrow(id);
        GroupMapper.applyPatch(entity, request);
        return GroupMapper.toDto(userGroupRepository.save(entity));
    }

    public UfsPageListRs<GroupDto> getGroups(UfsPageRequest pageRequest, String sort, String name) {
        Sort resolvedSort = SortResolver.resolve(sort, GroupSpecifications.SORT_WHITELIST, "id");
        Pageable pageable = CommonMapper.map(pageRequest, resolvedSort);
        Specification<UserGroupEntity> spec = Specifications.allOf(GroupSpecifications.nameContains(name));
        Page<UserGroupEntity> result = userGroupRepository.findAll(spec, pageable);
        return UfsPageListRs.of(result.getContent().stream().map(GroupMapper::toDto).toList(),
                pageRequest, result.getTotalPages(), result.getTotalElements());
    }

    public UfsPageListRs<UserDto> getGroupUsers(Long id, UfsPageRequest pageRequest, String sort,
                                                 String lastName, String firstName, String tabNum) {
        requireExists(id);
        Sort resolvedSort = SortResolver.resolve(sort, AppUserSpecifications.SORT_WHITELIST, "id");
        Pageable pageable = CommonMapper.map(pageRequest, resolvedSort);
        Specification<AppUserEntity> spec = Specifications.allOf(
                AppUserSpecifications.memberOfGroup(id),
                AppUserSpecifications.lastNameContains(lastName),
                AppUserSpecifications.firstNameContains(firstName),
                AppUserSpecifications.tabNumEquals(tabNum));
        Page<AppUserEntity> result = appUserRepository.findAll(spec, pageable);
        return UfsPageListRs.of(result.getContent().stream().map(UserMapper::toDto).toList(),
                pageRequest, result.getTotalPages(), result.getTotalElements());
    }

    @Transactional
    public boolean addGroupUsers(Long id, List<String> userIds) {
        if (!userGroupRepository.existsById(id) || userIds == null || userIds.isEmpty()) {
            return false;
        }
        try {
            List<UserGroupMemberEntity> rows = userIds.stream()
                    .map(userId -> new UserGroupMemberEntity(userId, id))
                    .toList();
            userGroupMemberRepository.saveAll(rows);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("One of the given user ids does not exist");
        }
        return true;
    }

    @Transactional
    public boolean removeGroupUsers(Long id, List<String> userIds) {
        if (!userGroupRepository.existsById(id) || userIds == null || userIds.isEmpty()) {
            return false;
        }
        userGroupMemberRepository.deleteByGroupIdAndUserIds(id, userIds);
        return true;
    }

    public List<GroupFlagDto> getGroupFlags(Long id) {
        requireExists(id);
        List<FlagGroupValueEntity> values = flagGroupValueRepository.findAllById_GroupId(id);
        Map<Long, FlagEntity> flagsById = flagRepository.findAllById(
                        values.stream().map(v -> v.getId().getFlagId()).toList()).stream()
                .collect(Collectors.toMap(FlagEntity::getId, Function.identity()));

        return values.stream()
                .map(v -> {
                    FlagDto flag = FlagMapper.toDto(flagsById.get(v.getId().getFlagId()));
                    return new GroupFlagDto(flag.getId(), flag.getName(), flag.getDesc(), v.isValue());
                })
                .toList();
    }

    @Transactional
    public boolean deleteGroup(Long id) {
        if (!userGroupRepository.existsById(id)) {
            return false;
        }
        flagGroupValueRepository.deleteAllByGroupId(id);
        userGroupMemberRepository.deleteAllByGroupId(id);
        userGroupRepository.deleteById(id);
        return true;
    }

    private UserGroupEntity getOrThrow(Long id) {
        return userGroupRepository.findById(id).orElseThrow(() -> new NotFoundException("Group " + id + " not found"));
    }

    private void requireExists(Long id) {
        if (!userGroupRepository.existsById(id)) {
            throw new NotFoundException("Group " + id + " not found");
        }
    }
}
