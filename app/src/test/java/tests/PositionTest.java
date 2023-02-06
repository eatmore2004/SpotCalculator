package tests;

import com.example.positioncalculator.Order;
import com.example.positioncalculator.Position;

import org.junit.Assert;
import org.junit.Test;

public class PositionTest {


    @Test
    public void decodeHash() {
        Position position = createPosition();
        String hash = position.getHash();
        Position new_position = new Position(hash);
        Assert.assertTrue(new_position.equals(position));
        position.addBuy(new Order(22300.4,0.002,0.001));
        Assert.assertFalse(position.equals(new_position));
    }

    private Position createPosition() {
        Position position = new Position();
        position.addBuy(new Order(22300.4,0.002,0.001));
        position.addBuy(new Order(22100,0.022,0.022));
        position.addSell(new Order(22300.4,0.002,0.001));
        position.addSell(new Order(22330.56,0.022,0.022));
        return position;
    }
}
