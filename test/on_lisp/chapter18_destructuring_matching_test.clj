(ns on-lisp.chapter18-destructuring-matching-test
  (:require [on-lisp.chapter18-destructuring-matching :refer :all]
            [midje.sweet :refer :all]))

(fact "match works"
      (match? ['p 'a 'b 'c 'a] ['p '?x '?y 'c '?x]) => ['?y 'a '?y 'b]
      (match? ['p '?x 'b '?y 'a] ['p '?y 'b 'c 'a]) => ['?y 'c '?x '?y]
      (match? ['a 'b 'c] ['a 'a 'a]) => nil)