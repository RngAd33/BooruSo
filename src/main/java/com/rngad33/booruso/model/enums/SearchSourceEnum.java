package com.rngad33.booruso.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 搜索源枚举
 */
@Getter
@RequiredArgsConstructor
public enum SearchSourceEnum {

    SAFE_BOORU("Safebooru", 1),
    DUITANG("堆糖", 2);

    private final String source;
    private final int code;

    /**
     * 根据code获取枚举
     *
     * @param code
     * @return
     */
    public static SearchSourceEnum getEnumByCode(int code) {
        for (SearchSourceEnum value : values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}