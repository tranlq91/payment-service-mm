package org.example;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.example.entity.BillingEntity;
import org.example.entity.PaymentEntity;
import org.example.enums.BillingState;
import org.example.enums.PaymentState;
import org.example.service.PaymentService;
import org.example.utils.DateTimeUtils;
import org.example.utils.JsonFileHandler;

public class PaymentApp {
    private static final HashMap<Integer, BillingEntity> billingStore = new HashMap<>();
    private static final HashMap<Integer, PaymentEntity> paymentStore = new HashMap<>();
    public static PaymentService paymentService = new PaymentService();
    public static int billingIndex = 0;
    public static int paymentIndex = 0;

    private static final String BILLING_FILE = "billing.json";
    private static final String PAYMENT_FILE = "payment.json";
    private static final String BALANCE_FILE = "balance.txt";


    public static void main(String[] args) {
        setupData();
        System.out.println("======== args ====== ");
        Arrays.stream(args).toList().forEach(System.out::println);
        try {
            String inChoice = args[0];
            switch (inChoice) {
                case "CASH_IN" -> {
                    int amount = Integer.parseInt(args[1]);
                    cashInMoney(amount);
                }
                case "LIST_BILL" -> listBill(billingStore.values().stream().toList());
                case "PAY" -> makePayment(Arrays.stream(Arrays.copyOfRange(args, 1, args.length)).toList());
                case "CHECK_BALANCE" -> checkBalance();
                case "LIST_PAYMENT" -> listPayment();
                case "SCHEDULE" -> updateDueDate(Integer.parseInt(args[1]), args[2]);
                case "SEARCH_BILL_BY_PROVIDER" -> searchBillByProvider(args[1]);
                case "CREATE_BILL" -> makeBill(args[1], Integer.parseInt(args[2]), args[3], args[4]);
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Application has been stop cause as " + e.getMessage());
        }

        saveDataBeforeCloseApp();
    }

    private static void setupData() {
        // Setup billings
        List<BillingEntity> billingEntities = JsonFileHandler.readBillings(BILLING_FILE);
        billingEntities.forEach(e -> billingStore.put(e.getNo(), e));

        billingIndex = billingStore.keySet().stream().mapToInt(i -> i).max().getAsInt() + 1;

        // Setup payments
             List<PaymentEntity> paymentEntities = JsonFileHandler.readPayment(PAYMENT_FILE);
        paymentEntities.forEach(e -> paymentStore.put(e.getNo(), e));

        paymentIndex = paymentStore.keySet().stream().mapToInt(i -> i).max().getAsInt() + 1;

        // Setup balance
        int balance = JsonFileHandler.readBalance(BALANCE_FILE);

        paymentService.setBalance(balance);
    }

    private static void saveDataBeforeCloseApp() {
        JsonFileHandler.writeBilling(billingStore.values().stream().toList(), BILLING_FILE);
        JsonFileHandler.writePayment(paymentStore.values().stream().toList(), PAYMENT_FILE);
        JsonFileHandler.writeBalance(paymentService.getBalance(), BALANCE_FILE);
    }


    private static void searchBillByProvider(String provider) {
        List<BillingEntity> billings = billingStore.values().stream().filter(bill -> bill.getProvider().equals(provider)).toList();
        listBill(billings);
    }

    private static void updateDueDate(int billId, String newDueDate) {
        BillingEntity billingEntity = billingStore.get(billId);
        if (billingEntity == null) {
            System.out.println("Sorry! Not found a bill with such id");
            return;
        }

        if (billingEntity.getState().equals(BillingState.PAID)) {
            System.out.println("Sorry! This bill has been paid, not need to change due date");
            return;
        }
        billingEntity.setDueDate(newDueDate);
        billingStore.put(billId, billingEntity);
    }

    private static void makeBill(String type, int amount, String provider, String dueDate) {
        BillingEntity billingEntity = new BillingEntity();
        billingEntity.setNo(++billingIndex);
        billingEntity.setType(type);
        billingEntity.setAmount(amount);
        billingEntity.setProvider(provider);
        billingEntity.setDueDate(dueDate);
        billingEntity.setState(BillingState.NOT_PAID);

        billingStore.put(billingIndex, billingEntity);
    }

    private static void listBill(List<BillingEntity> billings) {

        System.out.printf("--------------------------------%n");
        System.out.printf(" (print billing table )         %n");
        System.out.printf("--------------------------------%n");
        System.out.printf("|%-10s | %-20s | %12s | %12s | %10s | %12s |%n", "Bill No.", "Type", "Amount", "Due Date", "State", "PROVIDER");
        billings.forEach(billing -> System.out.printf("|%-10s | %-20s | %12s | %12s | %10s | %12s |%n",
            billing.getNo(),
            billing.getType(),
            billing.getAmount(),
            billing.getDueDate(),
            billing.getState(),
            billing.getProvider()));

        System.out.printf("--------------------------------%n");
    }

    private static void cashInMoney(int amount) {
        paymentService.cashIn(amount);
    }

    private static void makePayment(List<String> billings) {
        billings.forEach((bill) -> {
            int billId = Integer.parseInt(bill);
            BillingEntity billingEntity = billingStore.get(billId);
            if (billingEntity == null) {
                System.out.println("Sorry! Not found a bill with such id");
                return;
            }
            if (billingEntity.getState().equals(BillingState.PAID)) {
                System.out.println("Sorry! This bill has been paid, not need to change due date");
                return;
            }
            // find exist payments of billId or create a new one
            List<PaymentEntity> payments = paymentStore.values()
                    .stream()
                    .filter(p -> p.getBillId() == billId)
                    .toList();

            PaymentEntity paymentEntity;
            if (payments.isEmpty()) {
                paymentEntity = new PaymentEntity();
                paymentEntity.setAmount(billingEntity.getAmount());
                paymentEntity.setNo(++paymentIndex);
                paymentEntity.setBillId(billId);
                paymentEntity.setProvider(billingEntity.getProvider());
                paymentEntity.setPaymentDate(DateTimeUtils.localTimeToStringFormat());
            } else {
                paymentEntity = payments.stream().findFirst().get();
            }

            if (paymentService.makePayment(billingEntity)) {
                paymentEntity.setState(PaymentState.PROCESSED);
                // Update billing again to billing store
                billingEntity.setState(BillingState.PAID);
                billingStore.put(billId, billingEntity);
            } else {
                paymentEntity.setState(PaymentState.PENDING);
            }

            paymentStore.put(paymentIndex, paymentEntity);
        });

    }

    private static void listPayment() {
        System.out.printf("--------------------------------%n");
        System.out.printf(" (print payment table )         %n");
        System.out.printf("--------------------------------%n");
        System.out.printf("|%-10s | %-12s | %12s | %12s | %12s |%n", "Bill No.", "Amount", "Payment Date", "State", "Bill Id");
        paymentStore.values().forEach(payment -> System.out.printf("|%-10s | %-12s | %12s | %12s | %12s |%n",
                payment.getNo(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getState(),
                payment.getBillId()));

        System.out.printf("--------------------------------%n");

    }

    private static void checkBalance() {
        System.out.println("Current balance: $" + paymentService.getBalance());
    }
}

