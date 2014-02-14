(ns on-lisp.lisp-do.internal)

(defn- inc? [marker] (= :inc marker))

(defn- do-extract-bindings-1 [binding-forms bound]
  (if (or (nil? binding-forms) (empty? binding-forms))
    bound
    (let [[var init-form & rest-after-binding] binding-forms
          [inc-marker inc-form & rest-after-inc-form] rest-after-binding
          new-binding {:var var :init-form init-form :inc-form (if (inc? inc-marker) inc-form var)}
          rest (if (inc? inc-marker) rest-after-inc-form rest-after-binding)]
      (recur rest (conj bound new-binding)))))

(defn- do-extract-bindings [binding-forms]
  (do-extract-bindings-1 binding-forms []))

(defn let-bindings [binding-forms]
  (flatten (map #(vector (:var %) (:init-form %)) (do-extract-bindings binding-forms))))

(defn inc-forms [binding-forms]
  (map #(:inc-form %) (do-extract-bindings binding-forms)))
