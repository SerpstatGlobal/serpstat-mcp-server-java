# Модульная архитектура Serpstat MCP Server для 40+ API методов

```
serpstat-mcp-server/
│
├── pom.xml                                 # Maven конфигурация
├── README.md                               # Документация проекта  
├── run.sh                                  # Скрипт запуска
├── test-server.py                          # Python скрипт для тестирования
├── claude_desktop_config.json              # Конфигурация для Claude Desktop
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/serpstat/mcp/
│   │   │       │
│   │   │       ├── SerpstatMcpServer.java         # Главный класс-оркестратор
│   │   │       │
│   │   │       ├── core/                          # Ядро системы
│   │   │       │   ├── SerpstatApiClient.java     # HTTP клиент с rate limiting
│   │   │       │   ├── ToolRegistry.java          # Автоматическая регистрация инструментов  
│   │   │       │   ├── ToolProvider.java          # Интерфейс провайдера инструментов
│   │   │       │   ├── BaseToolHandler.java       # Базовый класс обработчиков
│   │   │       │   ├── SerpstatApiResponse.java   # Модель ответа API
│   │   │       │   ├── SerpstatApiException.java  # Исключения API
│   │   │       │   ├── ValidationException.java   # Исключения валидации
│   │   │       │   └── RateLimiter.java           # Rate limiting
│   │   │       │
│   │   │       ├── domains/                       # Группировка по доменам API
│   │   │       │   │
│   │   │       │   ├── domain/                    # Анализ доменов (10-12 методов)
│   │   │       │   │   ├── DomainTools.java       # MCP инструменты для доменов
│   │   │       │   │   ├── DomainValidator.java   # Валидация параметров доменов  
│   │   │       │   │   ├── DomainSchemas.java     # JSON схемы для доменов
│   │   │       │   │   ├── DomainResponseFormatter.java # Форматирование ответов
│   │   │       │   │   └── models/                # Модели данных доменов
│   │   │       │   │       ├── DomainInfo.java
│   │   │       │   │       ├── DomainOverview.java
│   │   │       │   │       └── DomainStats.java
│   │   │       │   │
│   │   │       │   ├── keywords/                  # Исследование ключевых слов (8-10 методов)
│   │   │       │   │   ├── KeywordTools.java      # MCP инструменты для ключевых слов
│   │   │       │   │   ├── KeywordValidator.java  # Валидация параметров ключевых слов
│   │   │       │   │   ├── KeywordSchemas.java    # JSON схемы для ключевых слов
│   │   │       │   │   ├── KeywordResponseFormatter.java # Форматирование ответов
│   │   │       │   │   └── models/                # Модели данных ключевых слов
│   │   │       │   │       ├── KeywordInfo.java
│   │   │       │   │       ├── KeywordDifficulty.java
│   │   │       │   │       └── KeywordSuggestions.java
│   │   │       │   │
│   │   │       │   ├── competitors/               # Анализ конкурентов (6-8 методов)
│   │   │       │   │   ├── CompetitorTools.java   # MCP инструменты для конкурентов
│   │   │       │   │   ├── CompetitorValidator.java
│   │   │       │   │   ├── CompetitorSchemas.java
│   │   │       │   │   ├── CompetitorResponseFormatter.java
│   │   │       │   │   └── models/
│   │   │       │   │       ├── CompetitorAnalysis.java
│   │   │       │   │       └── CompetitorComparison.java
│   │   │       │   │
│   │   │       │   ├── backlinks/                 # Анализ бэклинков (5-7 методов)
│   │   │       │   │   ├── BacklinkTools.java
│   │   │       │   │   ├── BacklinkValidator.java
│   │   │       │   │   ├── BacklinkSchemas.java
│   │   │       │   │   ├── BacklinkResponseFormatter.java
│   │   │       │   │   └── models/
│   │   │       │   │       ├── BacklinkProfile.java
│   │   │       │   │       └── BacklinkAnalysis.java
│   │   │       │   │
│   │   │       │   ├── ranking/                   # Отслеживание позиций (4-6 методов)
│   │   │       │   │   ├── RankingTools.java
│   │   │       │   │   ├── RankingValidator.java
│   │   │       │   │   ├── RankingSchemas.java
│   │   │       │   │   ├── RankingResponseFormatter.java
│   │   │       │   │   └── models/
│   │   │       │   │       ├── RankingData.java
│   │   │       │   │       └── PositionHistory.java
│   │   │       │   │
│   │   │       │   └── reports/                   # Отчеты и экспорт (остальные методы)
│   │   │       │       ├── ReportTools.java
│   │   │       │       ├── ReportValidator.java
│   │   │       │       ├── ReportSchemas.java
│   │   │       │       ├── ReportResponseFormatter.java
│   │   │       │       └── models/
│   │   │       │           ├── Report.java
│   │   │       │           └── ExportData.java
│   │   │       │
│   │   │       └── utils/                         # Утилиты
│   │   │           ├── JsonSchemaUtils.java       # Работа с JSON схемами
│   │   │           ├── CacheUtils.java           # Утилиты кэширования
│   │   │           └── StringUtils.java          # Строковые утилиты
│   │   │
│   │   └── resources/
│   │       ├── logback.xml                       # Конфигурация логирования
│   │       ├── schemas/                          # JSON схемы для валидации
│   │       │   ├── domain/
│   │       │   │   ├── domains_info.json
│   │       │   │   └── domain_overview.json
│   │       │   ├── keywords/
│   │       │   │   ├── keyword_research.json
│   │       │   │   └── keyword_difficulty.json
│   │       │   └── ...
│   │       │
│   │       └── serpstat-methods.yaml             # Конфигурация всех методов API
│   │
│   └── test/
│       └── java/
│           └── com/serpstat/mcp/
│               ├── SerpstatMcpServerTest.java    # Основные тесты
│               ├── core/                         # Тесты ядра
│               │   ├── SerpstatApiClientTest.java
│               │   ├── ToolRegistryTest.java
│               │   └── RateLimiterTest.java
│               └── domains/                      # Тесты доменов
│                   ├── domain/
│                   │   └── DomainToolsTest.java
│                   ├── keywords/
│                   │   └── KeywordToolsTest.java
│                   └── ...
│
├── target/                                # Директория сборки Maven
│   ├── classes/
│   ├── test-classes/
│   ├── serpstat-mcp-server-1.0.0.jar
│   └── serpstat-mcp-server-1.0.0-shaded.jar  # Fat JAR для запуска
│
├── logs/                                  # Директория для логов
│   ├── serpstat-mcp-server.log           # Текущий лог файл
│   └── archived/                         # Архивные логи
│
└── .gitignore                             # Git ignore файл
```

