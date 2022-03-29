package org.lucasimi.tda.mapper.topology;

public class Point<S> {

    private final Integer id;

    private final S value;

    public Point(Integer id, S value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public S getValue() {
        return value;
    }

}
