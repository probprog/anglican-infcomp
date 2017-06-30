(defproject anglican-infcomp "0.2.1-SNAPSHOT"
  :description "Inference Compilation Library for Anglican"
  :url "https://arxiv.org/abs/1610.09900"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [anglican "1.0.1"]
                 [org.zeromq/jeromq "0.3.3"]
                 [org.zeromq/cljzmq "0.1.4" :exclusions [org.zeromq/jzmq]] ; https://github.com/zeromq/cljzmq
                 [org.clojure/tools.logging "0.3.1"]
                 [danlentz/clj-uuid "0.1.7"]]
  :plugins [[lein-codox "0.10.1"]]
  :java-source-paths ["src/java"])