## 🏗️ Ключевые принципы новой архитектуры:

### **1. Модульность по доменам**
- Каждый домен API (domain, keywords, competitors) = отдельный пакет
- Внутри каждого домена: Tools, Validator, Schemas, ResponseFormatter, Models
- Легко добавлять новые домены без изменения существующего кода

### **2. Автоматическая регистрация инструментов**
- `ToolRegistry` сканирует все `ToolProvider` классы
- Каждый домен реализует `ToolProvider` интерфейс
- Добавили новый домен → он автоматически подключается

### **3. Единая обработка ошибок и логирования**
- `BaseToolHandler` содержит общую логику для всех инструментов
- Стандартизированное логирование через MCP протокол
- Централизованная обработка исключений

### **4. Rate Limiting и кэширование**
- `SerpstatApiClient` управляет всеми HTTP запросами
- Встроенный rate limiter (защита от блокировки API)
- Возможность добавить кэширование частых запросов

### **5. Конфигурационный подход**
- JSON схемы вынесены в отдельные файлы
- YAML конфигурация для описания всех методов API
- Легко менять параметры без перекомпиляции

## 🚀 Преимущества для масштабирования до 40+ методов:

### **✅ Простота добавления новых методов:**
```java
// Шаг 1: Создать новый метод в существующем домене
private McpServerFeatures.SyncToolSpecification createNewMethodTool() {
    return new McpServerFeatures.SyncToolSpecification(
        new Tool("new_method", "Description", SCHEMA),
        this::handleNewMethod
    );
}

// Шаг 2: Добавить в getTools()
@Override
public List<McpServerFeatures.SyncToolSpecification> getTools() {
    return Arrays.asList(
        existingTool(),
        createNewMethodTool()  // <- просто добавить сюда
    );
}
```

