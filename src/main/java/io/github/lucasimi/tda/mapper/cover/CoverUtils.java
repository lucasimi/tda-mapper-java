package io.github.lucasimi.tda.mapper.cover;

import java.util.Collection;
import java.util.Collections;

import io.github.lucasimi.tda.mapper.pipeline.MapperException.CoverException;
import io.github.lucasimi.tda.mapper.topology.Lens;

public class CoverUtils {

    private CoverUtils() {
    }

    public static <S> Cover<S> trivialCover() {
        return new TrivialCover<>();
    }

    public static <S> Cover.Builder<S> trivialCoverBuilder() {
        return new TrivialCover.Builder<>();
    }

    private static class TrivialCover<S> implements Cover<S> {

        private TrivialCover() {}

        @Override
        public Collection<Collection<S>> run(Collection<S> dataset) {
            return Collections.singleton(dataset);
        }

        public static class Builder<S> implements Cover.Builder<S> {

            @Override
            public Cover<S> build() throws CoverException {
                return new TrivialCover<>();
            }

            @Override
            public <R> Builder<R> pullback(Lens<R, S> lens) {
                return new Builder<>();
            }

        }

    }

}
