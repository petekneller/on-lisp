(ns on-lisp.chapter16-macro-defining-macros-test
  (:require [midje.sweet :refer :all]
            [on-lisp.chapter16-macro-defining-macros :refer :all]))

; 16.3 Anaphoric macros

(fact (a+ 7.95 (* it 0.05) (* it 3)) => (roughly 9.54))

(fact (alist 1 (+ 2 it) (+ 2 it)) => '(1 3 5))
