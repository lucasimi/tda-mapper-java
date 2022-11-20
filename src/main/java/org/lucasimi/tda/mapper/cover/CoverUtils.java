package org.lucasimi.tda.mapper.cover;

import java.util.Collection;
import java.util.Collections;

public class CoverUtils {

    private CoverUtils() {
    }

    public static <S> CoverAlgorithm<S> trivialCover() {
        return new CoverAlgorithm<S>() {

            @Override
            public Collection<Collection<S>> run(Collection<S> dataset) {
                return Collections.singleton(dataset);
            }

        };
    }

    public static <S> CoverAlgorithm.Builder<S> trivialCoverBuilder() {
        return new CoverAlgorithm.Builder<S>() {

            @Override
            public CoverAlgorithm<S> build() {
                return trivialCover();
            }
        };
    }

}
