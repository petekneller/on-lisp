(ns on-lisp.chapter6-macros
  (:require [clojure.core :refer :all]))


(str "hello" "world") ; => helloworld
'(str "hello" "world") ; => (str hello world)
; syntax quote resolves symbols
`(str "hello" "world") ; => (clojure.core/str hello world)
; both forms of quoting are recursive
'(str "hel" (str "lo" "wo") "rld") ; => (str hel (str lo wo) rld)
`(str "hel" (str "lo" "wo") "rld") ; => (clojure.core/str hel (clojure.core/str lo wo) rld)
; quoting is of course necessary for literal lists
(1 2 3) ; => ClassCastException java.lang.Long cannot be cast to clojure.lang.IFn
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
`(str "hel" (str ~("lo" "wo")) "rld") ; => ClassCastException java.lang.Long cannot be cast to clojure.lang.IFn
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


; move these somewhere else
; destructuring - vectors
(let [[a b] []]
  {:A a :B b}) ; => {:A nil, :B nil} => can always destructure vectors even when there aren't enough elems
(let [[a b :as all] []]
  {:A a :B b :ALL all}) ; => {:A nil, :B nil, :ALL []} => :as is the equiv of an @ binding in scala/haskell pattern matching
(let [[a b & rest :as all] []]
  {:A a :B b :ALL all :REST rest}) ; => {:A nil, :B nil, :ALL [], :REST nil}
(let [[a b & rest :as all] [1]]
  {:A a :B b :ALL all :REST rest}) ; => {:A 1, :B nil, :ALL [1], :REST nil}
(let [[a b & rest :as all] [1 2]]
  {:A a :B b :ALL all :REST rest}) ; => {:A 1, :B 2, :ALL [1 2], :REST nil}
(let [[a b & rest :as all] [1 2 3]]
  {:A a :B b :ALL all :REST rest}) ; => {:A 1, :B 2, :ALL [1 2 3], :REST (3)}
; destructuring - maps
(let [{a :a b :b} {}]
  [:A a :B b]) ; => [:A nil :B nil] => same goes for missing keys when destructuring maps
(let [{a :a b :b} {:a 2}]
  [:A a :B b]) ; => [:A 2 :B nil]
(let [{:keys [a b]} {:a 2}]
  [:A a :B b]) ; => [:A 2 :B nil]
(let [{:keys [a b] :as all} {:a 2}]
  [:A a :B b :ALL all]) ; => [:A 2 :B nil :ALL {:a 2}]
(let [{:keys [a b] :or {b 3} :as all} {:a 2}]
  [:A a :B b :ALL all]) ; => [:A 2 :B 3 :ALL {:a 2}]


(defmacro macexp [form]
  `(pprint (macroexpand-1 form)))

(defn my-print [exp]
  (do
    (println "inside macro")
    `(str ~(str "my-print" ": ") exp)))
