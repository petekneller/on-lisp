(ns on-lisp.chapter18-destructuring-matching
  (:require [on-lisp.chapter14-anaphoric-macros :refer :all]))

(defn varsym? [x]
  (and (symbol? x) (.startsWith (name x) "?")))

(defn ignoresym? [x]
  (and (symbol? x) (= (name x) "_")))

(defn vars-in
  ([query] (vars-in query []))
  ([query vars]
   (cond
     (vector? query) (vec (set (mapcat #(vars-in % vars) query)))
     (varsym? query) (conj vars query)
     :else vars)))

(defn quote-vars-in [query]
  (cond
    (vector? query) (vec (map #(quote-vars-in %) query))
    (or (varsym? query) (ignoresym? query)) `(quote ~query)
    :else query))

(defn- zip [& colls]
  (apply map vector colls))

(defn- pair-match? [pair tail binds]
  (let [l (first pair)
        r (second pair)]
    (acond
      (nil? pair) binds
      (or (= l r) (= l '_) (= r '_)) (recur (first tail) (rest tail) binds)
      (get binds l) (recur [it r] tail binds)
      (get binds r) (recur [l it] tail binds)
      (varsym? l) (recur (first tail) (rest tail) (assoc binds l r))
      (varsym? r) (recur (first tail) (rest tail) (assoc binds r l)))))

(defn match?
  ([l r] (match? l r {}))
  ([l r binds]
   (if (not (= (count l) (count r)))
     nil
     (let [pairs (zip l r)]
       (pair-match? (first pairs) (rest pairs) binds)))))

(defmacro if-match [pat seq then & else]
  `(aif (match? ~(quote-vars-in pat) ~seq)
        (let ~(vec (mapcat (fn [var] [var `(get ~'it '~var)]) (vars-in pat)))
          ~then)
        ~@else))