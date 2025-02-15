import org.example.entity.BillingEntity;
import org.example.enums.BillingState;
import org.example.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest {

    private PaymentService service;

    @BeforeEach
    public void setUp() {
        // starting balance of $100
        service = new PaymentService(100);
    }

    @Test
    public void testCashInPositiveAmount() {
        service.cashIn(50);
       assertEquals(150, service.getBalance(), "Balance should be updated after a valid deposit.");
    }

    @Test
    public void testCashInNegativeAmount() {
        service.cashIn(-20);
        assertEquals(100, service.getBalance());
    }

    @Test
    public void testMakePaymentValidAmount() {
        BillingEntity entity = new BillingEntity();
        entity.setAmount(30);
        boolean paymentResult = service.makePayment(entity);
        assertTrue(paymentResult, "Payment should be successful.");
        assertEquals(70, service.getBalance(), "Balance should be updated after payment.");
    }

    @Test
    public void testMakePaymentInsufficientBalance() {
        BillingEntity entity = new BillingEntity();
        entity.setAmount(200);
        entity.setState(BillingState.NOT_PAID);
        boolean paymentResult = service.makePayment(entity);
        assertFalse(paymentResult, "Sorry! Not enough fund to proceed with payment.");
        assertEquals(100, service.getBalance(), "Balance should remain unchanged after failed payment.");
    }

    @Test
    public void testMakePaymentNegativeAmount() {
        BillingEntity entity = new BillingEntity();
        entity.setAmount(-50);
        entity.setState(BillingState.NOT_PAID);
        boolean paymentResult = service.makePayment(entity);
        assertFalse(paymentResult, "Payment should fail for negative amounts.");
        assertEquals(100, service.getBalance(), "Balance should remain unchanged after failed payment.");
    }

    @Test
    public void testMakePaymentWithPaidBilling() {
        BillingEntity entity = new BillingEntity();
        entity.setAmount(-50);
        entity.setState(BillingState.PAID);
        boolean paymentResult = service.makePayment(entity);
        assertFalse(paymentResult, "Payment should fail for negative amounts.");
        assertEquals(100, service.getBalance(), "Balance should remain unchanged after failed payment.");
    }

    @Test
    void testPaymentProcessing() {
        service.cashIn(100);
        BillingEntity entity = new BillingEntity();
        entity.setAmount(10);
        service.makePayment(entity);
        assertEquals(190, service.getBalance(), "Payment has been completed for Bill with id ");
    }
}

