package org.example;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InteractiveOrderProcessor {
    public static void main(String[] args) {
        //  declare scanner object
        Scanner scanObj = new Scanner(System.in);

        System.out.println("---------------------------------");
        System.out.println("Welcome to the Interactive Order Processor!\n");

        System.out.println("--- Enter Order Details ---");

        // ~~~~~ inputs and error handling portion ~~~~~
        double unitPrice = 0.0;
        // unit price input error handling
        while (true) {
            try {
                System.out.print("Enter unit price: $");
                unitPrice = scanObj.nextDouble();
                if (unitPrice < 0) throw new IllegalArgumentException("Unit price cannot be negative.");
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number for unit price.");
                scanObj.next(); // Clear invalid input
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }


        int quantity = 0;
        // quantity input error handling
        while (true) {
            try {
                System.out.print("Enter quantity: ");
                quantity = scanObj.nextInt();
                if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative.");
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer for quantity.");
                scanObj.next(); // Clear invalid input
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }


        boolean isMember = false;
        // isMember boolean input error handling
        while (true) {
            try {
                System.out.print("Is customer a member (true/false)?: ");
                isMember = scanObj.nextBoolean();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter true or false.");
                scanObj.next(); // Clear invalid input
            }
        }

        scanObj.nextLine(); // consume leftover newline

        System.out.print("Enter customer tier (Regular, Silver, Gold): ");
        String customerTier = scanObj.nextLine();

        System.out.print("Enter shipping zone (ZoneA, ZoneB, ZoneC, Unknown): ");
        String shippingZone = scanObj.nextLine();
        // shipping zone blank input handling
        while (true) {
            if(shippingZone.isEmpty()) {
                System.out.println("Shipping Zone can't be empty. Try again");
                System.out.print("Enter shipping zone (ZoneA, ZoneB, ZoneC, Unknown): ");
                shippingZone = scanObj.nextLine();
            }
            else {
                break;
            }
        }

        System.out.print("Enter discount code (SAVE10, FREESHIP, or \"\" for none): ");
        String discountCode = scanObj.nextLine();

        // ~~~~~ user inputs summary portion ~~~~~
        System.out.println("\n--- Order Details ---");
        System.out.printf("\nUnit Price: $%.2f", unitPrice);
        System.out.printf("\nQuantity: %s", quantity);
        System.out.printf("\nIs Member: %s", isMember);
        System.out.printf("\nCustomer Tier: %s", customerTier);
        System.out.printf("\nShipping Zone: %s", shippingZone);
        System.out.printf("\nDiscount Code: %s", discountCode);

        // ~~~~~ calculation steps portion ~~~~~
        System.out.println("\n\n--- Calculation Steps ---");

        double initialSubtotal = calcInitialSubtotal(unitPrice, quantity);
        System.out.printf("Initial Subtotal: $%.2f", initialSubtotal);

        double subTotalAfterTierDiscount = calcTierBasedDiscount(initialSubtotal, customerTier);
        System.out.printf("\nAfter Tier Discount %s: $%.2f",
                getTierBasedStringDisplay(customerTier),
                subTotalAfterTierDiscount
        );

        double subTotalAfterQuantityDiscount = calcAfterQuantityDiscount(subTotalAfterTierDiscount, quantity);
        System.out.printf("\nAfter Quantity Discount (5%% for >=5 items): $%.2f", subTotalAfterQuantityDiscount);

        //  determine if order shipping is free or not using .equals("FREESHIP")
        boolean isFreeShipping = discountCode.equals("FREESHIP");

        double subTotalAfterPromotionCodeDisc = calcPromotionalCodeDiscount(subTotalAfterQuantityDiscount, discountCode);
        System.out.printf("\nAfter Promotional Code (SAVE10 for >$75): $%.2f", subTotalAfterPromotionCodeDisc);

        //  applied ternary operator to check if subtotal after calculate promotion code
        double subtotalSmallOrderSurcharge = subTotalAfterPromotionCodeDisc < 25 ?
                subTotalAfterPromotionCodeDisc + 3.00 : subTotalAfterPromotionCodeDisc;

        //  applied ternary operator for dynamic surcharge output display to pass in print
        String smallOrderSurchargeDisplay = subTotalAfterPromotionCodeDisc < 25 ? "Surcharge Applied" : "No Surcharge";
        System.out.printf("\nAfter Small Order Surcharge (if applicable): $%.2f (%s)",
                subtotalSmallOrderSurcharge, smallOrderSurchargeDisplay);

        //  applied ternary operation for shipping cost
        double shippingCost = isFreeShipping ? 0.00 : calcShippingCost(shippingZone);
        System.out.printf("\n\nShipping Cost: $%.2f (%s)", shippingCost, shippingZone);

        //  final order total will add the previous subtotal to shipping cost
        System.out.printf("\n\nFinal Order Total: $%.2f", (subtotalSmallOrderSurcharge + shippingCost));
        System.out.println("\n---------------------------------");

        //  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //  string equality demo portion
        System.out.println("\n--- String Equality Demo ---");

        System.out.print("Enter first string for comparison: ");
        String firstStringInput = scanObj.nextLine();

        System.out.print("Enter second string for comparison: ");
        String secondStringInput = scanObj.nextLine();

        System.out.printf("\nString 1: \"%s\"", firstStringInput);
        System.out.printf("\nString 2: \"%s\"", secondStringInput);

        boolean compareStrings = (firstStringInput == secondStringInput);
        boolean equalsStrings = firstStringInput.equals(secondStringInput);
        boolean equalsIgnoreCaseStrings = firstStringInput.equalsIgnoreCase(secondStringInput);

        System.out.printf("\n\nString 1 == String 2: [%s] (Compares references, which are different for user input strings)",
                compareStrings);
        System.out.printf("\nString 1 .equals() String 2: [%s] (Content is different due to case)",
                equalsStrings);
        System.out.printf("\nString 1 .equalsIgnoreCase() String 2: [%s] (Content is identical, ignoring case)",
                equalsIgnoreCaseStrings);

        //  close scanner object
        scanObj.close();

    }
    //  method to calculate initial subtotal
    public static double calcInitialSubtotal(double unitPrice, int quant) {
        return unitPrice * quant;
    }

    //  method to get String display for "Tier-Based Discount" (e.g. Gold - 15%, Silver - 10%)
    public static String getTierBasedStringDisplay(String customerTier) {
        if(customerTier.equalsIgnoreCase("gold")) {
            return "(Gold - 15%)";
        } else if(customerTier.equalsIgnoreCase("silver")) {
            return "(Silver - 10%)";
        }
        else {
            return "(Regular)";
        }
    }

    //  method to calculate "Tier-Based Discount"
    public static double calcTierBasedDiscount(double subtotal, String customerTier) {
        // declared FINAL discount percentage tier
        final double GOLD_TIER = 0.15;
        final double SILVER_TIER = 0.1;

        //  if customerTier is "Gold", apply 15% discount based on unit price
        if(customerTier.equalsIgnoreCase("gold")) {
            return subtotal - (subtotal * GOLD_TIER);
        } else if(customerTier.equalsIgnoreCase("silver")) {
            return subtotal - (subtotal * SILVER_TIER);
        }
        else {
            return subtotal;
        }
    }

    // method to calculate "Quantity Discount" and apply 5% discount for quantity value more than 5
    public static double calcAfterQuantityDiscount(double subtotal, int quant) {
        if(quant >= 5) {
            return subtotal - (subtotal * 0.05);
        }
        else {
            return subtotal;
        }
    }

    // method to calculate "Promotional Code Application" discount
    public static double calcPromotionalCodeDiscount(double subtotal, String discountCode) {
        if(discountCode.equals("SAVE10") && subtotal > 75) {
            return subtotal - 10;
        }
        else {
            return subtotal;
        }
    }

    // method to calculate "Shipping Cost"
    public static double calcShippingCost(String shippingZone) {
        double shippingCost = 0;

        switch (shippingZone.toLowerCase()) {
            case "zonea":   // ZoneA
                shippingCost += 5.00;
                break;
            case "zoneb":
                shippingCost += 12.50;
                break;
            case "zonec":
                shippingCost += 20.00;
                break;
            default:
                shippingCost += 25.00;
                break;
        }

        return shippingCost;
    }


}