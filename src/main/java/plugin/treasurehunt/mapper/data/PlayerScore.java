package plugin.treasurehunt.mapper.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * プレイヤーのスコア情報を扱うオブジェクト
 * DBに存在するテーブルと連動する。
 */

@Getter
@Setter
@NoArgsConstructor
public class PlayerScore {

    private int id;
    private String playerName;
    private int score;
    private double elapsedSec;
    private LocalDateTime registeredAt;


    public PlayerScore(String playerName, int score, double elapsedSec) {
        this.playerName = playerName;
        this.score = score;
        this.elapsedSec = elapsedSec;
    }
}
