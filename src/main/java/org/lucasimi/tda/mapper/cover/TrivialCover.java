package org.lucasimi.tda.mapper.cover;

import java.util.Collection;
import java.util.Collections;

import org.lucasimi.tda.mapper.topology.Lens;

public class TrivialCover<S> implements Cover<S> {

    private TrivialCover() {}

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    @Override
    public Collection<Collection<S>> run(Collection<S> dataset) {
        return Collections.singleton(dataset);
    }

    public static class Builder<S> implements Cover.Builder<S> {

        @Override
        public Cover<S> build() {
            return new TrivialCover<>();
        }

        @Override
        public <R> Builder<R> pullback(Lens<R, S> lens) {
            return new Builder<>();
        }

    }

}
