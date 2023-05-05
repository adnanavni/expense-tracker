# Expense Tracker

## Käyttöönotto ohjeet

Voit aloittaa projektin kloonaamalla tämän repositorion ja rakentamalla sen IntelliJ:llä. Java version pitää olla 16 tai
uudempi.
Sinun tulee tehdä .evn tiedosto kansioon ```src/main/java/fi/metropolia/expensetracker/datasource.``` .env tiedostossa
tulee
olla seuraavat muuttujat:

```dotenv
DB_name=expensetracker
DB_username=jokuKayttajanimi
DB_password=jokuSalasana
```

Voit luoda oman tietokannan käyttäen databaseSetup.sql tiedostoa.

Kun kaikki on valmista, voit käynnistää sovelluksen ajamalla MainApplication.java tiedoston.

HUOM: Metropolian VPN-yhteys vaaditaan educloud-tietokantaan yhdistämiseen, mutta voit myös käyttää omaa tietokantaa tietokoneellasi.

## Teknologiat
* Java
* JavaFX
* IntelliJ IDEA
* SceneBuilder
* Maven
* JUnit 5
* MariaDB (tietokanta)