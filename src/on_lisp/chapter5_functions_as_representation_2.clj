(ns on-lisp.chapter5-functions-as-representation-2)

(def ^:dynamic *nodes* (hash-map))

(defn defnode [name & args]
  (alter-var-root
    (var *nodes*)
    #(assoc % name args)))

(defn compile-net [root]
  (let [node (get *nodes* root)]
    (if (nil? node)
      nil
      (let [contents (nth node 0)
            yes (nth node 1)
            no (nth node 2)]
        (if (nil? yes)
          (fn [] (println contents))
          (let [yes-fn (compile-net yes)
                  no-fn (compile-net no)]
              (fn []
                (do
                  (println contents)
                  (apply (case (read-line)
                           ("yes", "y") yes-fn
                           ("no", "n") no-fn) [])))))))))

(defn create-questions []
  (doall (map #(apply defnode %)
              [
                ['people "Is the person a man?" 'male 'female]
                ['male "Is he living?" 'liveman 'deadman]
                ['deadman "Was he American?" 'us 'them]
                ['us "Is he on a coin?" 'coin 'cidence]
                ['coin "Is the coin a penny?" 'penny 'coins]
                ['penny 'lincoln nil nil]])))

(defn -main []
  (do
    (create-questions)
    (apply (compile-net 'people) [])))
