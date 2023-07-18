package configs

import (
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
	"log"
)

var DB *gorm.DB

func ConnectToDB(config *Config) {
	var err error

	DB, err = gorm.Open(mysql.Open("root:root@tcp(127.0.0.1:3306)/"+config.DBName), &gorm.Config{})
	if err != nil {
		panic("Failed to connect to DB")
	} else {
		log.Println("Successfully connected to mysql")
	}
}
