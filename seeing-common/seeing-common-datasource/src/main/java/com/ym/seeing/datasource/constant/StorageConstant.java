package com.ym.seeing.datasource.constant;

import lombok.Data;

import java.io.File;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 17:03
 * @Desc:
 */
public class StorageConstant {
    public static String LOCPATH = File.separator + "HellohaoData";

    public static final Integer NOS = 1;
    public static final Integer OSS = 2;
    public static final Integer USS = 3;
    public static final Integer KODO = 4;
    public static final Integer LOCAL = 5;
    public static final Integer COS = 6;
    public static final Integer FTP = 7;
    public static final Integer UFILE = 8;

    public enum StorageType{
        NOS(1),OSS(2),USS(3),KODO(4),LOCAL(5),COS(6),FTP(7),UFILE(8);

        private Integer type;

        private StorageType(Integer type) {
            this.type = type;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }
    }
}
