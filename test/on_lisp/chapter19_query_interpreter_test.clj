(ns on-lisp.chapter19-query-interpreter-test
  (:require [midje.sweet :as m]
            [on-lisp.chapter19-query-interpreter :refer :all]))

(m/fact "basic query functions"
        (clear-db)
        (fact :painter ["reynolds" "joshua" :english])
        (fact :painter ["canale" "antonio" :venetian])
        (db-query :painter) => [["canale" "antonio" :venetian]
                                ["reynolds" "joshua" :english]])

(defn set-test-data []
  (do
    (clear-db)
    (fact :painter ["hogarth" "william" "english"])
    (fact :painter ["canale" "antonio" "venetian"])
    (fact :painter ["reynolds" "joshua" "english"])
    (fact :dates ["hogarth" 1697 1772])
    (fact :dates ["canale" 1697 1768])
    (fact :dates ["reynods" 1723 1792])))

(m/fact "lookup"
        (set-test-data)
        (lookup :painter ['?x '?y "english"]) => (m/just
                                                   [{'?x "hogarth" '?y "william"}
                                                    {'?x "reynolds" '?y "joshua"}]
                                                   :in-any-order)
        (lookup :painter ['?x '?y "french"]) => []
        (lookup :painter ['?x '?y "english"] [{'?x "hogarth"}]) => [{'?x "hogarth" '?y "william"}]
        (lookup :painter ['?x '?y "english"] [{'?x "hogarth"} {'?x "reynolds"}]) => (m/just
                                                                                      [{'?x "hogarth" '?y "william"}
                                                                                       {'?x "reynolds" '?y "joshua"}]
                                                                                      :in-any-order)
        (lookup :painter ['?x '?y "english"] []) => [])

(m/facts "interpret-query"
         (m/fact "simple predicate"
                 (set-test-data)
                 (interpret-query [:dates ['?x 1697 '?w]]) => (m/just
                                                                [{'?w 1768 '?x "canale"}
                                                                 {'?w 1772 '?x "hogarth"}]
                                                                :in-any-order)
                 (interpret-query [:dates ['?x 1697 '?w]] [{'?x "canale"}]) => [{'?w 1768 '?x "canale"}]
                 (interpret-query [:dates ['?x 1697 '?w]] [{'?x "reynolds"}]) => [])

         (m/fact "and combinator"
                 (set-test-data)
                 (interpret-and [[:dates ['?x 1697 '?w]]] [{'?x "hogarth"} {'?x "canale"}]) => (m/just
                                                                      [{'?w 1768 '?x "canale"}
                                                                       {'?w 1772 '?x "hogarth"}]
                                                                      :in-any-order)
                 (interpret-and [[:dates ['?x 1697 '?w]]
                                 [:painter ['?x '?y '?z]]] [{}]) => (m/just
                                                                      [{'?w 1768 '?z "venetian" '?y "antonio" '?x "canale"}
                                                                       {'?w 1772 '?z "english" '?y "william" '?x "hogarth"}]
                                                                      :in-any-order)
                 (interpret-query [:and
                                   [:dates ['?x 1697 '?w]]
                                   [:painter ['?x '?y '?z]]]) => (m/just
                                                                   [{'?w 1768 '?z "venetian" '?y "antonio" '?x "canale"}
                                                                    {'?w 1772 '?z "english" '?y "william" '?x "hogarth"}]
                                                                   :in-any-order)))
