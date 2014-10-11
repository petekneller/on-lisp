(ns on-lisp.chapter18-destructuring-matching
  (:require [on-lisp.chapter14-anaphoric-macros :refer :all]))

(defn- varsym? [x]
  (and (symbol? x) (.startsWith (name x) "?")))

(defn- zip [& colls]
  (apply map vector colls))

(defn- pair-match? [pair tail binds]
  (let [l (first pair)
        r (second pair)
        _ (println pair)]
    (acond
      (nil? pair) binds
      (or (= l r) (= l '_) (= r '_)) (recur (first tail) (rest tail) binds)
      (get binds l) (recur [it r] tail binds)
      (get binds r) (recur [l it] tail binds)
      (varsym? l) (recur (first tail) (rest tail) (assoc binds l r))
      (varsym? r) (recur (first tail) (rest tail) (assoc binds r l)))))

(defn match? [l r]
  (if (not (= (count l) (count r)))
    nil
    (let [pairs (zip l r)]
      (pair-match? (first pairs) (rest pairs) {}))))

;(defmacro if-match [pat seq then & else]
;  `(aif (match? ~pat ~seq)
;        (let (vec it)
;          ~then)
;     ~else))
