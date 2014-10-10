(ns on-lisp.chapter19-query-compiler)


(def default-db (atom {}))

(defn clear-db []
  (swap! default-db (fn [_] {})))

(defn db-push [key val]
  (swap! default-db (fn [db] (assoc db key (conj (get db key) val)))))

(defmacro fact [pred args]
  `(do
    (db-push ~pred ~args)
    ~args))

(defmacro db-query [key]
  `(get (deref default-db) ~key []))
