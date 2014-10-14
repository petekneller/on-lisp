(ns on-lisp.chapter19-query-compiler-test
  (:require [midje.sweet :as m]
            [on-lisp.chapter19-query-compiler :refer :all]
            [on-lisp.chapter19-query-interpreter-test :refer [set-test-data]]))


(m/fact "with-answer"
        (set-test-data)
        (m/fact "simple"
              (with-answer [:painter ["hogarth" ?x ?y]]
                           [?x ?y]) => (m/just [["william" "english"]]))
        (m/fact "and combinator"
              (with-answer [:and
                            [:painter [?x _ _]]
                            [:dates [?x 1697 _]]]
                           ?x) => (m/just ["canale" "hogarth"]))
        (m/fact "or combinator"
              (with-answer [:or
                            [:dates [?x ?y 1772]]
                            [:dates [?x ?y 1792]]]
                           [?x ?y]) => (m/just [["hogart" 1697] ["reynolds" 1723]] :in-any-order))
        (m/fact "not combinator"
              (with-answer [:and
                            [:painter [?x _ "english"]]
                            [:dates ?x ?b _]
                            [:not [:and
                                   [:painter [?x2 _ "venetian"]]
                                   [:dates ?x2 ?b _]]]]
                           ?x) => (m/just "reynolds"))
        (m/fact "arbitrary clojure"
              (with-answer [:and
                            [:painter ?x _ _]
                            [:dates ?x _ ?d]
                            [:lisp (< 1770 ?d 1800)]]
                           [?x ?d]) => (m/just [["reynolds" 1792] ["hogarth" 1772]])))
