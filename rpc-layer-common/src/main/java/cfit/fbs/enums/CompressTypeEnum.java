package cfit.fbs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author shengshuo
 *
 */
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip"),
    ANT((byte) 0x02, "ant");

    private final byte code;
    private final String name;


    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
