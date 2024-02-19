(ns web-app.core
  (:require [ring.adapter.jetty :as jetty]
            [clojure.pprint :as pprint]
            [compojure.core :as comp]
            [compojure.route :as route]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]))

;; (defn foo
;;   "I don't do a whole lot."
;;   [x]
;;   (println x "Hello, World!"))

(defonce server (atom nil)) ;; defonce instead of def, when we reload the file, it won't overwrite the variable

;; (defn app [req])

(comp/defroutes routes
  (comp/GET "/" [] {:status 200
                    :body "<h1>Homepage</h1>
                             <ul>
                                 <li><a href=\"/echo\">Echo request</a></li>
                                 <li><a href=\"/greeting\">Greeting</a></li>
                             </ul>"
                    :headers {"Content-Type" "text/html; charset=UTF-8"}})
  (comp/ANY "/echo" req {:status 200
                         :body (with-out-str (pprint/pprint req)) ;; pretty print the ring request 
                         :headers {"Content-Type" "text/plain"}})
  (comp/GET "/greeting" [] {:status 200
                            :body "Hello, World!"
                            :headers {"Content-Type" "text/plain"}})
  (route/not-found {:status 404
                    :body "404 Not Found! Error!!!!" ;; pretty print the ring request 
                    :headers {"Content-Type" "text/plain"}}))


#_(def app (wrap-params (wrap-keyword-params routes)))
(def app
  (-> routes
      wrap-keyword-params
      wrap-params))



(defn start-server []
  (reset! server
          (jetty/run-jetty (fn [req] (app req))  ;; call app
                           {:port 3001           ;; listen on port 3001
                            :join? false})))     ;; don't block the main thread

(defn stop-server []
  (when-some [s @server] ;; check if there is an object in the atom
    (.stop s) ;; call the .stop method
    (reset! server nil))) ;; overwrite the atom with nil

