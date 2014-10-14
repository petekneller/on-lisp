(ns on-lisp.chapter19-query-interpreter
  (:require [on-lisp.chapter18-destructuring-matching :refer :all]
            [on-lisp.chapter14-anaphoric-macros :refer :all]))


(def default-db (atom {}))

(defn clear-db []
  (swap! default-db (fn [_] {})))

(defn db-push [key val]
  (swap! default-db (fn [db] (assoc db key (conj (get db key) val)))))

(defmacro fact [pred args]
  `(do
     (db-push ~pred ~args)
     ~args))

(defmacro db-query [key]
  `(get (deref default-db) ~key []))

(defn lookup
  ([pred pattern] (lookup pred pattern [{}]))
  ([pred pattern binds]
   (let [bindings-per-answer (for [row (db-query pred) binding binds]
                               (match? row pattern binding))]
     (filter map? bindings-per-answer))))

(declare interpret-query)

(defn interpret-and [clauses binds]
  (if (empty? clauses)
    binds
    (mapcat (fn [b] (interpret-query (first clauses) (vector b)))
            (interpret-and (rest clauses) binds))))

(defn interpret-or [clauses binds]
  (vec (mapcat #(interpret-query % binds) clauses)))

(defn interpret-not [clause binds]
  (if (empty? (interpret-query clause binds))
    binds
    []))

(defn interpret-query
  ([form] (interpret-query form [{}]))
  ([form binds]
   (cond
     (= :or (first form)) (interpret-or (rest form) binds)
     (= :and (first form)) (interpret-and (reverse (rest form)) binds)
     (= :not (first form)) (interpret-not (second form) binds)
     :else (lookup (first form) (second form) binds))))

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

(defmacro with-answer [query & body]
  (let [binds (gensym)]
  `(for [~binds (interpret-query ~(quote-vars-in query))]
     (let ~(vec (mapcat (fn [var] [var `(get ~binds '~var)]) (vars-in query)))
       ~@body))))