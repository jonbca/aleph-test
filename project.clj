(defproject aleph-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [lamina "0.5.2"]
                 [aleph "0.3.2"]
                 [compojure "1.1.6"]
                 [javax.servlet/servlet-api "2.5"]]
  :main ^:skip-aot aleph-test.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
