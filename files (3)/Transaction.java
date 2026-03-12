import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// ╔══════════════════════════════════════════════════════════════════╗
// ║                       Transaction.java                          ║
// ║           CORE DATA MODEL — used by ALL data structures         ║
// ╠══════════════════════════════════════════════════════════════════╣
// ║  This class represents ONE financial transaction.               ║
// ║  Every ArrayList node, Stack element, LinkedList node,          ║
// ║  PriorityQueue element, HashMap value, and BST node             ║
// ║  stores an object of this class.                                ║
// ╚══════════════════════════════════════════════════════════════════╝

public class Transaction {

    // ── DATE FORMAT ────────────────────────────────────────────────
    // Static formatter shared across the whole application
    // Pattern: yyyy-MM-dd   e.g. 2026-03-15
    public static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ── FIELDS ─────────────────────────────────────────────────────
    int       id;        // Unique auto-incrementing identifier
    String    name;      // Description  e.g. "Grocery Shopping"
    double    income;    // Money received (credit)
    double    expense;   // Money spent   (debit)
    String    category;  // Tag: Food, Salary, Transport, etc.
    LocalDate date;      // Date of the transaction

    // ── CONSTRUCTOR ────────────────────────────────────────────────
    public Transaction(int id, String name, double income,
                       double expense, String category, LocalDate date) {
        this.id       = id;
        this.name     = name;
        this.income   = income;
        this.expense  = expense;
        this.category = category;
        this.date     = date;
    }

    // ── GETTERS ────────────────────────────────────────────────────
    public int       getTransactionId() { return id; }
    public String    getName()          { return name; }
    public double    getIncome()        { return income; }
    public double    getExpense()       { return expense; }
    public String    getCategory()      { return category; }
    public LocalDate getDate()          { return date; }

    // ── toString ───────────────────────────────────────────────────
    // Used every time a Transaction is printed to the console.
    @Override
    public String toString() {
        String n = name.length() > 20 ? name.substring(0, 19) + "." : name;
        return String.format(
            "[ID:%-2d] %-20s | Income:%10.2f | Expense:%10.2f | %-12s | %s",
            id, n, income, expense, category, date.format(DATE_FMT));
    }
}
