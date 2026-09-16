package ru.sbrf.platformmanagement.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.sbrf.platformmanagement.domain.model.UserEntity;
import ru.sbrf.platformmanagement.domain.model.UserPermissionEntity;
import ru.sbrf.platformmanagement.domain.model.UserSnapshot;
import ru.sbrf.platformmanagement.domain.model.UserSudirRoleEntity;
import ru.sbrf.platformmanagement.domain.repository.PermissionDictRepository;
import ru.sbrf.platformmanagement.domain.repository.SudirRoleDictRepository;
import ru.sbrf.platformmanagement.domain.repository.UserPermissionRepository;
import ru.sbrf.platformmanagement.domain.repository.UserRepository;
import ru.sbrf.platformmanagement.domain.repository.UserSudirRoleRepository;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сохраняет {@link UserSnapshot} через обычные Spring Data JPA-репозитории. Роли и
 * пермишены синхронизируются **только дельтой**: сначала читаем, что у пользователя уже
 * привязано, и трогаем (справочник + связи) исключительно те коды, которых там ещё нет.
 * В устоявшемся состоянии (ничего не поменялось) на роли/пермишены уходит всего 2 запроса
 * на каждую сторону (bulk-delete устаревших + один select текущих), независимо от того,
 * сколько всего кодов у пользователя. Дополнительная работа появляется только под реально
 * новые коды — которых обычно 0.
 *
 * <p>Названо {@code *Writer}, а не {@code *Repository} — это не Spring Data репозиторий,
 * а класс, оркеструющий несколько настоящих репозиториев (папка {@code domain.service}).
 */
@Component
@RequiredArgsConstructor
public class UserSyncWriter {

    private final UserRepository userRepository;
    private final SudirRoleDictRepository sudirRoleDictRepository;
    private final PermissionDictRepository permissionDictRepository;
    private final UserSudirRoleRepository userSudirRoleRepository;
    private final UserPermissionRepository userPermissionRepository;

    @Transactional
    public void upsert(UserSnapshot snapshot) {
        Long userId = upsertUser(snapshot.tabNum(), snapshot);
        syncRoles(userId, snapshot.roleCodes());
        syncPermissions(userId, snapshot.permissionCodes());
    }

    /**
     * На вставку (человек ещё не заведён через {@code createUser} и логинится впервые) имя/
     * фамилия берутся из ССД. На обновление ССД считается источником истины и перезаписывает
     * ФИО, даже если их правили вручную через {@code patchUser}. Ищем и апсертим по
     * {@code tabNum} — {@code id} для нового пользователя ещё не существует и назначается
     * базой только при вставке, поэтому возвращаем его отдельно для линковки ролей/пермишенов.
     */
    private Long upsertUser(String tabNum, UserSnapshot s) {
        UserEntity entity = userRepository.findByTabNum(tabNum).orElseGet(UserEntity::new);
        entity.setTabNum(tabNum);
        entity.setLastName(s.lastName());
        entity.setFirstName(s.firstName());
        entity.setMiddleName(s.middleName());
        entity.setLogin(s.login());
        entity.setUsername(s.username());
        entity.setFullName(s.fullName());
        entity.setDepartmentNumber(s.departmentNumber());
        entity.setIssuer(s.issuer());
        entity.setFingerprint(s.fingerprint());
        entity.setLastSyncedAt(Instant.now());
        return userRepository.save(entity).getId();
    }

    private void syncRoles(Long userId, List<String> codes) {
        if (codes.isEmpty()) {
            userSudirRoleRepository.deleteAllByUserId(userId);
            return;
        }
        userSudirRoleRepository.deleteStale(userId, codes);

        Set<String> existing = userSudirRoleRepository.findAllById_UserId(userId).stream()
                .map(r -> r.getId().getSudirRoleCode())
                .collect(Collectors.toSet());
        List<String> newCodes = codes.stream().filter(code -> !existing.contains(code)).toList();
        if (newCodes.isEmpty()) {
            return;
        }

        sudirRoleDictRepository.upsertMissing(newCodes.toArray(String[]::new));
        userSudirRoleRepository.saveAll(newCodes.stream()
                .map(code -> new UserSudirRoleEntity(userId, code))
                .toList());
    }

    private void syncPermissions(Long userId, List<String> codes) {
        if (codes.isEmpty()) {
            userPermissionRepository.deleteAllByUserId(userId);
            return;
        }
        userPermissionRepository.deleteStale(userId, codes);

        Set<String> existing = userPermissionRepository.findAllById_UserId(userId).stream()
                .map(p -> p.getId().getPermissionCode())
                .collect(Collectors.toSet());
        List<String> newCodes = codes.stream().filter(code -> !existing.contains(code)).toList();
        if (newCodes.isEmpty()) {
            return;
        }

        permissionDictRepository.upsertMissing(newCodes.toArray(String[]::new));
        userPermissionRepository.saveAll(newCodes.stream()
                .map(code -> new UserPermissionEntity(userId, code))
                .toList());
    }
}
