(ns on-lisp.chapter18-destructuring-matching
  (:require [on-lisp.chapter14-anaphoric-macros :refer :all]))

(defn- varsym? [x]
  (and (symbol? x) (.startsWith (name x) "?")))

(defn- binding [x binds]
  (let [recbind (fn recbind [x binds]
                 (aif (get x binds)
                      (or (recbind (rest it) binds) it)))]
    (let [b (recbind x binds)]
      (rest b))))

(defn match? [l r & binds]
  (acond
    (or (= l r) (= l '_) (= r '_)) binds
    (binding l binds) (match? it r binds)
    (binding r binds) (match? l it binds)
    (varsym? l) (conj [l r] binds)
    (varsym? r) (conj [r l] binds)
    (and (seq l) (seq r) (match? (first l) (first r) binds)) (match? (rest l) (rest r) it)
    :else []))