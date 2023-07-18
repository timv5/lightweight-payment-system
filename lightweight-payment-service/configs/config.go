package configs

import "github.com/spf13/viper"

type Config struct {
	// database configs
	DBHost         string `mapstructure:"MYSQL_HOST"`
	DBUsername     string `mapstructure:"MYSQL_USER"`
	DBUserPassword string `mapstructure:"MYSQL_PASSWORD"`
	DBName         string `mapstructure:"MYSQL_NAME"`
	DBPort         string `mapstructure:"MYSQL_PORT"`

	// rmq configs
	RMQUrl         string `mapstructure:"RMQ_URL"`
	RMQQueueName   string `mapstructure:"RMQ_QUEUE_NAME"`
	RMQExchangeKey string `mapstructure:"RMQ_EXCHANGE_KEY"`

	// stripe configs
	StripeSecretKey string `mapstructure:"STRIPE_KEY"`
}

func LoadConfig(path string) (config Config, err error) {
	viper.AddConfigPath(path)
	viper.SetConfigType("env")
	viper.SetConfigName("app")

	viper.AutomaticEnv()

	err = viper.ReadInConfig()
	if err != nil {
		return
	}

	err = viper.Unmarshal(&config)
	return
}
