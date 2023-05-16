package io.github.lucasimi.tda.mapper.cover;

import java.util.Collection;

import io.github.lucasimi.tda.mapper.pipeline.MapperException.CoverException;
import io.github.lucasimi.tda.mapper.topology.Lens;

public interface Cover<S> {

    public Collection<Collection<S>> run(Collection<S> dataset);

    public static interface Builder<S> {

        public Cover<S> build() throws CoverException;

        public <R> Builder<R> pullback(Lens<R, S> lens);

    }

}
