package rmq

import "time"

type Message struct {
	Date    time.Time
	Message string
	Uuid    string
}
