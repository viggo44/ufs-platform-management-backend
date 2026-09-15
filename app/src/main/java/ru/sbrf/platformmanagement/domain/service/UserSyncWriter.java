package ru.sbrf.platformmanagement.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.sbrf.platformmanagement.domain.model.AppUserEntity;
import ru.sbrf.platformmanagement.domain.model.AppUserPermissionEntity;
import ru.sbrf.platformmanagement.domain.model.AppUserRoleEntity;
import ru.sbrf.platformmanagement.domain.model.UserSnapshot;
import ru.sbrf.platformmanagement.domain.repository.AppUserPermissionRepository;
import ru.sbrf.platformmanagement.domain.repository.AppUserRepository;
import ru.sbrf.platformmanagement.domain.repository.AppUserRoleRepository;
import ru.sbrf.platformmanagement.domain.repository.PermissionDictRepository;
import ru.sbrf.platformmanagement.domain.repository.RoleDictRepository;

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

    private final AppUserRepository appUserRepository;
    private final RoleDictRepository roleDictRepository;
    private final PermissionDictRepository permissionDictRepository;
    private final AppUserRoleRepository appUserRoleRepository;
    private final AppUserPermissionRepository appUserPermissionRepository;

    @Transactional
    public void upsert(UserSnapshot snapshot) {
        Long tabNum = snapshot.employeeNumberParsed();
        upsertAppUser(tabNum, snapshot);
        syncRoles(tabNum, snapshot.roleCodes());
        syncPermissions(tabNum, snapshot.permissionCodes());
    }

    /**
     * На вставку (человек ещё не заведён через {@code createUser} и логинится впервые) имя/
     * фамилия берутся из ССД. На обновление ССД считается источником истины и перезаписывает
     * ФИО, даже если их правили вручную через {@code patchUser}.
     */
    private void upsertAppUser(Long tabNum, UserSnapshot s) {
        AppUserEntity entity = appUserRepository.findById(tabNum).orElseGet(AppUserEntity::new);
        entity.setTabNum(tabNum);
        entity.setLastName(s.lastName());
        entity.setFirstName(s.firstName());
        entity.setMiddleName(s.middleName());
        entity.setUserId(s.userId());
        entity.setUsername(s.username());
        entity.setFullName(s.fullName());
        entity.setDepartmentNumber(s.departmentNumber());
        entity.setIssuer(s.issuer());
        entity.setFingerprint(s.fingerprint());
        entity.setLastSyncedAt(Instant.now());
        appUserRepository.save(entity);
    }

    private void syncRoles(Long tabNum, List<String> codes) {
        if (codes.isEmpty()) {
            appUserRoleRepository.deleteAllByUserId(tabNum);
            return;
        }
        appUserRoleRepository.deleteStale(tabNum, codes);

        Set<String> existing = appUserRoleRepository.findAllById_UserId(tabNum).stream()
                .map(r -> r.getId().getRoleCode())
                .collect(Collectors.toSet());
        List<String> newCodes = codes.stream().filter(code -> !existing.contains(code)).toList();
        if (newCodes.isEmpty()) {
            return;
        }

        upsertRoleDictionary(newCodes);
        appUserRoleRepository.saveAll(newCodes.stream()
                .map(code -> new AppUserRoleEntity(tabNum, code))
                .toList());
    }

    private void syncPermissions(Long tabNum, List<String> codes) {
        if (codes.isEmpty()) {
            appUserPermissionRepository.deleteAllByUserId(tabNum);
            return;
        }
        appUserPermissionRepository.deleteStale(tabNum, codes);

        Set<String> existing = appUserPermissionRepository.findAllById_UserId(tabNum).stream()
                .map(p -> p.getId().getPermissionCode())
                .collect(Collectors.toSet());
        List<String> newCodes = codes.stream().filter(code -> !existing.contains(code)).toList();
        if (newCodes.isEmpty()) {
            return;
        }

        upsertPermissionDictionary(newCodes);
        appUserPermissionRepository.saveAll(newCodes.stream()
                .map(code -> new AppUserPermissionEntity(tabNum, code))
                .toList());
    }

    /**
     * Вызывается только с кодами, новыми для этого пользователя — не со всем его списком.
     * {@code upsertMissing} атомарен ({@code ON CONFLICT DO NOTHING}), поэтому не нужно
     * заранее проверять, чего уже нет в справочнике — конкурентная вставка того же кода
     * другим логином просто станет no-op, а не гонкой check-then-insert.
     */
    private void upsertRoleDictionary(List<String> newCodes) {
        roleDictRepository.upsertMissing(newCodes.toArray(String[]::new));
    }

    private void upsertPermissionDictionary(List<String> newCodes) {
        permissionDictRepository.upsertMissing(newCodes.toArray(String[]::new));
    }
}
