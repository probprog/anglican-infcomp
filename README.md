# Clojure library for Inference Compilation and Universal Probabilistic Programming

Code for Inference Compilation and Universal Probabilistic Programming ([main project page][project-page-link]).

This repository contains the [Clojure](https://clojure.org/)-based probabilistic programming part of the inference compilation scheme. The [Torch](http://torch.ch/)-based neural network part is [here][torch-csis-repo-link]. The interaction between these two is facilitated by [ZeroMQ](http://zeromq.org/).

For a walkthrough on how to set up a system to compile inference for a probabilistic program written in Anglican, check out the [tutorial][tutorial-link]. Also check out the [examples][examples-link] folder in the [torch-csis][torch-csis-repo-link] repo.

Clone this repo and run `lein install` to install. Documentation is [here][anglican-csis-docs-link].

If you use this code in your work, please cite our [paper][paper-link]:
```
@inproceedings{le2016inference,
  author = {Le, Tuan Anh and Baydin, Atılım Güneş and Wood, Frank},
  booktitle = {20th International Conference on Artificial Intelligence and Statistics, April 20--22, 2017, Fort Lauderdale, FL, USA},
  title = {Inference Compilation and Universal Probabilistic Programming},
  year = {2017}
}
```

[project-page-link]: http://tuananhle.co.uk/compiled-inference
[examples-link]: https://github.com/tuananhle7/torch-csis/tree/master/examples
[torch-csis-repo-link]: https://github.com/tuananhle7/torch-csis
[tutorial-link]: https://github.com/tuananhle7/torch-csis/blob/master/TUTORIAL.md
[anglican-csis-docs-link]: http://tuananhle.co.uk/anglican-csis-doc/
[paper-link]: https://arxiv.org/abs/1610.09900
