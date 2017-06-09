(ns anglican.infcomp.core
  (:require anglican.trap))

(defn reset-infcomp-addressing-scheme! []
  (alter-var-root
   (var anglican.trap/*gensym*)
   (let [i (atom 0)]
     (fn [_]
       (fn [prefix]
         (swap! i inc)
         (symbol (str prefix @i)))))))
