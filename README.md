# tda-mapper-java

[![test](https://github.com/lucasimi/tda-mapper-java/actions/workflows/test.yaml/badge.svg)](https://github.com/lucasimi/tda-mapper-java/actions/workflows/test.yaml) [![deploy](https://github.com/lucasimi/tda-mapper-java/actions/workflows/deploy.yaml/badge.svg)](https://github.com/lucasimi/tda-mapper-java/actions/workflows/deploy.yaml) [![release](https://github.com/lucasimi/tda-mapper-java/actions/workflows/release.yaml/badge.svg)](https://github.com/lucasimi/tda-mapper-java/actions/workflows/release.yaml)

A Java library for the Mapper Algorithm from TDA, working on any metric space.

## Install

To install just clone this repo, open up a terminal and move in the clones directory, then run `mvn clean install -DskipTests`.

## Usage

A `MapperPipeline` object encapsulates all the steps to compute a mapper graph: 

```
Cover.Builder<S> cover = ...
Clustering.Builder<R> clustering = ...
Lens<R, S> lens = ...

MapperPipeline<R> mapperPipeline = MapperPipeline.<R, S>newBuilder()
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

### Quick Intro to the Mapper Algorithm

The Mapper Algorithm is a powerful tool from Topological Data Analysis (TDA), which is able to give insights about the *shape of data*. It was originally presented in [1] and today is one of the backbones of some industrial application too. In its simplest form, the output of the Mapper is a graph network, representing a *topological summary* for the shape of the input dataset. The steps are the following:

1. Identify a *lens* $f \colon X \to Z$, or filter, which maps any point in the dataset $X$ to a parameter space $Z$. Usually the space $Z$ has lower dimension than the dataset $X$.

2. Cover the image of the lens $f$ with *open sets*. This can be done in many possible ways

3. Take the *pullback cover* under $f$, and split each pullback by using a *clustering algorithm*.

4. Build a *network graph* with a node for each local cluster, and an arc for any couple of intersecting clusters.

### Metrics and Lenses

A *metric* is any class implementing the `Metric<S>` interface, which has only one method: `public double eval(T x, T y)`. 

A *lens* represents a continuous map $f \colon X \to Z$, where $X$ is the the space where the dataset lives, and $Z$ is a parameter space where interesting features live. A lens can be defined by any class implementing the interface `Lens<S, T>`, which has only one method `public T evaluate(S x)`.

In the `TopologyUtils` class you can find methods to define *pullbacks* for metrics and lenses.

### Cover algorithm

A *cover algorithm* is any class implementing the `Cover<S>` interface, which requires only one method: `public Collection<Collection<S>> run(Collection<S> dataset);`
This method takes the whole dataset as input, and returns a collection of possibly overlapping subsets.

1. `TrivialCover<S>`: skip the cover step, returning a singleton collection containing the whole dataset;

2. `SearchCover<S>`: train a *search algorithm* on the dataset, and cover the points by using local neighborhoods.  A `SearchCover` loops through the points, when a non-covered point is found, its neighbors are queried (using the supplied `Search<S>` object), then those are added to the results as a new open cover. For the `Search<S>` algorithm you can chose from two options:

    2.1. `BallSearch`: for any point $p$, returns a collection of those points within the given radius $r$ from $p$;
        
    2.2. `KNNSearch`: for any point $p$, returns a collection of the first $k$ nearest neighbors of $p$.

    2.3. `CubicSearch`: this is the original type of cover presented in [1]. You need to specify the number of intervals and the overlap in the range $(0.0, 1.0)$, and a lens which maps points to coordinates. 

Both `BallSearch` and `KNNSearch` require a metric to be built. The `CubicSearch` requires a lens instead. This is because the cubic cover works with point coordinates.

Any implementation of `Cover` has a companion *builder* class, which is expected to implement a method `pullback` which, given a lens $f: R \to S$, return a builder for `Cover<R>`. 

### Clustering algorithm

A *clustering algorithm* is any class implementing the `Clustering<S>` interface, which requires only one method: `public Collection<Collection<S>> run(Collection<S> dataset);`
Concerning the clustering step, you have two choices:

1. `TrivialClustering`: skip the clustering step, returning a singleton collection containing the whole dataset;

2. `CoverGraphClustering`: any given cover algorithm can induce a clustering algorithm by taking as clusters the connected components of the cover graph;

3. `DBSCAN`: an experimental implementation of the DBSCAN algorithm. Two variants are implemented: `DBSCANSimple` and `DBSCANFaster`.

Any implementation of `Cluster` must have a companion *builder* class. 

## Design choices

Many of the classes used in this implementation use some kind of mutable state, in order to cache and preserve settings/indices. This has the unfortunate downside of making parallel computations harder to run consistently, and harder to debug when things go wrong. For this reason, each stateful class has a dedicated builder class, which can be used to pass around its configuration. This is enough to easily prevent runtime collisions of shared mutable state in a concurrent environment.

## References

1. G. Singh , F. Mémoli, G. Carlsson. *Topological Methods for the Analysis of High Dimensional Data Sets and 3D Object Recognition.* https://research.math.osu.edu/tgda/mapperPBG.pdf

1. N. Saul, D. L. Arendt. *Machine Learning Explanations with Topological Data Analysis.* https://sauln.github.io/blog/tda_explanations/

2. P. N. Yianilos. *Data structures and algorithms for nearest neighbor search in general metric spaces.* SODA '93: Proceedings of the fourth annual ACM-SIAM symposium on Discrete algorithms, Jan 1993, pp. 311-321. https://dl.acm.org/doi/10.5555/313559.313789

3. S. Brin. *Near Neighbor Search in Large Metric Spaces.* VLDB '95 Proceedings of the 21th International Conference on Very Large Data Bases. Zurich, Switzerland: Morgan Kaufmann Publishers Inc.: 574–584. http://www.vldb.org/conf/1995/P574.PDF

4. Y. Zhou, N. Chalapathi, A. Rathore, Y. Zhao, B. Wang. *Mapper Interactive: A Scalable, Extendable, and Interactive Toolbox for the Visual Exploration of High-Dimensional Data.* https://arxiv.org/abs/2011.03209
