(ns on-lisp.chapter5_functions_as_representation)

(defstruct node :contents :yes :no)

(def ^:dynamic *nodes* (hash-map))

(defn defnode [name contents & optionals]
  (alter-var-root
    (var *nodes*)
    #(assoc % name (struct-map node
                     :contents contents
                     :yes (:yes optionals)
                     :no (:no optionals)))))
