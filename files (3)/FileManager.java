import java.io.*;
import java.time.LocalDate;
import java.util.*;

// ╔══════════════════════════════════════════════════════════════════╗
// ║                       FileManager.java                          ║
// ║              FILE I/O — Save and Load Transactions              ║
// ╠══════════════════════════════════════════════════════════════════╣
// ║  FORMAT : Plain CSV (Comma Separated Values)                    ║
// ║  FILE   : transactions.csv  (created in same folder as .java)  ║
// ║                                                                  ║
// ║  CSV STRUCTURE:                                                  ║
// ║    id,name,income,expense,category,date                         ║
// ║    1,"Monthly Salary",85000.00,0.00,"Salary",2026-03-01         ║
// ║    2,"Rent Payment",0.00,18000.00,"Housing",2026-03-02          ║
// ║                                                                  ║
// ║  TIME COMPLEXITY:                                                ║
// ║    save() → O(n)  — writes each transaction once                ║
// ║    load() → O(n)  — reads and parses each line once             ║
// ╚══════════════════════════════════════════════════════════════════╝
public class FileManager {

    private static final String FILE = "transactions.csv";

    // ══════════════════════════════════════════════════════════════
    // SAVE — Write all transactions to CSV file
    // ══════════════════════════════════════════════════════════════
    // Iterates the ArrayList once → O(n)
    // Overwrites the file completely each time (no append).
    // ══════════════════════════════════════════════════════════════
    public static void save(List<Transaction> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            // Write header row first
            pw.println("id,name,income,expense,category,date");

            // Write each transaction as one CSV line
            for (Transaction t : list) {
                pw.printf("%d,\"%s\",%.2f,%.2f,\"%s\",%s%n",
                        t.id,
                        t.name.replace("\"", "\"\""),      // escape quotes
                        t.income,
                        t.expense,
                        t.category.replace("\"", "\"\""),  // escape quotes
                        t.date.format(Transaction.DATE_FMT));
            }
            System.out.println("  [FileManager] Saved " + list.size()
                    + " transaction(s) to " + FILE);
        } catch (IOException e) {
            System.out.println("  [FileManager] ERROR saving: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════
    // LOAD — Read all transactions from CSV file
    // ══════════════════════════════════════════════════════════════
    // Returns empty list if file does not exist yet.
    // Skips malformed lines silently.
    // ══════════════════════════════════════════════════════════════
    public static List<Transaction> load() {
        List<Transaction> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return list;  // no file found, return empty

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }  // skip header
                if (line.trim().isEmpty()) continue;            // skip blank lines
                Transaction t = parseLine(line);
                if (t != null) list.add(t);
            }
        } catch (IOException e) {
            System.out.println("  [FileManager] ERROR loading: " + e.getMessage());
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════════
    // PARSE — Convert one CSV line into a Transaction object
    // Handles quoted fields that may contain commas.
    // Returns null if line is malformed.
    // ══════════════════════════════════════════════════════════════
    private static Transaction parseLine(String line) {
        try {
            List<String> parts = new ArrayList<>();
            StringBuilder sb   = new StringBuilder();
            boolean inQuotes   = false;

            for (char c : line.toCharArray()) {
                if (c == '"')             inQuotes = !inQuotes;
                else if (c==',' && !inQuotes) { parts.add(sb.toString()); sb.setLength(0); }
                else                      sb.append(c);
            }
            parts.add(sb.toString());
            if (parts.size() < 6) return null;

            return new Transaction(
                Integer.parseInt(parts.get(0).trim()),
                parts.get(1).trim(),
                Double.parseDouble(parts.get(2).trim()),
                Double.parseDouble(parts.get(3).trim()),
                parts.get(4).trim(),
                LocalDate.parse(parts.get(5).trim(), Transaction.DATE_FMT)
            );
        } catch (Exception e) {
            return null;  // skip bad line
        }
    }
}
