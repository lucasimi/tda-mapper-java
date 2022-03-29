package org.lucasimi.tda.mapper.utils;

import org.lucasimi.tda.mapper.topology.Lens;
import org.lucasimi.tda.mapper.topology.Metric;

public class Distance<S> implements Lens<S, Float> {

    private S center;

    private Metric<S> metric;

    public Distance(Metric<S> metric) {
        this.metric = metric;
    }

    public void setCenter(S center) {
        this.center = center;
    }

    @Override
    public Float evaluate(S source) {
        return this.metric.evaluate(this.center, source);
    }
    
}
