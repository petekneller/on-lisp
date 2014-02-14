(ns on-lisp.lisp-do.internal-test
  (:require [on-lisp.lisp-do.internal :refer :all]
            [midje.sweet :refer :all]))

(facts "do bindings can be converted into let bindings"
       (fact "bindings can be of the form [name init-form] just like let bindings"
             (let-bindings `[x 1]) => [`x 1]
             (let-bindings `[x 1 y 2]) => [`x 1 `y 2 ])
       (fact "bindings can specify an increment using the form [name init-form :inc inc-form]"
             (let-bindings `[x 1 :inc 1]) => [`x 1]
             (let-bindings `[x 1 :inc 1 y 2 :inc 3]) => [`x 1 `y 2]))

(fact "do bindings can be converted into increment forms"
       (inc-forms `[x 1 y 2]) => [`x `y]
       (inc-forms `[x 0 :inc (+ 1 x)]) => [`(+ 1 x)])

;(fact "do bindings can be converted into let bindings"
;      (let-bindings `[x 1]) => [])

