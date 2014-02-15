(ns on-lisp.chapter10-other-macro-pitfalls
  (:require [on-lisp.lisp-do.public :refer :all]))

; 10.4 Recursion

(defn ntha [n coll]
  (if (= n 0)
    (first coll)
    (ntha (- n 1) (rest coll))))

(defmacro nthb [n coll]
  `(if (= ~n 0)
     (first ~coll)
     (nthb (- ~n 1) (rest ~coll))))

(defmacro nthc [n coll]
  `(loop [n# ~n
          coll# ~coll]
     (if (= n# 0)
       (first coll#)
       (recur (- n# 1) (rest coll#)))))

(defn or-expand [args]
  (if (empty? args)
    nil
    `(let [sym# ~(first args)]
       (if sym#
         sym#
         ~(or-expand (rest args))))))

(defmacro ora [& args]
  (or-expand args))

(defmacro orb [& args]
  (if (empty? args)
    nil
    `(let [sym# ~(first args)]
       (if sym#
         sym#
         (orb ~@(rest args))))))