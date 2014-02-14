(ns on-lisp.lisp-do.public
  (:require [on-lisp.lisp-do.internal :refer :all]))

(defmacro lisp-do [binding-forms test-form & body]
  `(loop [~@(let-bindings binding-forms)]
     (if ~test-form
       nil
       (do ~@body
           (recur ~@(inc-forms binding-forms))))))
