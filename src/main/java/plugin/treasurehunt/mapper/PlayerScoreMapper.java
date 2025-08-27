package plugin.treasurehunt.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import plugin.treasurehunt.mapper.data.PlayerScore;

import java.util.List;

public interface PlayerScoreMapper {

    @Select("select * from player_score")
    List<PlayerScore> selectList();

    @Insert("insert player_score(player_name, score, elapsed_sec, registered_at) " +
            "values (#{playerName}, #{score}, #{elapsedSec}, now())")
    int insert(PlayerScore playerScore);
}
