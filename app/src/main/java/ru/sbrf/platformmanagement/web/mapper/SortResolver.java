package ru.sbrf.platformmanagement.web.mapper;

import jakarta.ws.rs.BadRequestException;
import org.springframework.data.domain.Sort;

import java.util.Map;

/**
 * {@code UfsPageRequest} не несёт поле сортировки — контроллеры принимают {@code sort}
 * отдельным query-параметром и резолвят его в {@link Sort} через белый список полей на
 * стороне entity (никогда не доверяет "сырому" пути свойства JPA от клиента).
 */
public final class SortResolver {

    private SortResolver() {
    }

    /** @param sort "сырое" значение вида {@code "field,dir"}, например {@code "name,desc"}; может быть null */
    public static Sort resolve(String sort, Map<String, String> whitelist, String defaultField) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, resolveField(whitelist, defaultField));
        }
        String[] parts = sort.split(",", 2);
        String jpaPath = resolveField(whitelist, parts[0].trim());
        Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(direction, jpaPath);
    }

    private static String resolveField(Map<String, String> whitelist, String field) {
        String jpaPath = whitelist.get(field);
        if (jpaPath == null) {
            throw new BadRequestException("Unsupported sort field: " + field);
        }
        return jpaPath;
    }
}
