
# Kotlin, Spring Boot, jOOQ, H2 Sample

KotlinとSpring Boot、jOOQを使った、書籍管理APIサーバーのサンプル。

## 制限事項

- H2 最新に脆弱性報告あり [（2023/06現在. v2.1.214）](https://mvnrepository.com/artifact/com.h2database/h2/2.1.214)
  - サンプルとして実装するため、一旦無視するものとする。

## 実装仕様

- 書籍には著者の属性があり、書籍と著者の情報をRDBに登録・変更・検索ができる。
- 著者に紐づく本を取得できる。
- APIのみ。※Viewはなし。

## How To Build & Run(War)

### Debug Run

```shell
$ ./gradlew flywayMigrate
$ ./gradlew bootRun
``` 

### Build

Flyway は実行時に動かない仕組みにしたので、デプロイ先で調整する必要あり。

```shell
$ ./gradlew build
```

