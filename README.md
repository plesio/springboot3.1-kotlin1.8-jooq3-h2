
# Kotlin, Spring Boot, jOOQ, MariaDB Sample

KotlinとSpring Boot、jOOQを使った、書籍管理APIサーバーのサンプル。

## 制限事項

- DB は MariaDB を Docker-Compose で立ち上げるものとする.
- DB は Flyway でマイグレーションするものとする.
  - マイグレーションファイルは `src/main/resources/db/migration` 以下に配置する.
  - また、Flyway が 新しい MariaDB に正式対応していないみたいなので、あまり突飛なコードを書くと動かないかも。

## 実装仕様

- 書籍には著者の属性があり、書籍と著者の情報をRDBに登録・変更・検索ができる。
- 著者に紐づく本を取得できる。
- APIのみ。※Viewはなし。

## How To Build & Run

- Java 17 or Later required.
- Docker/Docker-Compose required.

### Debug Run

```shell
$ mkdir db_data
$ docker-compose up -d
  >> これで mariadb が立ち上がる.
$ ./gradlew flywayMigrate
$ ./gradlew generateJooq
$ ./gradlew bootRun
```

### Test

- 制限事項
  - M1 Mac の場合 Docker Desktop を入れないとテストできない。
    - Rancher Desktop の Docker デフォルトだと TestContainers がパスを認識しないバグがある？っぽい。
      - 地味にめんどくさい。本当にめんどくさい。
  - 先にDebug Runを走らせる過程で、jooq を実行しておくこと。
    - 特に、Test向けに jooq の自動実行の設定をしていないため。

```shell
$ ./gradlew test
```

### Build

TBD..(というか本番デプロイの想定はしない.)

## development

### on IntelliJ IDEA

- プロジェクト設定とGradle設定のJavaを17以上に設定しないと初回からエラー出る。

## DB仕様について

* 参考資料
  * [OpenBD 書誌APIデータ仕様 (v1)](https://openbd.jp/spec/)
* サンプルデータ
  * ChatGPTに作らせてみた