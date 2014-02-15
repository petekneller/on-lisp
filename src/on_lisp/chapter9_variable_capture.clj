(ns on-lisp.chapter9-variable-capture
  (:require [on-lisp.lisp-do.public :refer :all]))

; 9.1 Variable capture

; the book example:
;(defmacro bad-for [[var start stop] & body] ; wrong
;  `(lisp-do [~var ~start :inc (+ 1 ~var)
;             limit ~stop]
;            (> ~var limit)
;            ~@body))
; won't compile. You get:
; CompilerException java.lang.RuntimeException: Can't let qualified name: on-lisp.chapter9-variable-capture/limit
; explained well in this blog post:
; http://thinkrelevance.com/blog/2008/12/17/on-lisp-clojure-chapter-9
; it turns out that clojure's symbol resolution takes care of a lot of macro capture issues

; can bypass the safety by unquote/requote-without-resolution the symbol in question
(defmacro bad-for [[var start stop] & body] ; wrong
  `(lisp-do [~var ~start :inc (+ 1 ~var)
             ~'limit ~stop]
            (> ~var ~'limit)
            ~@body))

; 9.2 Free symbol capture

(def w (atom []))

(defmacro gripe [warning]
  `(do
     (swap! ~'w conj ~warning) ; NB the unquote/requote of the global var
     nil))

(defn sample-ratio [v w]
  (let [vn (count v)
        wn (count w)]
    (if (or (< vn 2) (< wn 2))
      (gripe "sample < 2")
      (/ vn wn))))

; 9.6 Avoiding capture with gensyms

(defmacro good-for [[var start stop] & body] ; wrong
  `(lisp-do [~var ~start :inc (+ 1 ~var)
             limit# ~stop]
            (> ~var limit#)
            ~@body))
