(ns on-lisp.chapter5-functions-as-representation)

(defstruct node :contents :yes :no)
(defn answer-node? [node] (nil? (:yes node)))

(def ^:dynamic *nodes* (hash-map))

(defn defnode [name contents & [yes no]]
  (alter-var-root
    (var *nodes*)
    #(assoc % name (struct-map node
                     :contents contents
                     :yes yes
                     :no no))))

(defn create-questions []
  (doall (map #(apply defnode %) [
    ['people "Is the person a man?" 'male 'female]
    ['male "Is he living?" 'liveman 'deadman]
    ['deadman "Was he American?" 'us 'them]
    ['us "Is he on a coin?" 'coin 'cidence]
    ['coin "Is the coin a penny?" 'penny 'coins]
    ['penny 'lincoln]])))

(defn run-node [name]
  (let [node (get *nodes* name)]
    (if (answer-node? node)
      (println (:contents node))
      (do
        (println (:contents node))
        (case (read-line)
          ("yes" "y") (run-node (:yes node))
          ("no" "n") (run-node (:no node)))))))

(defn -main []
  (do
    (create-questions)
    (run-node 'people)))