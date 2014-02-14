(ns on-lisp.lisp-do.internal-test
  (:require [on-lisp.lisp-do.internal :refer :all]
            [midje.sweet :refer :all]))

(facts "do-extract-bindings will turn the binding forms into something useful"
       (fact "bindings can be of the form [name init-form] just like let bindings"
             (do-extract-bindings `[x 1]) => [{:var `x :init-form 1}]
             (do-extract-bindings `[x 1 y 2]) => [{:var `x :init-form 1} {:var `y :init-form 2}])
       (fact "bindings can specify an increment using the form [name init-form :inc inc-form]"
             (do-extract-bindings `[x 1 :inc 1]) => [{:var `x :init-form 1 :inc-form 1}]
             (do-extract-bindings `[x 1 :inc 1 y 2 :inc 3]) => [{:var `x :init-form 1 :inc-form 1} {:var `y :init-form 2 :inc-form 3}]))

