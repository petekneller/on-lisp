(ns on-lisp.lisp-do.public
  (:require [on-lisp.lisp-do.internal :refer :all]))

(defmacro lisp-do [bindings test-form & body]
  (let [[var init-form step-form] bindings]
    `(loop [~var ~init-form]
       (if ~test-form nil
                      (do ~@body
                          (recur ~step-form))))))
