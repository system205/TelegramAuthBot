# Prerequisites
1) Telegram bot token in env 
2) Postgres database 'telegram'
3) Specify web-client.base-url of your other backend server with auth controller
4) Run kafka and enable telegram.kafka.enabled
5) You should specify password, user and database in env. The same if you run in docker

# Workflow:
Kafka is used to apply TelegramUserUpdates:
1. If a user is new and saved the update has old user and a new one to be equal.
2. If a user is old and bot noticed some changes in user info the update has old and new TelegramUser to be different in some attributes. 

- Bot checks user changes (username, for example) periodically and before each external call (for example, /get_password, not implemented yet) to send only the relevant information about the user.

# Features

1. On /get_password [GetPasswordMessageProcessor](src/main/java/com/system205/telegram/message/GetPasswordMessageProcessor.java) does the following:
   - Sends POST request to `/api/telegram/auth` of specified auth server.
   - Passes [TelegramUser](src/main/java/com/system205/entity/TelegramUser.java) in body.
   - Awaits for OK status and returns the password
   - Otherwise, outputs negatively (can't register, for example)