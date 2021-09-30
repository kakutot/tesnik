package impl;

import java.util.Objects;

class Pair<L, R> {

    private final L key;
    private final R value;

    public Pair(L key, R value) {
        this.key = key;
        this.value = value;
    }

    public L getKey() {
        return key;
    }

    public R getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(key, pair.key) &&
            Objects.equals(value, pair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}                                                     
                                                      