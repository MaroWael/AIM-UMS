package com.ums.system;

import com.ums.system.model.*;
import com.ums.system.service.*;
import com.ums.system.utils.DatabaseConnection;

import java.sql.Connection;
import java.util.List;

/**
 * Payment System Test & Demo
 * Run this to test the level fee payment system
 */
public class PaymentSystemDemo {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  UMS LEVEL FEE PAYMENT SYSTEM - DEMO");
        System.out.println("=================================================\n");

        Connection conn = DatabaseConnection.getInstance().getConnection();
        PaymentService paymentService = new PaymentServiceImpl(conn);

        int studentId = 12;
        int studentLevel = 2;

        try {
            System.out.println("LEVEL FEE STRUCTURE:");
            System.out.println("─────────────────────────────────────────────");
            for (int level = 1; level <= 4; level++) {
                double fee = paymentService.calculateLevelFee(level);
                String yearName = getYearName(level);
                System.out.printf("Level %d (%s):  $%.2f\n", level, yearName, fee);
            }
            System.out.println();

            System.out.println("CHECKING PAYMENT STATUS:");
            System.out.println("─────────────────────────────────────────────");
            System.out.println("Student ID: " + studentId);
            System.out.println("Current Level: " + studentLevel + " (" + getYearName(studentLevel) + ")");

            boolean hasPaid = paymentService.hasUserPaidForLevel(studentId, studentLevel);

            if (hasPaid) {
                System.out.println("Status: PAID");
                System.out.println("\nStudent has already paid for this level.");
            } else {
                System.out.println("Status: NOT PAID");
                System.out.println("\nProcessing payment...\n");

                double levelFee = paymentService.calculateLevelFee(studentLevel);

                PaymentRequest request = new PaymentRequest(
                    studentId,
                    studentLevel,
                    levelFee,
                    "EGP",
                    "Level " + studentLevel + " tuition fee",
                    "CARD"
                );

                System.out.println("Payment Details:");
                System.out.println("  Amount: $" + levelFee);
                System.out.println("  Method: CARD");
                System.out.println("  Description: " + request.getDescription());
                System.out.println("\nConnecting to payment gateway...");

                Payment payment = paymentService.processPayment(request);

                System.out.println("\nPAYMENT RESULT:");
                System.out.println("─────────────────────────────────────────────");

                if ("SUCCESS".equals(payment.getStatus())) {
                    System.out.println("PAYMENT SUCCESSFUL!");
                    System.out.println();
                    System.out.println("Payment ID: " + payment.getId());
                    System.out.println("Transaction ID: " + payment.getTransactionId());
                    System.out.println("Amount Paid: $" + payment.getAmount());
                    System.out.println("Level: " + payment.getLevel());
                    System.out.println("Date: " + payment.getCreatedAt());
                } else {
                    System.out.println("PAYMENT FAILED");
                    System.out.println("Status: " + payment.getStatus());
                    System.out.println("\nPlease try again or contact administration.");
                }
            }

            System.out.println("\n\nPAYMENT HISTORY:");
            System.out.println("─────────────────────────────────────────────");
            List<Payment> paymentHistory = paymentService.getPaymentsByUserId(studentId);

            if (paymentHistory.isEmpty()) {
                System.out.println("No payment history found.");
            } else {
                System.out.printf("%-5s %-8s %-12s %-12s %-25s\n",
                    "ID", "Level", "Amount", "Status", "Date");
                System.out.println("─".repeat(65));

                for (Payment p : paymentHistory) {
                    System.out.printf("%-5d %-8d $%-11.2f %-12s %-25s\n",
                        p.getId(),
                        p.getLevel(),
                        p.getAmount(),
                        p.getStatus(),
                        p.getCreatedAt().toString().substring(0, 19)
                    );
                }
            }

            System.out.println("\n\nREVENUE STATISTICS:");
            System.out.println("─────────────────────────────────────────────");

            double totalRevenue = paymentService.getTotalRevenue();
            System.out.printf("Total Revenue: $%.2f\n\n", totalRevenue);

            System.out.println("Revenue by Level:");
            for (int level = 1; level <= 4; level++) {
                double levelRevenue = paymentService.getTotalRevenueByLevel(level);
                System.out.printf("  Level %d: $%.2f\n", level, levelRevenue);
            }

            System.out.println("\n=================================================");
            System.out.println("  DEMO COMPLETED SUCCESSFULLY ");
            System.out.println("=================================================");

        } catch (Exception e) {
            System.err.println("\nERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getYearName(int level) {
        switch (level) {
            case 1: return "Freshman";
            case 2: return "Sophomore";
            case 3: return "Junior";
            case 4: return "Senior";
            default: return "Unknown";
        }
    }
}

