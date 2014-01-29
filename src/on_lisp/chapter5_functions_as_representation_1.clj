(ns on-lisp.chapter5-functions-as-representation-1)

(def ^:dynamic *nodes* (hash-map))

; using data structs
(defstruct node :contents :yes :no)
(defn answer-node? [node] (nil? (:yes node)))

(defn defnode1 [name contents & [yes no]]
  (alter-var-root
    (var *nodes*)
    #(assoc % name (struct-map node
                     :contents contents
                     :yes yes
                     :no no))))

(defn run-node [name]
  (let [node (get *nodes* name)]
    (if (answer-node? node)
      (println (:contents node))
      (do
        (println (:contents node))
        (case (read-line)
          ("yes" "y") (run-node (:yes node))
          ("no" "n") (run-node (:no node)))))))


; using closures
(defn defnode2 [name contents & [yes no]]
  (alter-var-root
    (var *nodes*)
    #(assoc % name (if yes
      (fn []
        (do
          (println contents)
          (case (read-line)
            ("yes" "y") (apply (get *nodes* yes) [])
            ("no" "n") (apply (get *nodes* no) []))))
      (fn []
        (println contents))))))



(defn create-questions [create-node]
  (doall (map #(apply create-node %)
              [
                ['people "Is the person a man?" 'male 'female]
                ['male "Is he living?" 'liveman 'deadman]
                ['deadman "Was he American?" 'us 'them]
                ['us "Is he on a coin?" 'coin 'cidence]
                ['coin "Is the coin a penny?" 'penny 'coins]
                ['penny 'lincoln]])))


(defn -main []
  (do
    (create-questions defnode2)
    ; (run-node 'people) ;using data
    (apply (get *nodes* 'people) []) ;using closures
    ))