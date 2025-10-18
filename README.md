# PatrickChat
Чат созданный усилиями группы

Специальная версия ко Дню святого Патрика

## Запуск

### Запуск сервера
```bash
mvn exec:java -Dexec.mainClass="patrick.StartServer"
```

### Запуск клиента
Клиенты могут запускаться как отдельные процессы и подключаться к серверу по сети.

```bash
mvn exec:java -Dexec.mainClass="patrick.StartClient" -Dexec.args="<server_host> <nickname>"
```

Параметры:
- `<server_host>` (необязательный) - адрес сервера (по умолчанию: localhost)
- `<nickname>` (необязательный) - имя пользователя (если не указан, будет запрошен при запуске)

Примеры:
```bash
# Подключение к локальному серверу с никнеймом User1
mvn exec:java -Dexec.mainClass="patrick.StartClient" -Dexec.args="localhost User1"

# Подключение к удаленному серверу
mvn exec:java -Dexec.mainClass="patrick.StartClient" -Dexec.args="192.168.1.10 Alice"

# Подключение к локальному серверу (никнейм будет запрошен)
mvn exec:java -Dexec.mainClass="patrick.StartClient" -Dexec.args="localhost"

# Использование параметров по умолчанию
mvn exec:java -Dexec.mainClass="patrick.StartClient"
```
