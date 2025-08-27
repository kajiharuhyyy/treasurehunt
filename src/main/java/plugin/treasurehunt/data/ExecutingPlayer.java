package plugin.treasurehunt.data;

import lombok.Getter;
import lombok.Setter;

/**
 * TreasureHuntのゲームを実行する際のプレイヤー情報を扱うオブジェクト。
 * プレイヤー名、合計点数、日時などの情報を持つ。
 */
@Getter
@Setter
public class ExecutingPlayer {

    private String playerName;
    private int Score;
    private Long startCountTime;
    private int gameTime;

    public ExecutingPlayer(String playerName) {
        this.playerName = playerName;
    }
}
