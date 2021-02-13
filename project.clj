(defproject cube-test "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.764"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library
                               org.clojure/google-closure-library-third-party]]
                 [thheller/shadow-cljs "2.9.3"]
                 [reagent "0.10.0"]
                 [re-frame "0.12.0"]
                 [funcool/promesa "5.1.0"]
                 ;;vt add
                 [cljstache "2.0.6"]]
                 ;; vt add for string interpolation
                 ;; nope: only works for clj
                 ; [org.clojure/core.incubator "0.1.4"]]

  :plugins [[lein-shadow "0.2.0"]

            [lein-shell "0.5.0"]]

  :min-lein-version "2.9.0"

  ;;:source-paths ["src/clj" "src/cljs"]
  :source-paths ["src/clj" "src/cljs" "src/cljc" "src/test" "test2" "test"]

  ;;vt add for re-frame-10x
  ; :compiler    {
  ;                :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true}
  ;                :preloads             [day8.re-frame-10x.preload]
  ;                :main                 "re-con.core"}
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
                                               :preloads [devtools.preload]}}
                                                          ;;vt add
                                                          ; day8.re-frame-10x.preload]}}
                                ;;vt add
                               :dev {:compiler-options {
                                                        :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true}
                                                        :preloads             [day8.re-frame-10x.preload]}}
                                ;;vt end
                               :devtools {:http-root "resources/public"
                                          :http-port 8281}}}}


  :aliases {"dev"          ["with-profile" "dev" "do"
                            ["shadow" "watch" "app"]]
            "prod"         ["with-profile" "prod" "do"
                            ["shadow" "release" "app"]]
            "build-report" ["with-profile" "prod" "do"
                            ["shadow" "run" "shadow.cljs.build-report" "app" "target/build-report.html"]
                            ["shell" "open" "target/build-report.html"]]
            "karma"        ["with-profile" "prod" "do"
                            ["shadow" "compile" "karma-test"]
                            ["shell" "karma" "start" "--single-run" "--reporters" "junit,dots"]]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "1.0.0"]
                   ;;vt add
                   [day8.re-frame/re-frame-10x "0.7.0"]]
                   ; [day8.re-frame/re-frame-10x "0.4.7"]]
                   ; [day8.re-frame/tracing "0.5.3"]]
                   ;;vt end
    :source-paths ["dev"]}
    ;; vt add for re-frame-10x
                ; [day8.re-frame/re-frame-10x "0.7.0"]}
                ; [day8.re-frame/tracing "0.5.3"]}
    ;;vt end


   :prod {}}



  :prep-tasks [])
