# Фургон кави

Консольний застосунок на Java, який завантажує фургон певного об'єму кавою на
певну суму, сортує вантаж за співвідношенням ціни та ваги й шукає товари
із заданим діапазоном параметрів якості.

## Завдання

Завантажити фургон певного об'єму вантажем на певну суму з різних сортів кави,
які знаходяться в різних фізичних станах (зерно, мелене, розчинне в банках
і пакетиках). Урахувати об'єм кави разом з упаковкою. Провести сортування
товарів на основі співвідношення ціни та ваги. Знайти товар у фургоні, що
відповідає заданому діапазону параметрів якості.

## Як це працює

1. Параметри фургона (об'єм, бюджет) і діапазон якості для пошуку зчитуються
   з [`van.properties`](src/main/resources/van.properties).
2. Каталог товарів зчитується з
   [`coffee-catalog.csv`](src/main/resources/coffee-catalog.csv).
3. `VanLoader` завантажує товари в порядку каталогу; товар, що не вміщується
   за об'ємом (з урахуванням упаковки) чи бюджетом, пропускається.
4. Вантаж сортується за ціною кілограма кави (від найдешевшої).
5. У фургоні шукаються товари, у яких аромат, кислотність і тіло
   потрапляють у задані межі.

Об'єм товару рахується так:
`вага / насипна густина стану кави × коефіцієнт упаковки`.
Густина залежить від фізичного стану: для зерен — від обсмаження, для меленої
кави — від помелу, для розчинної — від технології виробництва.

## Структура

```
ua.lab.coffeevan
├── CoffeeVanApplication      точка входу, сценарій роботи програми
├── model                     предметна область
│   ├── Coffee                абстрактний кавовий товар
│   │   ├── CoffeeBeans       кава у зернах (RoastLevel)
│   │   ├── GroundCoffee      мелена кава (GrindSize)
│   │   └── InstantCoffee     розчинна кава в банках і пакетиках (InstantProcess)
│   ├── CoffeeVariety         сорт: арабіка, робуста, ліберика, ексцельза
│   ├── PackagingType         упаковка та її коефіцієнт об'єму
│   ├── Quality               параметри якості (аромат, кислотність, тіло)
│   └── QualityRange          діапазон параметрів якості
├── van
│   ├── CoffeeVan             фургон: завантаження, сортування, пошук
│   └── VanLoader             заповнення фургона товарами з каталогу
├── config
│   ├── VanConfig             параметри ініціалізації
│   └── VanConfigReader       читання параметрів з .properties
├── io
│   ├── CoffeeCatalogReader   читання каталогу з CSV
│   └── DataSources           відкриття файлів і вбудованих ресурсів (UTF-8)
├── report
│   └── CargoReportPrinter    виведення таблиць у консоль
└── exception
    ├── CargoLoadingException товар не вміщується у фургон
    └── InvalidDataException  некоректні дані у файлах
```

**ООП у проєкті:**

- **Успадкування** — `CoffeeBeans`, `GroundCoffee`, `InstantCoffee` розширюють
  абстрактний `Coffee`: це різні фізичні стани одного товару.
- **Поліморфізм** — `getBulkDensity()`, `getPhysicalState()`,
  `getStateDetails()` перевизначаються в підкласах; `CoffeeVan` працює з будь-якою
  кавою через тип `Coffee`.
- **Інкапсуляція** — усі поля `private final`, значення перевіряються
  в конструкторах, вантаж фургона доступний лише для читання.
- **Композиція** — упаковка й якість є окремими типами, а не підкласами.

## Запуск

### IntelliJ IDEA

1. Відкрити теку `LAB_OOP`. Якщо IDEA запропонує *Load Maven Project* —
   погодитися (або ПКМ по `pom.xml` → *Add as Maven Project*).
2. Вказати JDK 17 або новішу: *File → Project Structure → Project → SDK*.
3. Запустити `CoffeeVanApplication` (зелена стрілка біля методу `main`).
4. Тести: ПКМ по `coffee-van/src/test/java` → *Run 'All Tests'*
   (або *Run with Coverage*).

### Maven

У IDEA: вкладка *Maven* → *LAB_OOP* → *Lifecycle* → `verify`. З терміналу:

```bash
mvn verify
```

```bash
java -jar coffee-van/target/coffee-van-1.0.jar
```

Можна передати власні файли: `java -jar coffee-van-1.0.jar van.properties catalog.csv`.

Звіт про покриття тестами — `coffee-van/target/site/jacoco/index.html`.
Збірка завершується помилкою, якщо тести покривають менше 95% рядків.

## Формат файлів

`van.properties`:

```properties
van.capacity.liters=18
van.budget=5600
search.aroma.min=7
search.aroma.max=10
search.acidity.min=5
search.acidity.max=9
search.body.min=5
search.body.max=8
```

`coffee-catalog.csv` — один товар у рядку, поля через `;`:

```
стан;назва;сорт;вага_г;ціна_грн;упаковка;аромат;кислотність;тіло;особливість
BEANS;Colombia Supremo;ARABICA;1000;1150.00;VACUUM_PACK;8;6;7;MEDIUM
GROUND;Espresso Italiano;ARABICA;250;330.00;TIN_CAN;8;5;8;FINE
INSTANT;Crema Sticks;ARABICA;50;95.00;SACHET;5;3;4;AGGLOMERATED
```

| Стан      | Упаковки                                | Особливість                                 |
|-----------|-----------------------------------------|---------------------------------------------|
| `BEANS`   | `PAPER_BAG`, `VACUUM_PACK`              | `LIGHT`, `MEDIUM`, `DARK`                   |
| `GROUND`  | `PAPER_BAG`, `VACUUM_PACK`, `TIN_CAN`   | `FINE`, `MEDIUM`, `COARSE`                  |
| `INSTANT` | `GLASS_JAR`, `TIN_CAN`, `SACHET`        | `FREEZE_DRIED`, `AGGLOMERATED`, `SPRAY_DRIED` |

## Технології

Java 17, Maven, JUnit 5, Mockito, JaCoCo.
