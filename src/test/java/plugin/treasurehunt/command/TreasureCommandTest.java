package plugin.treasurehunt.command;

import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TreasureCommandTest {

    @Test
    void testIsNight() {
        //World mockWorld = mock(World.class);
        World world = mock(World.class);

        when(world.getTime()).thenReturn(14000L); // 夜の時間
        assertTrue(TreasureCommand.isNight(world));

        when(world.getTime()).thenReturn(6000L); // 昼の時間
        assertFalse(TreasureCommand.isNight(world));
    }

}