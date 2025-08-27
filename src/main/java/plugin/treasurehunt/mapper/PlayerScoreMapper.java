package plugin.treasurehunt.mapper;

import org.apache.ibatis.annotations.Select;
import plugin.treasurehunt.mapper.data.PlayerScore;

import java.util.List;

public interface PlayerScoreMapper {

    @Select("select * from player_score")
    List<PlayerScore> selectList();
}
