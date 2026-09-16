package ru.sbrf.platformmanagement.domain.service;

import org.springframework.stereotype.Component;
import ru.sbrf.platformmanagement.domain.model.UserSnapshot;
import ru.sbrf.platformmanagement.web.dto.UserSyncRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

@Component
public class UserSnapshotFactory {

    public UserSnapshot from(UserSyncRequest request) {
        List<String> roleCodes = normalize(request.getRoles() == null ? List.of() : request.getRoles());
        List<String> permissionCodes = normalize(request.getPermissions() == null ? List.of() : request.getPermissions());

        String tabNum = normalizeTabNum(request.getEmployeeNumber());

        String fingerprint = sha256Hex(String.join("|",
                nullToEmpty(request.getUserId()),
                nullToEmpty(request.getUsername()),
                nullToEmpty(request.getFullName()),
                nullToEmpty(request.getLastName()),
                nullToEmpty(request.getFirstName()),
                nullToEmpty(request.getMiddleName()),
                nullToEmpty(request.getEmployeeNumber()),
                nullToEmpty(request.getDepartmentNumber()),
                nullToEmpty(request.getIssuer()),
                String.join(",", roleCodes),
                String.join(",", permissionCodes)));

        return new UserSnapshot(
                request.getUserId(),
                request.getUsername(),
                request.getFullName(),
                request.getLastName(),
                request.getFirstName(),
                request.getMiddleName(),
                request.getEmployeeNumber(),
                tabNum,
                request.getDepartmentNumber(),
                request.getIssuer(),
                roleCodes,
                permissionCodes,
                fingerprint);
    }

    private List<String> normalize(List<String> values) {
        return values.stream()
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    /** Табельный номер может быть не чисто числовым — просто обрезаем края, не парсим. */
    private String normalizeTabNum(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return raw.trim();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
