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
import ru.sbrf.platformmanagement.domain.model.FlagEntity;
import ru.sbrf.platformmanagement.domain.model.FlagGroupValueEntity;
import ru.sbrf.platformmanagement.domain.model.UserEntity;
import ru.sbrf.platformmanagement.domain.model.UserGroupEntity;
import ru.sbrf.platformmanagement.domain.repository.FlagGroupValueRepository;
import ru.sbrf.platformmanagement.domain.repository.FlagRepository;
import ru.sbrf.platformmanagement.domain.repository.GroupSpecifications;
import ru.sbrf.platformmanagement.domain.repository.Specifications;
import ru.sbrf.platformmanagement.domain.repository.UserGroupRepository;
import ru.sbrf.platformmanagement.domain.repository.UserRepository;
import ru.sbrf.platformmanagement.domain.repository.UserSpecifications;
import ru.sbrf.platformmanagement.ufs.api.model.model.FlagDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.GroupCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.GroupFlagDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.GroupPatchDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.GroupDto;
import ru.sbrf.platformmanagement.ufs.api.model.model.UfsPageListRs;
import ru.sbrf.platformmanagement.ufs.api.model.model.UfsPageRequest;
import ru.sbrf.platformmanagement.ufs.api.model.model.UserDto;
import ru.sbrf.platformmanagement.web.mapper.CommonMapper;
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
    private final UserRepository userRepository;
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
        Sort resolvedSort = SortResolver.resolve(sort, UserSpecifications.SORT_WHITELIST, "id");
        Pageable pageable = CommonMapper.map(pageRequest, resolvedSort);
        Specification<UserEntity> spec = Specifications.allOf(
                UserSpecifications.memberOfGroup(id),
                UserSpecifications.lastNameContains(lastName),
                UserSpecifications.firstNameContains(firstName),
                UserSpecifications.tabNumEquals(tabNum));
        Page<UserEntity> result = userRepository.findAll(spec, pageable);
        return UfsPageListRs.of(result.getContent().stream().map(UserMapper::toDto).toList(),
                pageRequest, result.getTotalPages(), result.getTotalElements());
    }

    @Transactional
    public boolean addGroupUsers(Long id, List<String> tabNums) {
        if (!userGroupRepository.existsById(id) || tabNums == null || tabNums.isEmpty()) {
            return false;
        }
        Long[] userIds = resolveTabNums(tabNums).toArray(Long[]::new);
        userGroupRepository.addMembers(id, userIds);
        return true;
    }

    @Transactional
    public boolean removeGroupUsers(Long id, List<String> tabNums) {
        if (!userGroupRepository.existsById(id) || tabNums == null || tabNums.isEmpty()) {
            return false;
        }
        userGroupRepository.removeMembers(id, resolveTabNums(tabNums));
        return true;
    }

    /** Публичный контракт передаёт tab_num — резолвим пачкой во внутренний id одним запросом. */
    private List<Long> resolveTabNums(List<String> tabNums) {
        List<UserEntity> users = userRepository.findAllByTabNumIn(tabNums);
        if (users.size() != tabNums.stream().distinct().count()) {
            throw new BadRequestException("One of the given user ids does not exist");
        }
        return users.stream().map(UserEntity::getId).toList();
    }

    public List<GroupFlagDto> getGroupFlags(Long id) {
        requireExists(id);
        List<FlagGroupValueEntity> values = flagGroupValueRepository.findAllByGroup_Id(id);
        Map<Long, FlagEntity> flagsById = flagRepository.findAllById(
                        values.stream().map(v -> v.getFlag().getId()).toList()).stream()
                .collect(Collectors.toMap(FlagEntity::getId, Function.identity()));

        return values.stream()
                .map(v -> {
                    FlagDto flag = FlagMapper.toDto(flagsById.get(v.getFlag().getId()));
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
        userGroupRepository.deleteAllByGroupId(id);
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
