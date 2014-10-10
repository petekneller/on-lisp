(ns on-lisp.chapter14-anaphoric-macros-test
  (:require [midje.sweet :refer :all]
            [on-lisp.chapter14-anaphoric-macros :refer :all]))

(unfinished big-long-calculation, owner, address town)


(fact (aif (big-long-calculation) (> it 2)) => true
      (provided (big-long-calculation) => 3))

(fact (aif false (> it 2)) => nil)

(fact (awhen (> (big-long-calculation) 4) "hoorah") => nil
      (provided (big-long-calculation) => 3))

(fact (aand (owner "Porsche") (address it) (town it) (not (empty? it))) => true
      (provided
        (owner "Porsche") => "Mike"
        (address "Mike") => "Mike's house"
        (town "Mike's house") => "Sydney"))

; is the above equiv to the threading macro?
(fact (-> (owner "Porsche")
          (address)
          (town)
          (nil?)) => false
      (provided
        (owner "Porsche") => "Mike"
        (address "Mike") => "Mike's house"
        (town "Mike's house") => "Sydney"))
; interesting

(fact (acond
        (count "Fooey") (+ 1 it)
        :else 0) => 6)
