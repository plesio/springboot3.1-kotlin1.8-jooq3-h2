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

### 大まかな方針

* 書籍は、ISBNでも特定できるものとする。
    * ただし、ISBNがない場合は、タイトルや著者名で特定できるものとする。
    * また、ISBNが重複することはISBN自体の仕様上でありうるため、合致する対象が複数あってもNGとしない。
* 書籍には必ず著者がいるものとする。
    * その上で、次のケースには対応しないものとする
        * 著者は、複数人いる場合。⇒代表者1名にするか、連名状態を1著者と表現するか、組織名にするか。
            * DB仕様上、著者を複数人登録できる仕様にはしているが、実装上は非対応のままとする。
        * 著者が不明な場合。⇒著者名を「不明」とする著者を作成するか、DB登録自体を仕様NGとするか。
            * これに関しては「不明」属性を取り扱い始めると、DB定義を変える必要があるため、今回は考慮外とする。
* 名字・名前の区別は付けず「著者名」として登録する。だいたい100文字程度をMAXで想定
    * 一応外国人対策（ミドルネームやめっちゃ長いフルネームまで対応してられない）
* スキーマに合致する一致条件すべての検索実装は「しない」。
    * 例えば、出版年で検索するとかは未実装。
    * 理由：他の検索条件を実装すれば、実装事例としては充分満たせると判断したため。

## テスト実装について

今回は、REST APIの疎通・正常系テストと、レコード数が1000，10000の実行秒数チェックのみを実装する。

実行秒数は明確なエラー判定は用意しないが、ある程度の目安としてはログに書き出すようにする。

* 次のテストは今回書かない
    * DB定義上のVARCHARやINTなどの最大値最小値判定、NULL判定
    * PK重複判定
    * etc.. 他に思いつくであろう一般的な単体テスト。