package top.aimixer.schema;

import java.util.List;
import java.util.Map;

/**
 * Helper class for Quadruple
 */
public class Quadruple<T, U, X, Y> {
    private final T first;
    private final U second;
    private final X third;
    private final Y fourth;

    public Quadruple(T first, U second, X third, Y fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    public X getThird() {
        return third;
    }

    public Y getFourth() {
        return fourth;
    }
}
