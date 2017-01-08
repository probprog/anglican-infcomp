# Clojure library for Inference Compilation and Universal Probabilistic Programming

Code for Inference Compilation and Universal Probabilistic Programming ([main project page][project-page-link]).

This repository contains the [Clojure](https://clojure.org/)-based probabilistic programming part of the compiled inference scheme. The [Torch](http://torch.ch/)-based neural network part is [here][torch-csis-repo-link]. The interaction between these two is facilitated by [ZeroMQ](http://zeromq.org/).

For a walkthrough on how to set up a system to compile inference for a probabilistic program written in Anglican, check out the [tutorial][tutorial-link]. Also check out the [examples][examples-link] folder in the [torch-csis][torch-csis-repo-link] repo.

Clone this repo `lein install` to install. Documentation is [here][anglican-csis-docs-link].

If you use this code in your work, please cite our [paper][paper-link]:
```
@article{le2016inference,
  title = {Inference Compilation and Universal Probabilistic Programming},
  author = {Le, Tuan Anh and Baydin, Atilim Gunes and Wood, Frank},
  journal = {arXiv preprint arXiv:1610.09900},
  year = {2016}
}
```

[project-page-link]: https://github.com/tuananhle7/torch-csis/blob/master/PROJECT_PAGE.md
[examples-link]: https://github.com/tuananhle7/torch-csis/tree/master/examples
[torch-csis-repo-link]: https://github.com/tuananhle7/torch-csis
[tutorial-link]: https://github.com/tuananhle7/torch-csis/blob/master/TUTORIAL.md
[anglican-csis-docs-link]: http://tuananhle.co.uk/anglican-csis-doc/
[paper-link]: https://arxiv.org/abs/1610.09900
