package org.lucasimi.tda.mapper.cover;

import java.util.Collection;
import java.util.Collections;

public class CoverUtils {

    private CoverUtils() {
    }

    public static <S> Cover<S> trivialCover() {
        return new Cover<S>() {

            @Override
            public Collection<Collection<S>> run(Collection<S> dataset) {
                return Collections.singleton(dataset);
            }

        };
    }

    public static <S> Cover.Builder<S> trivialCoverBuilder() {
        return new Cover.Builder<S>() {

            @Override
            public Cover<S> build() {
                return trivialCover();
            }
        };
    }

}
