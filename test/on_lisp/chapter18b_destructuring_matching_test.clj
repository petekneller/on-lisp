(ns on-lisp.chapter18b-destructuring-matching-test
  (:require [midje.sweet :refer :all]
            [on-lisp.chapter18b-destructuring-matching :refer :all]))

(fact "match correctly recognises literals and keywords"
      (match? [1] 1) => true
      (match? [false] false) => true
      (match? ["foo"] "foo") => true
      (match? [:foo] :foo) => true
      (macroexpand `(match? [1] 1)) => `(= 1 1))

(fact "match correctly recognises symbol values"
      (let [i 1
            bar "bar"]
        (match? [i] 1) => true
        (match? [bar] "bar") => true
        (macroexpand `(match? [bar] "bar")) => `(= "bar" bar)))

(fact "match ignores symbols with name beginning in question mark"
      (match? [?a] "foo") => true
      (match? [?_] "foo") => true
      (macroexpand `(match? [?_] "foo")) => `true)

(fact "match performs recursive destructuring for vectors"
      (match? [[1 ?b 3]] (vector 1 2 3)) => true
      (match? [[[1 ?b] [3]]] [(vector 1 2) (vector 3)]) => true
      (macroexpand `(match? [[1 ?b 3]] (vector 1 2 3))) => `(every? true? [(= 1 1) true (= 3 3)]))
; boundary conditions in the above?