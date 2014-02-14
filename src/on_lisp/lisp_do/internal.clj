(ns on-lisp.lisp-do.internal)

(defn- inc? [marker] (= :inc marker))

(defn- extract-bindings-1 [bindings bound]
  (if (or (nil? bindings) (empty? bindings))
    bound
    (let [[var init-form & rest-after-binding] bindings
          [inc-marker inc-form & rest-after-inc-form] rest-after-binding
          new-binding (if (inc? inc-marker)
                        {:var var :init-form init-form :inc-form inc-form}
                        {:var var :init-form init-form})
          rest (if (inc? inc-marker) rest-after-inc-form rest-after-binding)]
      (recur rest (conj bound new-binding)))))

(defn do-extract-bindings [bindings]
  (extract-bindings-1 bindings []))
