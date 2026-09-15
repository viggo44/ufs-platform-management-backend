package ru.sbrf.platformmanagement.web.mapper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.sbrf.platformmanagement.ufs.api.model.UfsPageRequest;

/**
 * Дано было {@code map(UfsPageRequest): ru.sbrf.phoenix.signal.api.model.PageRequest} —
 * у меня нет доступа к этому внутреннему типу платформы, поэтому маппер адаптирован на
 * {@link Pageable} из Spring Data, которым реально пользуются репозитории в этом решении
 * ({@code JpaSpecificationExecutor.findAll(spec, pageable)}). {@code UfsPageRequest.page}
 * — с единицы, Spring Data ждёт индекс с нуля — вычитание сохранено, как в исходнике.
 */
public final class CommonMapper {

    private CommonMapper() {
        throw new UnsupportedOperationException();
    }

    public static Pageable map(UfsPageRequest pageRequest, Sort sort) {
        if (pageRequest != null) {
            return PageRequest.of(pageRequest.getPage() - 1, pageRequest.getLimit(), sort);
        }
        return null;
    }
}
