package org.lucasimi.tda.mapper.cover;

import java.util.Collection;
import java.util.LinkedList;

public class CoverUtils {
    
    private CoverUtils() {}

    public static <S> CoverAlgorithm<S> trivialCover() {
        return dataset -> {
            Collection<Collection<S>> trivialMap = new LinkedList<>();
            trivialMap.add(dataset);
            return trivialMap;
        };
    }

}
