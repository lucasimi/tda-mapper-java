package org.lucasimi.tda.mapper.topology;

public interface Metric<T> {

    float evaluate(T testPoint, T center);
    
}
