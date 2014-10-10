(ns on-lisp.chapter18b-destructuring-matching)

(declare form-matcher)

(defn vector-matcher [form arg]
  (let [pairs (map vector form arg)]
    (cond
      :else `(every? true? ~(vec (map #(apply form-matcher %) pairs))))))

(defn form-matcher [form arg]
  (cond
    (vector? form) (vector-matcher form arg)
    (and (symbol? form) (.startsWith (name form) "?")) `true
    :else `(= ~arg ~form)))

(defmacro match? [forms arg]
  (let [one (first forms)]
    (form-matcher one (eval arg))))