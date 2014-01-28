(ns on-lisp.basics
  (:require [midje.sweet :refer :all]))


; can use symbols as slots
(defstruct foo 'name 'owner)
(def foo-name (accessor foo 'name))

; or keywords
(defstruct bar :name :owner)
(def bar-owner (accessor bar :owner))

; or strings
(defstruct baz "name" "owner")
(def baz-name (accessor baz "name"))

; or anything I suppose


(facts "on structmaps"
       (fact "struct maps can be accessed using generated accessor function"
         (let [f (struct foo "foo" "tom")]
           (foo-name f) => "foo"))
       (fact "...or using std map funcs - get"
         (let [b (struct bar "bar" "harry")]
           (get b :owner) => "harry"))
       (fact "can be created with ordinal values using struct"
         (let [b (struct baz "baz" "mike")]
           (baz-name b) => "baz"))
       (fact "or by name using struct-map"
         (let [f (struct-map foo 'name "foo")]
           (foo-name f) => "foo"))
       (fact "if a key is not provided, it will be set to nil"
         (let [f1 (struct foo "foo") ; no owner
               f2 (struct-map foo 'name "foo")] ; no owner
           (get f1 'owner) => nil
           (get f2 'owner) => nil))
       (fact "extra keys can be provided when using struct-map"
         (let [b1 (struct-map bar :friendly "hi there!")]
           (get b1 :friendly) => "hi there!")))

