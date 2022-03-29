package org.lucasimi.tda.mapper.pipeline;

public class MapperException extends Exception {
   
    public static class NoClusteringAlgorithm extends MapperException {}

    public static class NoCoverAlgorithm extends MapperException {}

}
