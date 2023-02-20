# tda-mapper-java
![test](https://github.com/lucasimi/tda-mapper-java/actions/workflows/test.yaml/badge.svg) ![deploy](https://github.com/lucasimi/tda-mapper-java/actions/workflows/deploy.yaml/badge.svg)

A Java library for the Mapper Algorithm from TDA, working on any metric space.

## Install

clone this repository, open a terminal, move to the root of the local repo, and run `mvn clean install -DskipTests`.

## Usage

A `MapperPipeline` object encapsulates all the steps to compute a mapper graph: 

```
Cover.Builder<S> cover = ...
Clustering.Builder<R> clustering = ...
Lens<R, S> lens = ...

MapperPipeline<S> mapperPipeline = MapperPipeline.<S, T>newBuilder()
    .withCover(cover)
    .withLens(lens)
    .withClustering(clustering)
    .build();
```

In this snippet, `cover` and `clustering` are expected to be *builders* defining the cover and the clustering algorithms. Once built, just run:

```
Collection<S> dataset = ... ;
MapperGraph graph = mapperPipeline.run(dataset);
```

### Cover algorithm

1. `TrivialCover<S>`: skip the cover step, returning a singleton collection containing the whole dataset;

2. `SearchCover<S>`: train a *search algorithm* on the dataset, and cover the points by using local neighborhoods.  A `SearchCover` loops through the points, when a non-covered point is found, its neighbors are queried (using the supplied `Search<S>` object), then those are added to the results as a new open cover. For the `Search<S>` algorithm you can chose from two options:

    2.1. `BallSearch`: for any point $p$, returns a collection of those points within the given radius $r$ from $p$;
        
    2.2. `KNNSearch`: for any point $p$, returns a collection of the first $k$ nearest neighbors of $p$.

    These two algorithms both need to use a *metric*, which is defined by implementing the `Metric<S>` interface, requiring only one method: `public double eval(T x, T y)`.

### Lenses

Each cover algorithm is a class implementing the `Cover<S>` interface, which also requires to take care about the *lens*. Any implementation of `Cover` must have a companion *builder* class, which is expected to implement a method `withLens`. Given a lens $f: R \to S$, calling `withLens` on a `Cover<S>.Builder` enables to pull the algorithm back on $R$, returning a builder for `Cover<R>`. 

### Clustering algorithm

Concerning the clustering step, you have two choices:

1. `TrivialClustering`: skip the clustering step, returning a singleton collection containing the whole dataset;

2. `CoverGraphClustering`: any given cover algorithm can induce a clustering algorithm by taking as clusters the connected components of the cover graph;

3. `DBSCAN`: an experimental implementation of the DBSCAN algorithm. Two variants are implemented: `DBSCANSimple` and `DBSCANFaster`.

## Design choices

Many of the classes used in this implementation use some kind of mutable state, in order to cache and preserve settings/indices. This has the unfortunate downside of making parallel computations harder to run consistently, and harder to debug when things go wrong. For this reason, each affected class has a dedicated builder class, which can be used to pass around its configuration. This is enough to easily prevent runtime collisions of shared mutable state in a concurrent environment.

## References

1. G. Singh , F. Mémoli, G. Carlsson. *Topological Methods for the Analysis of High Dimensional Data Sets and 3D Object Recognition.* https://research.math.osu.edu/tgda/mapperPBG.pdf

1. N. Saul, D. L. Arendt. *Machine Learning Explanations with Topological Data Analysis.* https://sauln.github.io/blog/tda_explanations/

2. P. N. Yianilos. *Data structures and algorithms for nearest neighbor search in general metric spaces.* SODA '93: Proceedings of the fourth annual ACM-SIAM symposium on Discrete algorithms, Jan 1993, pp. 311-321. https://dl.acm.org/doi/10.5555/313559.313789

3. S. Brin. *Near Neighbor Search in Large Metric Spaces.* VLDB '95 Proceedings of the 21th International Conference on Very Large Data Bases. Zurich, Switzerland: Morgan Kaufmann Publishers Inc.: 574–584. http://www.vldb.org/conf/1995/P574.PDF

4. Y. Zhou, N. Chalapathi, A. Rathore, Y. Zhao, B. Wang. *Mapper Interactive: A Scalable, Extendable, and Interactive Toolbox for the Visual Exploration of High-Dimensional Data.* https://arxiv.org/abs/2011.03209
