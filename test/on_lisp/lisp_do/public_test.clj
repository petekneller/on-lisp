(ns on-lisp.lisp-do.public-test
  (:require [on-lisp.lisp-do.public :refer :all]
            [midje.sweet :refer :all]))

(facts "lisp-do performs the clojure equivalent of the lisp do form"
       (macroexpand-1 `(lisp-do [x 1 :inc (+ x 1)] (> x 5) true)) =>

       `(loop [x 1]
          (if (> x 5)
            nil
            (do
              true
              (recur (+ x 1)))))

       (fact "bindings without increment forms are left unchanged"
             (macroexpand-1 `(lisp-do [x 1 :inc (+ x 1) y 2] (> x 5) true)) =>

             `(loop [x 1 y 2]
                (if (> x 5)
                  nil
                  (do
                    true
                    (recur (+ x 1) y))))))

; eg
;(lisp-do [x 1 :inc (+ 1 x)] (> x 5) (println x))
