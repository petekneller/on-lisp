(ns on-lisp.chapter6-macros
  (:require [clojure.core :refer :all]))


(println (str "hello" "world")) ; => helloworld
(println '(str "hello" "world")) ; => (str hello world)
; syntax quote resolves symbols
(println `(str "hello" "world")) ; => (clojure.core/str hello world)
; both forms of quoting are recursive
(println '(str "hel" (str "lo" "wo") "rld")) ; => (str hel (str lo wo) rld)
(println `(str "hel" (str "lo" "wo") "rld")) ; => (clojure.core/str hel (clojure.core/str lo wo) rld)



(defmacro macexp [form]
  `(pprint (macroexpand-1 form)))

(defn my-print [exp]
  (do
    (println "inside macro")
    `(str ~(str "my-print" ": ") exp)))
