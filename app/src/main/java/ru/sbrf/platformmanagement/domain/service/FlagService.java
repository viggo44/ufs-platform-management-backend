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
import ru.sbrf.platformmanagement.domain.model.FlagEntity;
import ru.sbrf.platformmanagement.domain.model.FlagGroupValueEntity;
import ru.sbrf.platformmanagement.domain.model.FlagUserValueEntity;
import ru.sbrf.platformmanagement.domain.model.UserGroupEntity;
import ru.sbrf.platformmanagement.domain.repository.AppUserRepository;
import ru.sbrf.platformmanagement.domain.repository.FlagGroupValueRepository;
import ru.sbrf.platformmanagement.domain.repository.FlagRepository;
import ru.sbrf.platformmanagement.domain.repository.FlagSpecifications;
import ru.sbrf.platformmanagement.domain.repository.FlagUserValueRepository;
import ru.sbrf.platformmanagement.domain.repository.Specifications;
import ru.sbrf.platformmanagement.domain.repository.UserGroupRepository;
import ru.sbrf.platformmanagement.ufs.api.model.FlagCreateDto;
import ru.sbrf.platformmanagement.ufs.api.model.FlagDto;
import ru.sbrf.platformmanagement.ufs.api.model.FlagInfoDeleteDto;
import ru.sbrf.platformmanagement.ufs.api.model.FlagInfoDto;
import ru.sbrf.platformmanagement.ufs.api.model.FlagInfoPatchDto;
import ru.sbrf.platformmanagement.ufs.api.model.FlagPatchDto;
import ru.sbrf.platformmanagement.ufs.api.model.GroupDto;
import ru.sbrf.platformmanagement.ufs.api.model.UfsPageListRs;
import ru.sbrf.platformmanagement.ufs.api.model.UfsPageRequest;
import ru.sbrf.platformmanagement.ufs.api.model.UserDto;
import ru.sbrf.platformmanagement.ufs.api.model.ValuedFlagDto;
import ru.sbrf.platformmanagement.web.mapper.CommonMapper;
import ru.sbrf.platformmanagement.web.mapper.FlagMapper;
import ru.sbrf.platformmanagement.web.mapper.GroupMapper;
import ru.sbrf.platformmanagement.web.mapper.SortResolver;
import ru.sbrf.platformmanagement.web.mapper.UserMapper;
import ru.sbrf.platformmanagement.domain.model.AppUserEntity;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlagService {

    private final FlagRepository flagRepository;
    private final FlagUserValueRepository flagUserValueRepository;
    private final FlagGroupValueRepository flagGroupValueRepository;
    private final AppUserRepository appUserRepository;
    private final UserGroupRepository userGroupRepository;

    @Transactional
    public FlagDto createFlag(FlagCreateDto request) {
        if (flagRepository.existsByName(request.getName())) {
            throw new BadRequestException("Flag name already exists: " + request.getName());
        }
        FlagEntity entity = FlagMapper.fromCreateRequest(request);
        return FlagMapper.toDto(flagRepository.save(entity));
    }

    @Transactional
    public FlagDto patchFlag(Long id, FlagPatchDto request) {
        FlagEntity entity = getOrThrow(id);
        FlagMapper.applyPatch(entity, request);
        return FlagMapper.toDto(flagRepository.save(entity));
    }

    public UfsPageListRs<FlagDto> getFlags(UfsPageRequest pageRequest, String sort, String name) {
        Sort resolvedSort = SortResolver.resolve(sort, FlagSpecifications.SORT_WHITELIST, "id");
        Pageable pageable = CommonMapper.map(pageRequest, resolvedSort);
        Specification<FlagEntity> spec = Specifications.allOf(FlagSpecifications.nameContains(name));
        Page<FlagEntity> result = flagRepository.findAll(spec, pageable);
        return UfsPageListRs.of(result.getContent().stream().map(FlagMapper::toDto).toList(),
                pageRequest, result.getTotalPages(), result.getTotalElements());
    }

    public FlagInfoDto getFlagInfo(Long id) {
        requireExists(id);
        return buildFlagInfo(id);
    }

    @Transactional
    public FlagInfoDto patchFlagInfo(Long id, FlagInfoPatchDto request) {
        requireExists(id);
        try {
            if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
                List<FlagUserValueEntity> rows = request.getUserIds().stream()
                        .map(v -> new FlagUserValueEntity(id, v.getData(), v.getValue()))
                        .toList();
                flagUserValueRepository.saveAll(rows);
            }
            if (request.getGroupIds() != null && !request.getGroupIds().isEmpty()) {
                List<FlagGroupValueEntity> rows = request.getGroupIds().stream()
                        .map(v -> new FlagGroupValueEntity(id, v.getData(), v.getValue()))
                        .toList();
                flagGroupValueRepository.saveAll(rows);
            }
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("One of the given user/group ids does not exist");
        }
        return buildFlagInfo(id);
    }

    @Transactional
    public boolean deleteFlagInfo(Long id, FlagInfoDeleteDto request) {
        if (!flagRepository.existsById(id)) {
            return false;
        }
        if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
            flagUserValueRepository.deleteByFlagIdAndUserIds(id, request.getUserIds());
        }
        if (request.getGroupIds() != null && !request.getGroupIds().isEmpty()) {
            flagGroupValueRepository.deleteByFlagIdAndGroupIds(id, request.getGroupIds());
        }
        return true;
    }

    @Transactional
    public boolean deleteFlag(Long id) {
        if (!flagRepository.existsById(id)) {
            return false;
        }
        flagUserValueRepository.deleteAllByFlagId(id);
        flagGroupValueRepository.deleteAllByFlagId(id);
        flagRepository.deleteById(id);
        return true;
    }

    private FlagInfoDto buildFlagInfo(Long flagId) {
        List<FlagUserValueEntity> userValues = flagUserValueRepository.findAllById_FlagId(flagId);
        Map<Long, AppUserEntity> usersById = index(
                appUserRepository.findAllById(userValues.stream().map(v -> v.getId().getUserId()).toList()),
                AppUserEntity::getTabNum);
        List<ValuedFlagDto<UserDto>> users = userValues.stream()
                .map(v -> new ValuedFlagDto<>(UserMapper.toDto(usersById.get(v.getId().getUserId())), v.isValue()))
                .toList();

        List<FlagGroupValueEntity> groupValues = flagGroupValueRepository.findAllById_FlagId(flagId);
        Map<Long, UserGroupEntity> groupsById = index(
                userGroupRepository.findAllById(groupValues.stream().map(v -> v.getId().getGroupId()).toList()),
                UserGroupEntity::getId);
        List<ValuedFlagDto<GroupDto>> groups = groupValues.stream()
                .map(v -> new ValuedFlagDto<>(GroupMapper.toDto(groupsById.get(v.getId().getGroupId())), v.isValue()))
                .toList();

        return new FlagInfoDto(users, groups);
    }

    private <T, K> Map<K, T> index(List<T> items, Function<T, K> keyFn) {
        return items.stream().collect(Collectors.toMap(keyFn, Function.identity()));
    }

    private FlagEntity getOrThrow(Long id) {
        return flagRepository.findById(id).orElseThrow(() -> new NotFoundException("Flag " + id + " not found"));
    }

    private void requireExists(Long id) {
        if (!flagRepository.existsById(id)) {
            throw new NotFoundException("Flag " + id + " not found");
        }
    }
}
