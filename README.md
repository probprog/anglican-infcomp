# Clojure library for Inference Compilation and Universal Probabilistic Programming

Code for [Inference Compilation and Universal Probabilistic Programming](https://arxiv.org/abs/1610.09900).

This repository contains the [Clojure](https://clojure.org/)-based probabilistic programming part of the compiled inference scheme. The [Torch](http://torch.ch/)-based neural network part is [here](https://github.com/tuananhle7/torch-csis). The interaction between these two is facilitated by [ZeroMQ](http://zeromq.org/).

For a walkthrough on how to set up a system to compile inference for a probabilistic program written in Anglican, check out the [tutorial](https://github.com/tuananhle7/torch-csis/blob/master/TUTORIAL.md). Also check out the [examples](https://github.com/tuananhle7/torch-csis/tree/master/examples) folder in  the [torch-csis](https://github.com/tuananhle7/torch-csis) repo.

Clone this repo `lein install` to install. Documentation is [here](http://tuananhle.co.uk/anglican-csis-doc/).

If you use this code in your work, please cite our [paper](https://arxiv.org/abs/1610.09900):
```
@article{le2016inference,
  title = {Inference Compilation and Universal Probabilistic Programming},
  author = {Le, Tuan Anh and Baydin, Atilim Gunes and Wood, Frank},
  journal = {arXiv preprint arXiv:1610.09900},
  year = {2016}
}
```
