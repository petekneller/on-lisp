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

(defn interpret-query
  ([form] (interpret-query form [{}]))
  ([form binds]
   (cond
     (= :and (first form)) (interpret-and (reverse (rest form)) binds)
     :else (lookup (first form) (second form) binds))))
