(ns clojure-arduino.core
  (:use :reload-all clodiuno.core)
  (:use :reload-all clodiuno.firmata)
  (:require [clojure-arduino.gmail :as gmail]))

(System/setProperty "gnu.io.rxtx.SerialPorts" "/dev/ttyACM0")

(def cnt (ref 0))
(def last-id (ref nil))
(def board (arduino :firmata "/dev/ttyACM0"))

(defn check-mail [board]
  (digital-write board 11 HIGH)
  (dosync
    (ref-set cnt (gmail/unread-count)))
  (digital-write board 11 LOW))

(defn -main []
  (pin-mode board 12 OUTPUT)
  (pin-mode board 11 OUTPUT)
  (enable-pin board :digital 3)
  (pin-mode board 3  INPUT)
  ;(digital-read board 3)
  (while true
    (check-mail board)
    (if (> @cnt 0)
      (digital-write board 12 HIGH)
      (digital-write board 12 LOW))
    (Thread/sleep (* 5 1000)))
  (close board))
