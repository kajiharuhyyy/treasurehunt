#  🏆宝探しゲーム

昼は動物、夜はモンスターを倒してドロップアイテムを探す、Minecraftで行う宝探しプラグインです。  
制限時間内にお題アイテムを拾うとスコアを獲得し、データベースに保存されます。

---

## 🎥 デモ動画

![Demo](./treasure.gif)


---

## 📌 機能

- `/treasure` : ゲームを開始
    - 昼なら動物系ドロップ（羽・革・肉など）
    - 夜ならモンスター系ドロップ（骨・糸・エンダーパールなど）
    - 制限時間内に拾えばスコア加算（時間ボーナスあり）
- `/treasure list` : これまでのスコア履歴を表示
- MySQL にスコアを保存（MyBatis 利用）

---

## 📌️ 技術スタック

- **Java 17**
- **Spigot API (Minecraft 1.20.x)**
- **MyBatis + MySQL 8.0**
- **Gradle**

---

## 📌 工夫した点 / 学んだこと

- 昼夜のワールド時間を判定し、対象アイテムプールを切り替え
- BukkitScheduler で 1分ごとに経過を通知 & 制限時間を制御
- MyBatis を利用し、SQL直書きせずに Mapper でスコアを登録
- タイマーの多重起動を防ぐ処理を実装
- プレイヤー名・スコア・経過時間をDBに保存し、一覧表示機能を追加

---

## 📌 インストール方法

1. `./gradlew build` で jar をビルド
2. `plugins/` フォルダに配置
3. サーバーを再起動
4. MySQL に以下のDDLを流す

```sql
CREATE TABLE player_score (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(100),
    score INT,
    elapsed_sec DOUBLE,
    registered_at DATETIME
);

```

---

## 📌 今後の拡張予定 (TODO)

- 難易度設定（昼限定 / 夜限定 / ランダム）
- カウントダウン機能の実装

---

## 📌 作者

Kajino Haruhisa

- [GitHub](https://github.com/kajiharuhyyy/treasurehunt.git)