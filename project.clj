(defproject cube-test "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.764"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library
                               org.clojure/google-closure-library-third-party]]
                 ; [thheller/shadow-cljs "2.9.3"]
                 [thheller/shadow-cljs "2.11.7"]
                 ; [thheller/shadow-cljs "2.11.20"]
                 [reagent "0.10.0"]
                 ; [re-frame "0.12.0"]
                 [re-frame "1.2.0"]
                 [funcool/promesa "5.1.0"]
                 ;;vt add
                 [cljstache "2.0.6"]
                 [net.sekao/odoyle-rules "0.8.0"]]
                 ; [proto-repl "1.4.24"]
                 ;; Note: proto-repl .jar has a different version than proto-repl the atom plugin
                 ;; so use "0.3.1" not "1.4.24"
                 ;; Note: need to add to ":profiles" so it's picked up by ":shadow-cljs"
                 ;; and subsequently put into (auto-generated)"shadow-cljs.edn"
                 ; [proto-repl "0.3.1"]]
                 ;; vt add for string interpolation
                 ;; nope: only works for clj
                 ; [org.clojure/core.incubator "0.1.4"]]

  :plugins [
            ; [lein-shadow "0.2.0"]
            [lein-shadow "0.3.1"]
            [lein-shell "0.5.0"]]

  :min-lein-version "2.9.0"

  ;;:source-paths ["src/clj" "src/cljs"]
  :source-paths ["src/clj" "src/cljs" "src/cljc" "src/test" "test2" "test"]

  ;;vt add for re-frame-10x
  :compiler    {
                 :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true}
                 :preloads             [day8.re-frame-10x.preload]
                 :main                 "re-con.core"}
  ;;vt end
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]


  :shell {:commands {"open" {:windows ["cmd" "/c" "start"]
                             :macosx  "open"
                             :linux   "xdg-open"}}}

  :shadow-cljs {:nrepl {:port 8778}
                ;;vt add
                ;; update 2020-02-04
                ;; this works in that it creates 'https://localhost:8281/', and thus creates
                ;; a navigator.xr in the browser.  However, xr-mode in firefox and chrome still
                ;; doesn't work.
                ;; Note: if using the oculus quest, the two browsers on there require https in order to enter vr mode
                :ssl {:keystore "dev_artifacts/ssl/keystore2.jks"
                      :password "shadow-cljs"}
                ;;vt end
                :builds {:app {:target :browser
                               :output-dir "resources/public/js/compiled"
                               :asset-path "/js/compiled"
                               :modules {:app {:init-fn cube-test.core/init
                                               :preloads [devtools.preload
                                                          ;;vt add
                                                          day8.re-frame-10x.preload]}}
                               ;;vt add
                               ; :js-options {:ignore-asset-requires true}
                               ;;vt end
                               ;;vt add
                               :dev {:compiler-options {
                                                        :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true}
                                                        :preloads             [day8.re-frame-10x.preload]}}
                               ;;vt end
                               :devtools {:http-root "resources/public"
                                          :http-port 8281}}}}


  :aliases {"dev"          ["with-profile" "dev" "do"
                            ["shadow" "watch" "app"]]
            ;; vt add watch from a more modern project
            "watch"         ["with-profile" "dev" "do"
                             ["shadow" "watch" "app" "browser-test" "karma-test"]]
            "prod"         ["with-profile" "prod" "do"
                            ["shadow" "release" "app"]]
            "build-report" ["with-profile" "prod" "do"
                            ["shadow" "run" "shadow.cljs.build-report" "app" "target/build-report.html"]
                            ["shell" "open" "target/build-report.html"]]
            "karma"        ["with-profile" "prod" "do"
                            ["shadow" "compile" "karma-test"]
                            ["shell" "karma" "start" "--single-run" "--reporters" "junit,dots"]]
            ;;vt-add
            ; "fig:build" ["run" "-m" "figwheel.main" "-b" "devfw" "-r"]
            ; "devfw" ["run" "-m" "figwheel.main" "-b" "devfw" "-r"]
            ; run with: lein.bat trampoline devfw
            ; update: run with: lein.bat devfw since we added "trampoline" before "run" below
            "devfw" ["with-profile" "devfw" "do"
                     ; ["run" "-m" "figwheel.main" "-b" "devfw" "-r"]
                     ["trampoline" "run" "-m" "figwheel.main" "-b" "devfw" "-r"]]}
                     ; ["trampoline" "-m" "figwheel.main" "-b" "devfw" "-r"]]}

  :profiles
  {:dev
    {:dependencies [[binaryage/devtools "1.0.0"]
                    ;;vt add
                    [day8.re-frame/re-frame-10x "0.7.0"]]
                        ; [day8.re-frame/re-frame-10x "0.4.7"]]
                        ; [day8.re-frame/tracing "0.5.3"]]
                        ; [proto-repl/proto-repl "0.3.1"]]
                        ;;vt end
     :source-paths ["dev"]}
   ;; vt add for re-frame-10x
   ; [day8.re-frame/re-frame-10x "0.7.0"]}
   ; [day8.re-frame/tracing "0.5.3"]}
   ;;vt end
   ;;vt add
   :devfw
    {:dependencies [[com.bhauman/figwheel-main "0.2.12"]
                    [com.bhauman/rebel-readline-cljs "0.1.4"]
                    [reagent "0.10.0"]
                    [re-frame "1.2.0"]]
     :resource-paths ["target"]
     :source-paths ["src"]
     ;; need to add the compiled assets to the :clean-targets
     :clean-targets ^{:protect false} ["target"]}
   ;; vt-end
   :prod {}}


  :prep-tasks [])
