# КЛАДР

Библиотека для работы с [классификатором адресов России](http://www.gnivc.ru/inf_provision/classifiers_reference/kladr/) (КЛАДР).

## 1. Модули проекта
  
  * `kladr-api` - сущности получаемые из HTTP-сервиса КЛАДР посредством API.
  * `kaldr-clent` - реализация HTTP-клиента для работы с сервисом КЛАДР
  * `kladr-core` - базовая функциональность для работы с КЛАДР
  * `kladr-search` - модуль строящий и читающий полнотекстовый индекс
  * `kladr-service` - HTTP-сервис, позволяющий посредством API делать запросы в КЛАДР

## 2. Как использовать

### 2.1. Использование основной библиотеки

Добавьте в ваш проект зависимость:

```xml
<dependency>
    <groupId>org.jamel.kladr</groupId>
    <artifactId>kladr-core</artifactId>
    <version>${kladr-version}</version>
</dependency>
```

, где `kladr-version` - номер последней стабильной версии библиотеки (например, `0.0.1`). Узнать какая версия является последней можно [здесь](https://github.com/jamel/kladr/releases).

После чего можно будет, например, вывести список всех регионов России:

```java
import java.io.File;

import org.jamel.kladr.KladrReader;
import org.jamel.kladr.data.Region;
import org.jamel.kladr.processors.KladrObjectsProcessor;

public class RegionsPrinter {

    public static void main(String[] args) {
        KladrReader reader = new KladrReader(new File("/tmp/kladr"));
        reader.readKladrTable(new KladrObjectsProcessor() {
            @Override
            public void processRegion(byte regionCode, Region region) {
                System.out.println(regionCode + "  " + region.getName());
            }
        });
    }
}
```

Выведет:
```
1  Адыгея
2  Башкортостан
3  Бурятия
...
87  Чукотский
89  Ямало-Ненецкий
99  Байконур
```

Или пример посложнее. Допустим нам нужно вывести полные адреса (до улицы) в городе Бердск (КЛАДР код 54000002):

```java
import java.io.File;

import org.jamel.kladr.AddressBuilder;
import org.jamel.kladr.KladrReader;
import org.jamel.kladr.cache.KladrCache;
import org.jamel.kladr.data.Street;
import org.jamel.kladr.processors.StreetProcessor;

public class BerdskAddressesPrinter {

    private static final long BERDSK_CODE = 54_000_002;

    public static void main(String[] args) {
        KladrReader reader = new KladrReader(new File("/tmp/kladr"));

        // read table kladr.dbf into cache and create address builder
        KladrCache cache = new KladrCache();
        reader.readKladrTableTo(cache);
        final AddressBuilder builder = new AddressBuilder(cache);

        // process each street
        reader.readStreetTable(new StreetProcessor() {
            @Override
            public void processStreet(long streetCode, Street street) {
                if (street.getCityCode() == BERDSK_CODE) {
                    System.out.println(builder.buildFor(street));
                }
            }
        });
    }
}

```

Выведет:
```
обл. Новосибирская, г. Бердск, ул. 30 лет Победы
обл. Новосибирская, г. Бердск, ул. 40 лет Октября
обл. Новосибирская, г. Бердск, пер. Банковский
...
обл. Новосибирская, г. Бердск, ул. Взлетная
обл. Новосибирская, г. Бердск, пер. Дивный
обл. Новосибирская, г. Бердск, пер. Добрый
```

### 2.2 HTTP-сервис

Сервис КЛАДР удобно использовать в том случае если вы хотите разместить на своем сайте форму для ввода адреса. Сервис предоставляет JSON API к которому легко привязать любой плагин для автокомплита (в своем примере я использовал [Typeahead](http://twitter.github.io/typeahead.js/) от Twitter). В действии можно посмотреть [здесь](http://indexes.polovko.me/kladr.html).

![alt text](http://img-fotki.yandex.ru/get/9153/182567699.0/0_bcad5_8370d82b_orig "Пример использования сервиса")

#### 2.2.1 Сборка

Для того чтобы получить работающий бинарник нужно собрать "толстый" jar, включающий все зависимости:

TODO: дописать как это сделать

#### 2.2.2 Создание индекса

TODO: дописать как это сделать

#### 2.2.3 Запуск сервиса

TODO: дописать как это сделать

#### 2.2.4 Пример конфигурации nginx

TODO: дописать как это сделать

### 2.3 Взаимодействие с сервисом по API

TODO: написать http-клиент



