(ns on-lisp.chapter9-variable-capture-test
  (:require [on-lisp.chapter9-variable-capture :refer :all]
             [midje.sweet :refer :all]))

; 9.1 Variable capture

; works okay
; (bad-for [x 1 5] (println x))

; doesn't work
; (bad-for [limit 1 5] (println limit)) ;=> prints '5' forever

; also doesn't work
;(let [limit 5]
;  (bad-for [i 1 10]
;           (when (> i limit) (println i)))) ; => nil


; 9.2 Free symbol capture

; calling sample-ratio like so:
;(sample-ratio [1] [1]) ; => ClassCastException clojure.lang.PersistentVector cannot be cast to clojure.lang.Atom  clojure.core/swap!
; after unquote/requoting the global var in gripe (which would have prevented any problems)...
; the 'w' symbol in sample-ratio is captured, but a nasty error is prevented since you cant mutate the arg

; 9.6 Avoiding capture with gensyms

`(x# y#) ; => (x__1577__auto__ y__1578__auto__)
`(~(gensym) ~(gensym)) ; => (G__1599 G__1600)

(good-for [limit 1 5] (println limit)) ; now works
