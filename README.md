# ping-pong
Тестовое задания для Glu Mobile.
<p>1) Для сборки необходим maven, mvn compile и запустить из командной строки Initializer класс в папке с скомпилированными сырцами.
Или же открыть в IDE сбилдить mavenом и запустить main class в Initializer.java.
База данных Монго, поднимается как Embedded контейнер(решения взяла тут https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo)</p>
2) Не использовала никакой DI фреймворки, не использовала servlet api и контейнер сервлетов, сервер самописный.
<p>3) Для тестирования необходимо послать get запрос на урл: localhost:8080/ping/#id 
Пример: curl -i -XGET localhost:8080/ping/1. Любой другой урл и метод в ответ получит 404 resource not Found. Добавить команды можно в 
ru.varlamova.controller.commands.PingController(примеры закомментированы). Сервер в текущей реализации не умеет парсить тела запросов.</p>
