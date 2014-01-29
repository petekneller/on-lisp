(ns on-lisp.chapter5-functions-as-representation)

(defstruct node :contents :yes :no)

(def ^:dynamic *nodes* (hash-map))

(defn defnode [name contents & [yes no]]
  (alter-var-root
    (var *nodes*)
    #(assoc % name (struct-map node
                     :contents contents
                     :yes yes
                     :no no))))

(defn create-questions []
  (do
    (defnode 'people "Is the person a man?" 'male 'female)
    (defnode 'male "Is he living?" 'liveman 'deadman)
    (defnode 'deadman "Was he American?" 'us 'them)
    (defnode 'us "Is he on a coin?" 'coin 'cidence)
    (defnode 'coin "Is the coin a penny?" 'penny 'coins)
    (defnode 'penny 'lincoln)))

(defn run-node [name]
  (let [node (get *nodes* name)]
    (if (nil? (:yes node))
      (:contents node)
      (do
        (println (:contents node))
        (case (read-line)
          ("yes" "y") (run-node (:yes node))
          ("no" "n") (run-node (:no node)))))))