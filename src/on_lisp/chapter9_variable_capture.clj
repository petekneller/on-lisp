(ns on-lisp.chapter9-variable-capture)

; 9.1

; to replicate the example properly I need to define a clojure equiv of lisp's do macro

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
