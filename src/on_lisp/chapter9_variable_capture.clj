(ns on-lisp.chapter9-variable-capture)

; 9.1

; to replicate the example properly I need to define a clojure equiv of lisp's do macro
(defmacro lisp-do [bindings test-form & body]
  (let [[var init-form step-form] bindings]
    `(loop [~var ~init-form]
       (if ~test-form nil
                     (do ~@body
                         (recur ~step-form))))))

; eg
(lisp-do [x 1 :inc (+ 1 x)]
         (> x 5)
         (println x))





;(defmacro bad-for [var start stop & body] ; wrong
;  `(do ~var ~start (+ 1 ~var)
;          limit ~stop]))
;
;
;
;  `(do ((,var ,start (1+ ,var))
;        (limit ,stop))
;       ((> ,var limit))
;     ,@body))