### **✅ Простота добавления новых доменов:**
```java
// Шаг 1: Создать новый пакет domains/newdomain/
// Шаг 2: Реализовать NewDomainTools implements ToolProvider
// Шаг 3: Добавить в ToolRegistry.initializeProviders()
providers.add(new NewDomainTools(apiClient));
```

### **✅ Переиспользование кода:**
- Базовые классы избавляют от копипасты
- Общие утилиты для всех доменов
- Стандартизированные валидаторы и форматтеры

### **✅ Легкое тестирование:**
- Каждый домен тестируется независимо
- Моки для `SerpstatApiClient`
- Интеграционные тесты через `test-server.py`

## 📊 Масштабирование:
- **Текущее состояние**: 2-3 инструмента реализованы
- **Следующий этап**: Реализовать оставшиеся методы доменов по 5-7 штук за итерацию
- **Финальная цель**: 40+ инструментов с минимальным дублированием кода

Теперь добавлять новые методы API будет как конвейер! 🏭# Файловая структура проекта Serpstat MCP Server

```
serpstat-mcp-server/
│
├── pom.xml                                 # Maven конфигурация с MCP SDK 0.10.0
├── README.md                               # Документация проекта
├── run.sh                                  # Скрипт запуска (chmod +x)
├── test-server.py                          # Python скрипт для тестирования
├── claude_desktop_config.json              # Пример конфигурации для Claude Desktop
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── SerpstatMcpServer.java     # Основной класс сервера (с реальным MCP API)
│   │   │
│   │   └── resources/
│   │       └── logback.xml                # Конфигурация логирования
│   │
│   └── test/
│       └── java/
│           └── SerpstatMcpServerTest.java # Тесты (опционально)
│
├── target/                                # Директория сборки Maven (создается автоматически)
│   ├── classes/
│   ├── test-classes/
│   ├── serpstat-mcp-server-1.0.0.jar
│   └── serpstat-mcp-server-1.0.0-shaded.jar  # Fat JAR для запуска
│
├── logs/                                  # Директория для логов (создается автоматически)
│   ├── serpstat-mcp-server.log           # Текущий лог файл
│   └── serpstat-mcp-server.2025-06-04.1.gz  # Архивные логи
│
└── .gitignore                             # Git ignore файл (рекомендуется)
```

## Команды для создания структуры:

```bash
# Создание корневой директории
mkdir serpstat-mcp-server
cd serpstat-mcp-server

# Создание структуры Maven
mkdir -p src/main/java
mkdir -p src/main/resources
mkdir -p src/test/java
mkdir -p logs

# Создание файлов
touch pom.xml
touch README.md
touch run.sh
touch test-server.py
touch claude_desktop_config.json
touch src/main/java/SerpstatMcpServer.java
touch src/main/resources/logback.xml
touch src/test/java/SerpstatMcpServerTest.java
touch .gitignore

# Права на выполнение для скриптов
chmod +x run.sh
chmod +x test-server.py
```

## Ключевые изменения в новой версии:

### ✅ Исправлена зависимость MCP SDK:
```xml
<dependency>
    <groupId>io.modelcontextprotocol.sdk</groupId>
    <artifactId>mcp</artifactId>
    <version>0.10.0</version>
</dependency>
```

### ✅ Используется реальный API:
- `McpServer.sync()` для создания синхронного сервера
- `McpServerFeatures.SyncToolSpecification` для регистрации инструментов
- `StdioServerTransport` для STDIO транспорта
- `CallToolResult` для результатов
- Интеграция с MCP протоколом логирования

### ✅ Дополнительные файлы:
- **test-server.py** - Python скрипт для тестирования MCP сервера
- **claude_desktop_config.json** - пример конфигурации для Claude Desktop
- Обновленная документация с реальными примерами использования

## Быстрый старт:

```bash
# 1. Создать проект
mkdir serpstat-mcp-server && cd serpstat-mcp-server

# 2. Скопировать все файлы из артефактов

# 3. Сборка
mvn clean package

# 4. Запуск
./run.sh YOUR_SERPSTAT_API_TOKEN

# 5. Тестирование (в другом терминале)
pip install mcp
python test-server.py YOUR_SERPSTAT_API_TOKEN
```

## Интеграция с Claude Desktop:

1. Скопируйте `claude_desktop_config.json` в директорию конфигурации Claude
2. Замените пути и токен на реальные
3. Перезапустите Claude Desktop
4. Теперь можете использовать Serpstat API прямо в чате с Claude!