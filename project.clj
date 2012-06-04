(defproject clojure-arduino "1.0.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [clodiuno "0.0.3-SNAPSHOT"]
                 [serial-port "1.1.2"]
                 [javax.mail/mail "1.4.3"]]
  :dev-dependencies [[native-deps "1.0.5"]]
  :native-dependencies [[rxtx22 "1.0.6"]]
  :jvm-opts ["-Djava.library.path=./native/linux/x86/"
             "-d64"
             "-server"]
  :main clojure-arduino.core)
