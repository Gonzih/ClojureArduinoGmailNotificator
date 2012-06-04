(ns clojure-arduino.gmail
  (:use [clojure.java.io :only (reader resource)])
  (:import [java.util Properties]
           [javax.mail Authenticator PasswordAuthentication Session Folder]
           [java.util Properties]))

(defn load-properties [src]
  (with-open [rdr (reader src)]
    (doto (Properties.)
          (.load rdr))))

(defn config []
  (load-properties (resource "gmail.properties")))

(def user (.get (config) "user"))
(def pass (.get (config) "pass"))
(def imap "imap.gmail.com")

(def props (doto (Properties.)
                 (.put "mail.imap.host" imap)
                 (.put "mail.imap.user" user)
                 (.put "mail.imap.socketFactory" 993)
                 (.put "mail.imap.socketFactory.class" "javax.net.ssl.SSLSocketFactory")
                 (.put "mail.imap.port" 993)))

(def auth (proxy [Authenticator] [] (getPasswordAuthentication [] (PasswordAuthentication. user pass))))
(def session (Session/getDefaultInstance props auth))
(def imap (.getStore session "imap"))

(.connect imap user pass)
(def fold (.getFolder imap "INBOX"))

(defmacro with-folder [method & args]
  `(do (.open fold (Folder/HOLDS_MESSAGES))
       (let [value# (~method fold ~@args)]
            (.close fold false)
            value#)))

(defn unread-count []
  (with-folder .getUnreadMessageCount))

(defn total-count []
  (with-folder .getMessageCount))

(defn last-message-id []
  (.getMessageNumber (first (with-folder .getMessages))))
