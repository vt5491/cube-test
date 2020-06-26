;; Note: macros have to be in .clj or .cljc files, thus this separate file
(ns cube-test.macros)

; ;;yeah!
(defmacro when-let*
          [bindings & body]
          `(let ~bindings
                (if (and ~@(take-nth 2 bindings))
                  (do ~@body))))
