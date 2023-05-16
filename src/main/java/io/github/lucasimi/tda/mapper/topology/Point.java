package io.github.lucasimi.tda.mapper.topology;

public interface Point<S> {

    public S getData();

    public float[] getCoords();

    public static <S> Point<S> newPoint(S data, float[] coords) {
        return new PointEager<S>(data, coords);
    }

    public static <S> Point<S> newPoint(S data, Lens<S, float[]> coords) {
        return new PointLazy<S>(data, coords);
    }

    public class PointEager<S> implements Point<S> {

        private S data;

        private float[] coords;

        private PointEager(S data, float[] coords) {
            this.data = data;
            this.coords = coords;
        }

        @Override
        public S getData() {
            return this.data;
        }

        @Override
        public float[] getCoords() {
            return this.coords;
        }

    }

    public static class PointLazy<S> implements Point<S> {

        private S data;

        private Lens<S, float[]> chart;

        private PointLazy(S data, Lens<S, float[]> chart) {
            this.data = data;
            this.chart = chart;
        }

        @Override
        public S getData() {
            return this.data;
        }

        @Override
        public float[] getCoords() {
            return this.chart.evaluate(this.data);
        }

    }

}
