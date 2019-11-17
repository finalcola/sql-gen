package com.finalcola.sql.struct;

/**
 * @author: yuanyou.
 * @date: 2019-11-11 19:45
 */
public enum IndexType {
    /**
     * Primary index type.
     */
    PRIMARY(0),
    /**
     * Normal index type.
     */
    Normal(1),
    /**
     * Unique index type.
     */
    Unique(2),
    /**
     * Full text index type.
     */
    FullText(3);

    private int i;

    IndexType(int i) {
        this.i = i;
    }

    /**
     * Value int.
     *
     * @return the int
     */
    public int value() {
        return this.i;
    }

    /**
     * Value of index type.
     *
     * @param i the
     * @return the index type
     */
    public static IndexType valueOf(int i) {
        for (IndexType t : values()) {
            if (t.value() == i) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid IndexType:" + i);
    }
}
