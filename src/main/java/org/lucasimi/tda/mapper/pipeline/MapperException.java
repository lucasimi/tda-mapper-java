package org.lucasimi.tda.mapper.pipeline;

public class MapperException extends Exception {

    private MapperException() {}

    public static class ClusteringException extends MapperException {}

    public static class CoverException extends MapperException {}

    public static class LensException extends MapperException {}

}
