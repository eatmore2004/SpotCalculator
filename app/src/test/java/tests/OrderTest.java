package tests;

import com.andrii.positioncalculator.Helpers.Order;

import org.junit.Assert;
import org.junit.Test;

public class OrderTest {
    @Test
    public void testConstructor() {

        double price = 22300.1;
        double amount = 0.002;
        double nofeeamount = 0.001;

        Order order = new Order(price,amount,nofeeamount);
        Assert.assertEquals(price, order.getPrice(), 0.0);
        Assert.assertEquals(amount, order.getAmount(), 0.0);
        Assert.assertEquals(nofeeamount, order.getInitialAmount(), 0.0);
    }
    @Test
    public void testEquals() {
        Order order1 = new Order(22300.1,0.002,0.001);
        Order order2 = new Order(22300.10,0.0020,0.0010);

        Assert.assertTrue(order1.equals(order2));
    }
    @Test
    public void testNotEquals() {
        Order order1 = new Order(22300.1,0.002,0.001);
        Order order2 = new Order(22302.10,0.020,0.10);

        Assert.assertFalse(order1.equals(order2));
    }
    @Test
    public void TestGetInt(){
        Order order = new Order(1685,0.020,0.10);
        Assert.assertEquals(1700,order.getPriceInt());
    }
}
