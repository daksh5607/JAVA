import java.util.InputMismatchException;
import java.util.Scanner;

class CalculatorLogic {
    public int add(int x, int y) { return x + y; }
    public double add(double x, double y) { return x + y; }
    public int add(int x, int y, int z) { return x + y + z; }

    public int subtract(int x, int y) { return x - y; }

    public double multiply(double x, double y) { return x * y; }

    public double divide(int x, int y) throws ArithmeticException {
        if (y == 0) {
            throw new ArithmeticException("Error: Cannot divide by zero.");
        }
        return (double) x / y;
    }
}

public class SimpleCalculator {
    private Scanner input = new Scanner(System.in);
    private CalculatorLogic logic = new CalculatorLogic();

    public void start() {
        int choice;
        do {
            showMenu();
            try {
                choice = input.nextInt();
                input.nextLine();

                switch (choice) {
                    case 1: handleAddition(); break;
                    case 2: handleSubtraction(); break;
                    case 3: handleMultiplication(); break;
                    case 4: handleDivision(); break;
                    case 5: System.out.println("Thank you! Program exiting."); break;
                    default: System.out.println("Invalid option. Please choose 1-5.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a number for your choice.");
                input.nextLine();
                choice = 0;
            }
        } while (choice != 5);
        input.close();
    }

    private void showMenu() {
        System.out.println("\n--- Calculator Menu ---");
        System.out.println("1. Add (Overloaded)");
        System.out.println("2. Subtract");
        System.out.println("3. Multiply");
        System.out.println("4. Divide (Handles Zero)");
        System.out.println("5. Exit");
        System.out.print("Enter choice: ");
    }

    private void handleAddition() {
        System.out.println("\n--- Addition Types ---");
        System.out.println("1. Two Integers (int)");
        System.out.println("2. Two Decimals (double)");
        System.out.println("3. Three Integers (int, int, int)");
        System.out.print("Select addition type: ");
        
        try {
            int type = input.nextInt();
            if (type == 1) {
                System.out.print("Enter two integers: ");
                int num1 = input.nextInt();
                int num2 = input.nextInt();
                System.out.println("Result: " + logic.add(num1, num2));
            } else if (type == 2) {
                System.out.print("Enter two decimals: ");
                double num1 = input.nextDouble();
                double num2 = input.nextDouble();
                System.out.println("Result: " + logic.add(num1, num2));
            } else if (type == 3) {
                System.out.print("Enter three integers: ");
                int num1 = input.nextInt();
                int num2 = input.nextInt();
                int num3 = input.nextInt();
                System.out.println("Result: " + logic.add(num1, num2, num3));
            } else {
                System.out.println("Invalid addition type selected.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Input Error. Please ensure you enter the correct number type.");
            input.nextLine();
        }
    }

    private void handleSubtraction() {
        try {
            System.out.print("Enter two integers for subtraction (a - b): ");
            int a = input.nextInt(), b = input.nextInt();
            System.out.println("Result: " + logic.subtract(a, b));
        } catch (InputMismatchException e) {
            System.out.println("Input Error. Please enter integers.");
            input.nextLine();
        }
    }

    private void handleMultiplication() {
        try {
            System.out.print("Enter two decimals for multiplication: ");
            double a = input.nextDouble(), b = input.nextDouble();
            System.out.println("Result: " + logic.multiply(a, b));
        } catch (InputMismatchException e) {
            System.out.println("Input Error. Please enter decimal numbers.");
            input.nextLine();
        }
    }

    private void handleDivision() {
        try {
            System.out.print("Enter numerator (dividend): ");
            int a = input.nextInt();
            System.out.print("Enter denominator (divisor): ");
            int b = input.nextInt();
            
            double result = logic.divide(a, b);
            System.out.println("Result: " + result);
        } catch (InputMismatchException e) {
            System.out.println("Input Error. Division requires integers.");
            input.nextLine();
        } catch (ArithmeticException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        new SimpleCalculator().start();
    }
}
