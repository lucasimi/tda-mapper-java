package org.lucasimi.tda.mapper.topology;

public interface Lens<S, T> {

    public T evaluate(S source);

}
