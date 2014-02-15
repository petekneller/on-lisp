(ns on-lisp.chapter11-classic-macros)

(defmacro our-let-s [bindings & body]
  `(apply
     (fn [~(first bindings)]
       ~(if (empty? (drop 2 bindings))
          `(do ~@body)
          `(our-let-s ~(drop 2 bindings) ~@body)))
     ~(second bindings)))

(defmacro our-let-p [bindings & body]
  `(apply
     (fn [~@(map first (partition 2 bindings))]
       (do ~@body))
     [~@(map second (partition 2 bindings))]))


; 11.3 Conditional evaluation

(defmacro if3 [test t-case nil-case ?-case]
  `(case ~test
     nil    ~nil-case
     ?      ~?-case
     :else  ~t-case))