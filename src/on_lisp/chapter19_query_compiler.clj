(ns on-lisp.chapter19-query-compiler
  (:require [on-lisp.chapter18-destructuring-matching :refer :all]
            [on-lisp.chapter19-query-interpreter :refer [db-query vars-in]]))

(defmacro with-gensyms [vars & body]
  `(let ~(vec (mapcat (fn [var] [var (gensym)]) vars))
     ~@body))

(declare compile-query)

(defn compile-simple [query body]
  `(for [fact# (db-query ~(first query))]
     (if (match? ~(second query) fact#)
       ~@body)))

(defn compile-query [query body]
  (cond
    :else (compile-simple query body)))

(defmacro with-answer [query & body]
  `(with-gensyms ~(vars-in query)
       ~(compile-query query `(do ~@body))))

