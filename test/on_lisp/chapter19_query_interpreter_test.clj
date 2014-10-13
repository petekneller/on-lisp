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
    (fact :dates ["reynolds" 1723 1792])))

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
                                                                   :in-any-order))
         (m/fact "or combinator"
                 (set-test-data)
                 (interpret-query [:or
                                   [:dates ['?x '?y 1772]]
                                   [:dates ['?x '?y 1792]]]) => (m/just
                                                                  [{'?x "reynolds" '?y 1723}
                                                                   {'?x "hogarth" '?y 1697}]
                                                                  :in-any-order))

         (m/fact "not combinator"
                 (set-test-data)
                 (interpret-not [:painter ['?x '_ "english"]] [{'?x "reynolds"}]) => []
                 (interpret-not [:painter ['?x '_ "venetian"]] [{'?x "reynolds"}]) => [{'?x "reynolds"}]
                 (interpret-query [:and
                                   [:painter ['?x '_ "english"]]
                                   [:dates ['?x '?b '_]]
                                   [:not
                                    [:and
                                     [:painter ['?x2 '_ "venetian"]]
                                     [:dates ['?x2 '?b '_]]]]]) => [{'?x "reynolds" '?b 1723}]))
