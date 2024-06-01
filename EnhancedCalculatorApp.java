import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Stack;

public class EnhancedCalculatorApp extends JFrame {
    private JTextField display;
    private StringBuilder currentInput;
    private static final String FILE_NAME = "history.txt";

    public EnhancedCalculatorApp() {
        setTitle("Enhanced Calculator");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        currentInput = new StringBuilder();

        display = new JTextField();
        display.setFont(new Font("Arial", Font.BOLD, 32));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel numberPanel = new JPanel(new GridLayout(4, 3, 5, 5));
        String[] numberButtons = {
                "7", "8", "9",
                "4", "5", "6",
                "1", "2", "3",
                "0", "(", ")"
        };

        for (String text : numberButtons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 24));
            button.setFocusPainted(false);
            button.setBackground(new Color(220, 220, 220));
            button.setOpaque(true);
            button.addActionListener(new ButtonClickListener());
            numberPanel.add(button);
        }

        JPanel basicOperationPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        String[] basicOperationButtons = {
                "+", "-", "*", "/", ".", "="
        };

        for (String text : basicOperationButtons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 24));
            button.setFocusPainted(false);
            button.setBackground(new Color(180, 180, 180));
            button.setOpaque(true);
            button.addActionListener(new ButtonClickListener());
            basicOperationPanel.add(button);
        }

        JPanel complexOperationPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        String[] complexOperationButtons = {
                "^", "%", "√", "log", "lg", "ln", "C"
        };

        for (String text : complexOperationButtons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 24));
            button.setFocusPainted(false);
            button.setBackground(new Color(180, 180, 180));
            button.setOpaque(true);
            button.addActionListener(new ButtonClickListener());
            complexOperationPanel.add(button);
        }

        JPanel historyPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton historyButton = new JButton("History");
        historyButton.setFont(new Font("Arial", Font.BOLD, 24));
        historyButton.setFocusPainted(false);
        historyButton.setBackground(new Color(180, 180, 180));
        historyButton.setOpaque(true);
        historyButton.addActionListener(e -> viewHistory());

        JButton clearHistoryButton = new JButton("Clear History");
        clearHistoryButton.setFont(new Font("Arial", Font.BOLD, 24));
        clearHistoryButton.setFocusPainted(false);
        clearHistoryButton.setBackground(new Color(180, 180, 180));
        clearHistoryButton.setOpaque(true);
        clearHistoryButton.addActionListener(e -> clearHistory());

        historyPanel.add(historyButton);
        historyPanel.add(clearHistoryButton);

        mainPanel.add(display, BorderLayout.NORTH);
        mainPanel.add(complexOperationPanel, BorderLayout.WEST);
        mainPanel.add(numberPanel, BorderLayout.CENTER);
        mainPanel.add(basicOperationPanel, BorderLayout.EAST);
        mainPanel.add(historyPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case "=":
                    calculateResult();
                    break;
                case "C":
                    currentInput.setLength(0);
                    display.setText("");
                    break;
                case "√":
                    currentInput.append("Math.sqrt(");
                    break;
                case "log":
                    currentInput.append("Math.log10(");
                    break;
                case "lg":
                    currentInput.append("Math.log10(");
                    break;
                case "ln":
                    currentInput.append("Math.log(");
                    break;
                default:
                    currentInput.append(command);
            }
            display.setText(currentInput.toString());
        }
    }

    private void calculateResult() {
        try {
            String input = currentInput.toString();
            double result = evaluateExpression(input);
            display.setText(String.valueOf(result));
            saveHistory(input + " = " + result);
            currentInput.setLength(0);
        } catch (Exception e) {
            display.setText("Error");
            currentInput.setLength(0);
        }
    }

    private double evaluateExpression(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int len = expression.length();

        for (int i = 0; i < len; i++) {
            char ch = expression.charAt(i);

            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < len && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--;
                numbers.push(Double.parseDouble(sb.toString()));
            } else if (ch == 'M') { // for Math.sqrt, Math.log10, Math.log
                StringBuilder sb = new StringBuilder();
                while (i < len && expression.charAt(i) != '(') {
                    sb.append(expression.charAt(i++));
                }
                if (sb.toString().equals("Math.sqrt")) {
                    int j = i;
                    StringBuilder nested = new StringBuilder();
                    int bracketCount = 1;
                    while (bracketCount > 0) {
                        j++;
                        if (expression.charAt(j) == '(') bracketCount++;
                        if (expression.charAt(j) == ')') bracketCount--;
                        if (bracketCount > 0) nested.append(expression.charAt(j));
                    }
                    double nestedResult = evaluateExpression(nested.toString());
                    numbers.push(Math.sqrt(nestedResult));
                    i = j;
                } else if (sb.toString().equals("Math.log10")) {
                    int j = i;
                    StringBuilder nested = new StringBuilder();
                    int bracketCount = 1;
                    while (bracketCount > 0) {
                        j++;
                        if (expression.charAt(j) == '(') bracketCount++;
                        if (expression.charAt(j) == ')') bracketCount--;
                        if (bracketCount > 0) nested.append(expression.charAt(j));
                    }
                    double nestedResult = evaluateExpression(nested.toString());
                    numbers.push(Math.log10(nestedResult));
                    i = j;
                } else if (sb.toString().equals("Math.log")) {
                    int j = i;
                    StringBuilder nested = new StringBuilder();
                    int bracketCount = 1;
                    while (bracketCount > 0) {
                        j++;
                        if (expression.charAt(j) == '(') bracketCount++;
                        if (expression.charAt(j) == ')') bracketCount--;
                        if (bracketCount > 0) nested.append(expression.charAt(j));
                    }
                    double nestedResult = evaluateExpression(nested.toString());
                    numbers.push(Math.log(nestedResult));
                    i = j;
                }
            } else if (ch == '^') {
                i++;
                StringBuilder sb = new StringBuilder();
                while (i < len && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--;
                double base = numbers.pop();
                double exponent = Double.parseDouble(sb.toString());
                numbers.push(Math.pow(base, exponent));
            } else if (ch == '%') {
                numbers.push(numbers.pop() / 100);
            } else if (ch == '(') {
                operators.push(ch);
            } else if (ch == ')') {
                while (operators.peek() != '(') {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop();
            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                while (!operators.isEmpty() && precedence(ch) <= precedence(operators.peek())) {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(ch);
            }
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
        }
        return -1;
    }

    private double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }

    private void saveHistory(String record) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(record);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error: Unable to save history", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewHistory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            StringBuilder history = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                history.append(line).append("\n");
            }
            JOptionPane.showMessageDialog(this, history.toString(), "Calculation History", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error: Unable to read history", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write("");
            JOptionPane.showMessageDialog(this, "History cleared successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error: Unable to clear history", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EnhancedCalculatorApp calculatorApp = new EnhancedCalculatorApp();
            calculatorApp.setVisible(true);
        });
    }
}
