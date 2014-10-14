(ns on-lisp.chapter14-anaphoric-macros)

; the messy way of unquote/requoting 'it' is necessary to get around the resolution of syntax-quoted forms
; https://groups.google.com/forum/#!topic/clojure/ELg9YnAtfDs

(defmacro aif [test then & else]
  `(let [~'it ~test]
     (if ~'it
       ~then
       ~@else)))


(defmacro awhen [test & body]
  `(let [~'it ~test]
     (when ~'it
       ~@body)))


(defmacro aand [& args]
  (cond
    (nil? args) true
    (nil? (rest args)) (first args)
    :else `(aif ~(first args) (aand ~@(rest args)) false)))

(defmacro acond [& clauses]
  (if (empty? clauses)
    nil
    (let [cl1 (take 2 clauses)
          sym (gensym)]
      `(let [~sym ~(first cl1)]
         (if ~sym
           (let [~'it ~sym] ~(second cl1))
           (acond ~@(drop 2 clauses)))))))
