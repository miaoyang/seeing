package com.ym.seeing.api.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/2/1 19:15
 * @Desc:
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PageResultBean<T> {
    private long total;
    private List<T> rows;
}
