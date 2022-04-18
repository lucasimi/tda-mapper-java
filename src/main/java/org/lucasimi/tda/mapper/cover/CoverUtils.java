package org.lucasimi.tda.mapper.cover;

import java.util.ArrayList;
import java.util.Collection;

public class CoverUtils {
    
    private CoverUtils() {}

    public static <S> CoverAlgorithm<S> trivialCover() {
        return new CoverAlgorithm<S>() {

            @Override
            public Collection<Collection<S>> getClusters(Collection<S> dataset) {
                Collection<Collection<S>> trivialMap = new ArrayList<>(1);
                trivialMap.add(dataset);
                return trivialMap;
            }

        };
    }

}
