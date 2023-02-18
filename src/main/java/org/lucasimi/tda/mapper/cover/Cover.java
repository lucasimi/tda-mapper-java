package org.lucasimi.tda.mapper.cover;

import java.util.Collection;

public interface Cover<S> {

    public Collection<Collection<S>> run(Collection<S> dataset);

    public static interface Builder<S> {

        public Cover<S> build();

    }

}