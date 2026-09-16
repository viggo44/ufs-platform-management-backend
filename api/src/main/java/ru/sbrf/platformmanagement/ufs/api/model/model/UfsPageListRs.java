package ru.sbrf.platformmanagement.ufs.api.model.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
public class UfsPageListRs<T> {

    private List<T> content;
    private UfsPageRequest pageRequest;
    private UfsPageResult pageResult;


    public UfsPageListRs(List<T> content, UfsPageRequest pageRequest, UfsPageResult pageResult) {
        this.content = content != null ? content : Collections.emptyList();
        this.pageRequest = pageRequest;
        this.pageResult = pageResult;
    }

    public static <T> UfsPageListRs<T> of(List<T> content, UfsPageRequest pageRequest, int totalPages, long totalRows) {
        return new UfsPageListRs<>(content, pageRequest, new UfsPageResult(content != null ? content.size() : 0, totalPages, totalRows));
    }
}
