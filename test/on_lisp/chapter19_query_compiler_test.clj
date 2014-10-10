(ns on-lisp.chapter19-query-compiler-test
  (:require [midje.sweet :as m]
            [on-lisp.chapter19-query-compiler :refer :all]))

(m/fact "basic query functions"
        (do
          (clear-db)
          (fact :painter ["reynolds" "joshua" :english])
          (fact :painter ["canale" "antonio" :venetian])
          (db-query :painter) => [["canale" "antonio" :venetian]
                                  ["reynolds" "joshua" :english]]))
