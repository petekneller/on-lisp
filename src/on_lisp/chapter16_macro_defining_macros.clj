(ns on-lisp.chapter16-macro-defining-macros)


; 16.1 Abbreviations

(defmacro macexp [& args]
  `(macroexpand ~@args))

(defmacro macexp-1 [& args]
  `(macroexpand-1 ~@args))

; derivation of abbrev

;(defmacro macexp [& args]
;  (let [name# `macroexpand]
;    `(~name ~@args)))

;`(defmacro ~short [& args]
;  (let [name# `~long]
;    `(~name ~@args)))

;`(defmacro ~short [& args]
;   `(~`~long ~@args))

(defmacro abbrev [short long]
  `(defmacro ~short [& args#]
     `(~'~long ~@args#)))

(defmacro abbrevs [& names]
  `(do
    ~@(map (fn [p] `(abbrev ~@p)) (partition 2 names))))


; 16.2 Properties

(def ball ^{:colour 'red} {}) ; try (meta ball) v (meta #'ball)

(defmacro colour [obj]
  `(:colour (meta ~obj)))

(defmacro weight [obj]
  `(:weight (meta ~obj)))

(defmacro propmacro [prop]
  `(defmacro ~prop [obj#]
    `(~'~(keyword (name prop)) (meta ~obj#))))

; 16.3 Anaphoric macros

(defn a+expand [args syms]
  (if (empty? args)
    `(+ ~@syms)
    (let [sym (gensym)]
      `(let [~sym ~(first args)
             ~'it ~sym]
         ~(a+expand (rest args) (conj syms sym))))))

(defmacro a+ [& args]
  (a+expand args []))


(defn pop-symbol [sym]
  (symbol (subs (name sym) 1)))

(defn anaphex [args expr]
  (if (empty? args)
    `(~@expr)
    (let [sym (gensym)]
      `(let [~sym ~(first args)
             ~'it ~sym]
         ~(anaphex (rest args) (conj expr sym))))))

(defmacro defanaph [name & calls]
  (let [calls (or calls (pop-symbol name))]
    `(defmacro ~name [& args#]
       (anaphex args# (vector '~calls)))))

(defanaph alist)






