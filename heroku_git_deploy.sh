# Deploy our application to heroku cloud platform

# Прикрепить данный git-репозиторий к существующему приложению heroku
heroku git:remote -a polyakov-a-social-network

# Задеплоить приложение (последний коммит) в heroku
git push heroku master

# Просмотр логов сервиса
heroku logs --tail