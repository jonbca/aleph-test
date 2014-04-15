(ns aleph-test.core
  (:use compojure.core)
  (:use lamina.core aleph.http)
  (:require [compojure.handler :as handler]
            [compojure.route :as route])
  (:gen-class))

(def sse-channel (permanent-channel))
(def event-id (atom 0))

(defn handle-event-request []
  (let [this-event-id (swap! event-id inc)]
    (enqueue sse-channel (str "id: " this-event-id "\ndata: hello!\n\n"))
    "ok\n"))

(defroutes app-routes
  (GET "/events" []
    {:headers {"Content-Type" "text/event-stream"}
     :body sse-channel})
  (POST "/trigger" []
    {:status 202
     :headers {"Content-Type" "text/plain"}
     :body (handle-event-request)}))

(def app
  (handler/site app-routes))

(defn -main
  [& args]
  (start-http-server (wrap-ring-handler app)
    {:port 3000
     :netty {"tcpNoDelay" true}}))
