(ns on-lisp.chapter18-destructuring-matching-test
  (:require [on-lisp.chapter18-destructuring-matching :refer :all]
            [midje.sweet :refer :all]))

(facts "match"
       (fact "arguments of different sizes do not match"
             (match? [1] [1 2]) => nil)
       (fact "literals are self explanatory"
             (match? [] []) => {}
             (match? [1] [2]) => nil
             (match? [1] [1]) => {})
       (fact "underscores always match"
             (match? ['_] [2]) => {}
             (match? [1] ['_]) => {})
       (fact "symbols beginning with ? establish bindings"
             (match?
               ['?a]
               [1]) => {'?a 1}
             (match?
               [1   2]
               ['?a '?b]) => {'?a 1 '?b 2}
             (match?
               ['_ '?a]
               [1   2]) => {'?a 2})
       (fact "symbols can be refer to previously established bindings"
             (match?
               ['?a   '?a]
               [1     1]) => {'?a 1}
             (match?
               ['?a   '?a]
               [1     2]) => nil
             (match?
               ['?a '?b   '?b]
               [1   '?a   2]) => nil))

;(facts "if-match"
;       (let [abab (fn [seq]
;                    (if-match ['?x '?y '?x '?y] seq
;                              ['?x '?y]
;                              nil))]
;         (fact (abab ["hi" "ho" "hi" "ho"]) => ["hi" "ho"])
;         (fact (abab ["hi" "ho" "he" "hu"]) => nil)))