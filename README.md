# Log Analytics & Discord Bot

Проект представляет собой систему мониторинга и аналитики логов на примере Minecraft-сервера, состоящую из двух микросервисов с использованием современных технологий.

## 🚀 Технологический стек

**Backend:**
- Java 17, Spring Boot, Spring AMQP (RabbitMQ)
- Hibernate, PostgreSQL, JPA
- Docker, Docker Compose

## 📋 Описание проекта

Система состоит из двух независимых микросервисов:

### 1. Log Collector Service
- Мониторит лог-файлы на примере Minecraft-сервера в реальном времени
- Фильтрует только информационные логи (`INFO` уровень)
- Отправляет данные через RabbitMQ для гарантированной доставки
- Обеспечивает отказоустойчивость и надежность передачи данных

### 2. Log Processor & Discord Bot Service
- Принимает и обрабатывает логи из RabbitMQ
- Сохраняет данные в PostgreSQL
- Запускает Discord-бота с функциями аналитики

## 🎮 Функциональность Discord Bot

### Команды бота:

**`!online`** - Показывает текущий онлайн игроков на сервере

**`!achievements`** - Выводит топ-5 игроков по количеству выполненных достижений

**Дополнительные возможности:**
- Аналитика игровой статистики в реальном времени
- Мониторинг активности игроков
- Исторические данные по достижениям

## 🛠 Установка и запуск

### Требования:
- Docker
- Docker Compose

### 🚀 Запуск проекта

```bash
git clone <repository-url>
cd <project-directory>
```

#### 1. Настройка Log Tailer Service
Создайте файл `.env` в директории `log-tailer-service`:

```env
SERVER-LOG-PATH=/path/to/your/minecraft/logs
RABBITMQ-HOST=rabbitmq-host
```

#### 2. Настройка Discord Bot Service
Отредактируйте файл конфигурации `discord-bot-spring-service/src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.username=<имя пользователя БД>
spring.datasource.password=<пароль пользователя БД>

# RabbitMQ Configuration
spring.rabbitmq.username=<имя пользователя RabbitMQ>
spring.rabbitmq.password=<пароль пользователя RabbitMQ>
spring.rabbitmq.queue-name=server-logs-queue

# Discord Bot Configuration
discordbot.token=<токен Discord-бота>
В log-tailer-service требуется создать .env файл с путём до логов SERVER-LOG-PATH и хостом RABBITMQ-HOST.
В discord-bot-spring-service/src/main/resources/application.properties требуется добавить поля:
spring.datasource.username=<имя пользователя БД>
spring.datasource.password=<пароль пользователя БД>
spring.rabbitmq.username=<имя пользователя RabbitMQ Management>
spring.rabbitmq.password=<пароль пользователя RabbitMQ Management>
spring.rabbitmq.queue-name=server-logs-queue
discordbot.token=<токен Discord-бота>
```

#### 3. Запуск Docker compose

```bash
docker-compose up -d
```
