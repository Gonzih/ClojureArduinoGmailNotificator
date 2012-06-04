(ns clojure-arduino.core
  (:use :reload-all clodiuno.core)
  (:use :reload-all clodiuno.firmata)
  (:require [clojure-arduino.gmail :as gmail]))

(System/setProperty "gnu.io.rxtx.SerialPorts" "/dev/ttyACM0")

(def cnt (ref 0))
(def last-id (ref nil))
(def last-id-pressed (atom nil))
(def contacting-gmail (atom false))

(def board (arduino :firmata "/dev/ttyACM0"))

(defn check-mail []
  (reset! contacting-gmail true)
  (dosync
    (ref-set cnt (gmail/unread-count))
    (ref-set last-id (gmail/last-message-id)))
  (reset! contacting-gmail false))

(defn button-pressed []
  (digital-write board 12 LOW)
  (reset! last-id-pressed @last-id))

(defn button-down? []
  (= (digital-read board 3) 1))

(defn -main []
  (pin-mode board 12 OUTPUT)
  (pin-mode board 11 OUTPUT)
  (pin-mode board 3  INPUT)
  (enable-pin board :digital 3)

  (future
    (while true
      (check-mail)
      (Thread/sleep (* 10 60 1000))))

  (while true
    (when (button-down?)
      (Thread/sleep 300)
      (when (button-down?)
        (button-pressed)))

    (if (and (> @cnt 0) (not= @last-id @last-id-pressed))
      (digital-write board 12 HIGH)
      (digital-write board 12 LOW))

    (if @contacting-gmail
      (digital-write board 11 HIGH)
      (digital-write board 11 LOW)))
  (close board))
