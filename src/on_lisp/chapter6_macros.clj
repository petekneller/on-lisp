(ns on-lisp.chapter6-macros)


(str "hello" "world") ; => helloworld
'(str "hello" "world") ; => (str hello world)
; syntax quote resolves symbols
`(str "hello" "world") ; => (clojure.core/str hello world)
; both forms of quoting are recursive
'(str "hel" (str "lo" "wo") "rld") ; => (str hel (str lo wo) rld)
`(str "hel" (str "lo" "wo") "rld") ; => (clojure.core/str hel (clojure.core/str lo wo) rld)
; quoting is of course necessary for literal lists
;(1 2 3) ; => ClassCastException java.lang.Long cannot be cast to clojure.lang.IFn
'(1 2 3) ; => (1 2 3)
''(1 2 3) ; => (quote (1 2 3))
'''(1 2 3) ; => (quote (quote (1 2 3)))
`(1 2 3) ; => (1 2 3)
``(1 2 3) ; => (clojure.core/seq (clojure.core/concat (clojure.core/list 1) (clojure.core/list 2) (clojure.core/list 3))) => WTF!
`'(1 2 3) ; => (quote (1 2 3))
; syntax quote allows use of unquote and splicing
`(str "hel" ~(str "lo" "wo") "rld") ; => (clojure.core/str hel lowo rld)
`~(str "hel" (str "lo" "wo") "rld") ; => helloworld
; unquoting a literal list is gonna break...
;`(str "hel" (str ~("lo" "wo")) "rld") ; => ClassCastException java.lang.String cannot be cast to clojure.lang.IFn
; ... but not using splicing unquote
`(str "hel" (str ~'("lo" "wo")) "rld") ; => (clojure.core/str hel (clojure.core/str (lo wo)) rld)
`(str "hel" (str ~@'("lo" "wo")) "rld") ; => (clojure.core/str hel (clojure.core/str lo wo) rld)
; normal quote doesnt allow use of unquoting
'(str "hel" ~(str "lo" "wo") "rld") ; => (str hel (clojure.core/unquote (str lo wo)) rld)
'(str "hel" (str ~@'("lo" "wo")) "rld") ; => (str hel (str (clojure.core/unquote-splicing (quote (lo wo)))) rld)


; args to macroexpand/macroexpand-1 need to be quoted, else they'll be evaluated
(macroexpand-1 (vector 1 2)) ; => [1 2]
(macroexpand-1 '(vector 1 2)) ; => (vector 1 2)
; macroexpand-1 ignores forms that dont evaluate to a macro call
(macroexpand-1 '(vector 1 2)) ; => [list 1 2]
(defmacro to-str [form] `(str ~form))
(to-str (vector 1 2)) ; => "[1 2]"
(macroexpand-1 '(to-str (vector 1 2))) ; => (clojure.core/str (vector 1 2))
; interestingly, if the macro knew its arg was a list...
(defmacro to-str2 [form] `(str ~@form))
(to-str2 (vector 1 2)) ; => "clojure.core$vector@a65dac12" => oops! see the 1 2 tacked onto the end?
(macroexpand-1 '(to-str2 (vector 1 2))) ; => (clojure.core/str vector 1 2)
; I suppose splicing unquote should properly be used with real sequences; like rest params
(defmacro to-str3 [& forms] `(str ~@forms))
(to-str3 (vector 1 2)) ; => "[1 2]"
(macroexpand-1 '(to-str3 (vector 1 2))) ; => (clojure.core/str (vector 1 2))
(to-str3 1 2) ; => "12"
(macroexpand-1 (to-str3 1 2)) ; => (clojure.core/str 1 2)

; can I mimic the compile-time expansion of macros at runtime?
(defmacro my-str-m [& args]
  `(str "prefix-" ~@args "-suffix"))
(my-str-m 1 (+ 1 2) 5) ; => "prefix-135-suffix"
(macroexpand-1 '(my-str-m 1 (+ 1 2) 5)) ; => (clojure.core/str "prefix-" 1 (+ 1 2) 5 "-suffix")
(defn my-str-f [& args]
  `(str "prefix-" ~@args "-suffix"))
(my-str-f 1 (+ 1 2) 5) ; => (clojure.core/str "prefix-" 1 3 5 "-suffix")
(my-str-f '1 '(+ 1 2) '5) ; => (clojure.core/str "prefix-" 1 (+ 1 2) 5 "-suffix")
(eval (my-str-f '1 '(+ 1 2) '5)) ; => "prefix-135-suffix"
; so the above is the runtime equivalent of a macro call; although the quotes on the integers aren't really necessary



(defmacro macexp [form]
  `(pprint (macroexpand-1 ~form)))

(defn my-print [exp]
  (do
    (println "inside macro")
    `(str ~(str "my-print" ": ") exp)))
