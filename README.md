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
- APIのみ。UIはなし。

### その他事項

- 登録・変更・検索(一部) を REST API として実装する。
- 削除 の REST APIは用意しない（テストコードの関係で直接呼び出すことはできるようにする）
- ログインなど認証機能はなし。
- テストは、最低限、疎通・正常系を実装する程度に抑える（後述）
- 依存ライブラリ・アーキ構成で表題以外のライブラリについて、採用理由にこだわりはない。
    - 「●●をするために▲▲か■■が必要で、世の中のトレンド的に▲▲の方が情報量が多そうだったりアクティブに開発してあったりしたので▲▲を採用した」程度。
        - e.g. ) Kotest + Test Containers は世の中のIT系記事で紹介例が多かったので使用した。

## How To Build & Run

- Java 17 or Later required.
    - Mac or Linux(Homebrew) > brew install openjdk@17
    - Ubuntu > sudo apt install openjdk-17-jdk
- Docker/Docker-Compose required.

作者が動作確認したマシン環境

- MacbookPro(M1 Pro) / V12(Monterey)
- Linux Mint 21 on Windows 11 (use VMWare)

### Debug Run

```shell
# $ mkdir db_data
# ↑ データ永続化をする場合 docker-compose.yaml 側の volumes 設定を書き換えること 
$ docker-compose up -d  ( または > docker compose up -d )
  >> これで mariadb が立ち上がる.
$ ./gradlew flywayMigrate
$ ./gradlew generateJooq
$ ./gradlew bootRun
```

docker-compose か docker compose かは、環境とDockerの入れ方次第。

### Test

Debug Run で立ち上げた DBコンテナは > docker-compose down で一応落としておく。

```shell
（bootRun が動いたところまで確認できているうえで）
$ ./gradlew test
```

- 制限事項
    - 先にDebug Runを走らせる過程で、jooq を実行しておくこと。
        - 特に、Test向けに jooq の自動実行の設定をしていないため。
    - M1 Mac の場合 Docker Desktop を入れる必要がありそう。
        - Rancher Desktop の Docker デフォルトだと TestContainers がパスを認識しないことがあるっぽい？
            - 一説には「docker.sockのシンボリックリンクを張ればRancher Desktopでも動く」らしい（試してない）
                - 一応コマンド＞ $ sudo ln -s $HOME/.docker/run/docker.sock /var/run/docker.sock
    - Linux環境などで、Dockerインストールした直後に TestContainers 使おうとすると実行パスを認識しない。
        - 素直に再起動や再ログイン、または source などして実行パスをリフレッシュすれば解決する。

### Build

TBD..(習作のため本番デプロイの想定はしない.)

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
    * 一応外国人対策（ミドルネームやめっちゃ長いフルネームまで対応するとちょっと大変そう）
* スキーマに合致する一致条件すべての検索実装は「しない」。
    * 例えば、出版年で検索するとかは未実装。
    * 理由：他の検索条件を実装すれば、実装事例としては充分満たせると判断したため。

## テスト実装について

今回は、REST APIの疎通・正常系テストと、レコード数が1000，10000の実行秒数チェックのみを実装する。

実行秒数は明確なエラー判定は用意しないが、ある程度の目安としてはログに書き出すようにする。

* 次のテストは今回書かない
    * DB定義上のVARCHARやINTなどの最大値最小値判定、NULL判定
    * PK重複判定, 変数上限値判定 etc.. 他に思いつくであろう一般的な単体テスト。

### GitHub Actions

トリガーは main にマージしたりしたとき。

### GitHub Actions on Local on Mac

act を使用することで実現ができるそうだが、割愛。  
(GitHub Actions で動かすインスタンスが Ubuntu22.04 で、それようの act インスタンスがないとかなんとかのため)
