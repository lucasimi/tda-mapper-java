package org.lucasimi.tda.mapper.graph;

public class MapperEdge {

    private double union;

    private double intersection;

    private double weight;

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getIntersection() {
        return intersection;
    }

    public void setIntersection(double intersection) {
        this.intersection = intersection;
    }

    public double getUnion() {
        return union;
    }

    public void setUnion(double union) {
        this.union = union;
    }

    public double getSimilarity() {
        return 1.0 - (this.intersection / this.union);
    }

    public void setSimilarity(double similarity) {
        this.union = 1.0;
        this.intersection = 1.0 - similarity;
    }
    
}
