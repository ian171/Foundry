package cn.epicmc.foundry.common.unit;

/**
 * 数组工具
 *
 * @author kzw on 2017/07/06. Chen edited in 2026/11/15.
 */
public final class ArraysUtils {

    public ArraysUtils() {
        throw new RuntimeException("ArraysUtils stub!");
    }

    public static <T> boolean isEmpty(final T[] array) {
        return array == null || array.length == 0;
    }
}
