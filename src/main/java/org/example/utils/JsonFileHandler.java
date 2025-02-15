package org.example.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.example.entity.BillingEntity;
import org.example.entity.PaymentEntity;
import org.example.enums.BillingState;
import org.example.enums.PaymentState;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonFileHandler {

    public static List<BillingEntity> readBillings(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
        return parseJsonBillings(content.toString());
    }

    private static List<BillingEntity> parseJsonBillings(String jsonString) {
        List<BillingEntity> billings = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            BillingEntity billing = new BillingEntity();
            billing.setProvider(jsonObject.getString("provider"));
            billing.setAmount(jsonObject.getInt("amount"));
            billing.setState(BillingState.valueOf(jsonObject.getString("state")));
            billing.setType(jsonObject.getString("type"));
            billing.setNo(jsonObject.getInt("no"));
            billing.setDueDate(jsonObject.getString("dueDate"));
            billings.add(billing);
        }
        return billings;
    }

    public static void writeBilling(List<BillingEntity> billings, String filePath) {
        JSONArray jsonArray = new JSONArray();
        for (BillingEntity billing : billings) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("provider", billing.getProvider());
            jsonObject.put("amount", billing.getAmount());
            jsonObject.put("no", billing.getNo());
            jsonObject.put("type", billing.getType());
            jsonObject.put("state", billing.getState());
            jsonObject.put("dueDate", billing.getDueDate());
            jsonArray.put(jsonObject);
        }
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4)); // Pretty print with indentation
            file.flush();
        } catch (IOException e) {
            System.err.println("Error writing JSON file: " + e.getMessage());
        }
    }

    public static int readBalance(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            return Integer.parseInt(line.trim());
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading integer from file: " + e.getMessage());
            return 0;
        }
    }

    public static void writeBalance(int balance, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(String.valueOf(balance));
            System.out.println("Successfully written " + balance + " to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static List<PaymentEntity> readPayment(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
        return parseJsonPayment(content.toString());
    }

    private static List<PaymentEntity> parseJsonPayment(String jsonString) {
        List<PaymentEntity> payments = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            PaymentEntity payment = new PaymentEntity();
            payment.setProvider(jsonObject.getString("provider"));
            payment.setAmount(jsonObject.getInt("amount"));
            payment.setState(PaymentState.valueOf(jsonObject.getString("state")));
            payment.setBillId(jsonObject.getInt("billId"));
            payment.setNo(jsonObject.getInt("no"));
            payments.add(payment);
        }
        return payments;
    }

    public static void writePayment(List<PaymentEntity> payments, String filePath) {
        JSONArray jsonArray = new JSONArray();
        for (PaymentEntity payment : payments) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("provider", payment.getProvider());
            jsonObject.put("amount", payment.getAmount());
            jsonObject.put("billId", payment.getBillId());
            jsonObject.put("no", payment.getNo());
            jsonObject.put("state", payment.getState());
            jsonArray.put(jsonObject);
        }
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4)); // Pretty print with indentation
            file.flush();
        } catch (IOException e) {
            System.err.println("Error writing JSON file: " + e.getMessage());
        }
    }

}
