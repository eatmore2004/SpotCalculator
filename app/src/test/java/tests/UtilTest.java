package tests;

import com.andrii.positioncalculator.Helpers.Position;
import com.andrii.positioncalculator.Helpers.Utils;

import org.junit.Test;

public class UtilTest {
    @Test
    public void getStats(){
        Utils.getStatsByPosition(new Position());
    }
}
