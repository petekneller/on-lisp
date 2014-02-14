(ns on-lisp.lisp-do.public-test
  (:require [on-lisp.lisp-do.public :refer :all]
            [midje.sweet :refer :all]))

(facts "lisp-do performs the clojure equivalent of the lisp do form"
       (macroexpand-1 `(lisp-do [x 1] true true)) => true)

; eg
;(lisp-do [x 1 :inc (+ 1 x)]
;         (> x 5)
;         (println x))
