package tests;

import com.andrii.positioncalculator.Position;
import com.andrii.positioncalculator.Utils;

import org.junit.Test;

public class UtilTest {
    @Test
    public void getStats(){
        Utils.getStats(new Position());
    }
}
