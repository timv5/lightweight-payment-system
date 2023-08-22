package request

import "time"

type WebhookRequest struct {
	ID      int       `json:"id"`
	Created time.Time `json:"created"`
	Type    string    `json:"type"`
}
