package org.lucasimi.tda.mapper.cover;

import java.util.Collection;
import java.util.Collections;

public class CoverUtils {

    private CoverUtils() {
    }

    public static <S> CoverAlgorithm<S> trivialCover() {
        return new CoverAlgorithm<S>() {

            private Collection<S> dataset;

            @Override
            public CoverAlgorithm<S> fit(Collection<S> dataset) {
                this.dataset = dataset;
                return this;
            }

            @Override
            public Collection<Collection<S>> getCover() {
                return Collections.singleton(this.dataset);
            }

        };
    }

}
