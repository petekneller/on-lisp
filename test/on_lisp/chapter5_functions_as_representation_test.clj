(ns on_lisp.chapter5_functions_as_representation_test
  (:require [on-lisp.chapter5-functions-as-representations :refer :all]
            [midje.sweet :refer :all]))

(fact "" (defnode "tom" "hello world") => nil)