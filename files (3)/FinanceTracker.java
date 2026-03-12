import java.util.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;

// ╔══════════════════════════════════════════════════════════════════════╗
// ║                  ULTIMATE FINANCE TRACKER                           ║
// ║          Data Structures & Algorithms — Java Console Project        ║
// ╠══════════════════════════════════════════════════════════════════════╣
// ║  DATA STRUCTURES USED:                                              ║
// ║  ┌─────────────────────┬───────────────────────────────────────┐   ║
// ║  │ Data Structure      │ Used For                              │   ║
// ║  ├─────────────────────┼───────────────────────────────────────┤   ║
// ║  │ 1. ArrayList        │ Main transaction storage              │   ║
// ║  │ 2. Stack            │ Undo last transaction (LIFO)          │   ║
// ║  │ 3. LinkedList       │ Node chain + Queue (FIFO)             │   ║
// ║  │ 4. HashMap          │ Category expense totals               │   ║
// ║  │ 5. PriorityQueue    │ Max-Heap for highest expense          │   ║
// ║  │ 6. BST              │ Tree traversal by ID                  │   ║
// ║  └─────────────────────┴───────────────────────────────────────┘   ║
// ║                                                                      ║
// ║  ALGORITHMS USED:                                                   ║
// ║  ┌─────────────────────┬──────────────┬───────────────────────┐    ║
// ║  │ Algorithm           │ Complexity   │ Used For              │    ║
// ║  ├─────────────────────┼──────────────┼───────────────────────┤    ║
// ║  │ Linear Search       │ O(n)         │ Search by keyword     │    ║
// ║  │ Binary Search       │ O(log n)     │ Search by ID          │    ║
// ║  │ Bubble Sort         │ O(n²)        │ Sort by income        │    ║
// ║  │ Insertion Sort      │ O(n²)        │ Sort by expense       │    ║
// ║  │ Merge Sort          │ O(n log n)   │ Sort by date          │    ║
// ║  └─────────────────────┴──────────────┴───────────────────────┘    ║
// ╚══════════════════════════════════════════════════════════════════════╝

public class FinanceTracker {

    // ══════════════════════════════════════════════════════════════════
    // DATA STRUCTURE 1 : ArrayList<Transaction>
    // ══════════════════════════════════════════════════════════════════
    // DEFINITION : A dynamic array that resizes itself automatically.
    //              Internally backed by a plain Java array.
    //              When full, it creates a new array of double the size
    //              and copies all elements over.
    //
    // WHY USED   : Best for storing the main list of transactions
    //              because we need fast index access (get by position).
    //
    // OPERATIONS:
    //   add(element)        → O(1) amortised  (occasional resize O(n))
    //   get(index)          → O(1)            (direct array access)
    //   remove(index)       → O(n)            (shifts elements left)
    //   size()              → O(1)
    //   contains/search     → O(n)            (linear scan)
    //
    // VISUAL:
    //   Index: [0]         [1]          [2]         [3]
    //   Data:  [Salary TX] [Rent TX]    [Food TX]   [Uber TX]
    // ══════════════════════════════════════════════════════════════════
    private static ArrayList<Transaction> transactions = new ArrayList<>();

    // ══════════════════════════════════════════════════════════════════
    // DATA STRUCTURE 2 : Stack<Transaction>
    // ══════════════════════════════════════════════════════════════════
    // DEFINITION : A LIFO (Last In First Out) collection.
    //              Think of a stack of plates — you can only add or
    //              remove from the TOP.
    //
    // WHY USED   : Perfect for UNDO because the last transaction added
    //              should be the first one undone.
    //
    // OPERATIONS:
    //   push(element) → O(1)   adds to TOP of stack
    //   pop()         → O(1)   removes from TOP and returns it
    //   peek()        → O(1)   looks at TOP without removing
    //   isEmpty()     → O(1)
    //
    // VISUAL:
    //   TOP → [Stock Dividend]   ← most recently added (will undo first)
    //         [Restaurant Lunch]
    //         [Amazon Shopping]
    //         [Uber Rides]
    //   BOT → [Monthly Salary]   ← added first (will undo last)
    // ══════════════════════════════════════════════════════════════════
    private static Stack<Transaction> undoStack = new Stack<>();

    // ══════════════════════════════════════════════════════════════════
    // DATA STRUCTURE 3A : Queue<Transaction>  via  LinkedList
    // ══════════════════════════════════════════════════════════════════
    // DEFINITION : A FIFO (First In First Out) collection.
    //              Implemented using Java's LinkedList class.
    //              Transactions enter at the REAR and exit from FRONT.
    //
    // WHY USED   : Simulates a queue of scheduled/pending transactions.
    //              The first transaction added will be processed first.
    //
    // OPERATIONS:
    //   offer(element) → O(1)  adds to REAR
    //   poll()         → O(1)  removes from FRONT
    //   peek()         → O(1)  looks at FRONT without removing
    //
    // VISUAL:
    //   FRONT → [Salary TX] → [Rent TX] → [Food TX] → [Uber TX] ← REAR
    //           (exits first)                           (entered last)
    // ══════════════════════════════════════════════════════════════════
    private static Queue<Transaction> txQueue = new LinkedList<>();

    // ══════════════════════════════════════════════════════════════════
    // DATA STRUCTURE 3B : LinkedList<Transaction>
    // ══════════════════════════════════════════════════════════════════
    // DEFINITION : A chain of NODE objects. Each node contains:
    //              [ DATA | NEXT POINTER ] → [ DATA | NEXT POINTER ] → NULL
    //
    //              Unlike ArrayList (contiguous memory), LinkedList
    //              nodes can be scattered anywhere in memory.
    //              They are connected only through pointers.
    //
    // WHY USED   : Demonstrates LinkedList structure — addFirst O(1),
    //              addLast O(1), traversal, search, deletion.
    //              Java's Queue above is also backed by LinkedList.
    //
    // OPERATIONS:
    //   addFirst(e)    → O(1)  inserts at HEAD (just update pointer)
    //   addLast(e)     → O(1)  inserts at TAIL (just update pointer)
    //   removeFirst()  → O(1)  removes HEAD node
    //   get(index)     → O(n)  must scan from HEAD to reach index
    //   contains(e)    → O(n)  must scan all nodes
    //
    // VISUAL:
    //   HEAD → [Salary|*] → [Rent|*] → [Food|*] → [Uber|*] → NULL
    //           node 1        node 2     node 3     node 4
    //           (*) = pointer to next node
    // ══════════════════════════════════════════════════════════════════
    private static LinkedList<Transaction> txLinkedList = new LinkedList<>();

    // ══════════════════════════════════════════════════════════════════
    // DATA STRUCTURE 4 : HashMap<String, Double>
    // ══════════════════════════════════════════════════════════════════
    // DEFINITION : A key-value pair collection backed by a hash table.
    //              When you call put("Food", 6700.00):
    //                1. Java computes "Food".hashCode() → e.g. 461
    //                2. Maps 461 → bucket index (e.g. bucket 5)
    //                3. Stores the value 6700.00 in bucket 5
    //              When you call get("Food"):
    //                1. Same hash → same bucket → O(1) retrieval
    //
    // WHY USED   : Category analysis needs fast lookup and update.
    //              Each category name maps to its total expense.
    //              Adding a new expense to a category = O(1).
    //
    // OPERATIONS:
    //   put(key, value)   → O(1) average
    //   get(key)          → O(1) average
    //   containsKey(key)  → O(1) average
    //   entrySet()        → O(n) to iterate all pairs
    //
    // VISUAL:
    //   Bucket 0: ---
    //   Bucket 1: "Salary"    → 110000.00
    //   Bucket 2: ---
    //   Bucket 3: "Food"      →   6700.00
    //   Bucket 4: "Housing"   →  18000.00
    //   Bucket 5: "Transport" →   1200.00
    //   Bucket 6: ---
    //   Bucket 7: "Shopping"  →   6800.00
    // ══════════════════════════════════════════════════════════════════
    private static Map<String, Double> categoryMap = new HashMap<>();

    // ══════════════════════════════════════════════════════════════════
    // DATA STRUCTURE 5 : PriorityQueue<Transaction>  (Max-Heap)
    // ══════════════════════════════════════════════════════════════════
    // DEFINITION : A binary tree where the PARENT is always GREATER
    //              than its children (Max-Heap property).
    //              The ROOT is always the MAXIMUM element.
    //              Internally stored as an array using the formula:
    //                parent of index i = (i-1)/2
    //                children of i     = 2i+1 and 2i+2
    //
    // WHY USED   : To find the highest expense transaction instantly.
    //              peek() returns the maximum in O(1) — no searching!
    //
    // COMPARATOR : (a, b) -> Double.compare(b.expense, a.expense)
    //              This reverses natural order → MAX-heap by expense.
    //
    // OPERATIONS:
    //   offer(element) → O(log n)  inserts and bubbles up to maintain heap
    //   peek()         → O(1)      returns root (max) without removing
    //   poll()         → O(log n)  removes root and restructures heap
    //
    // VISUAL (Max-Heap by expense):
    //               [Rent 18000]        ← ROOT = maximum expense
    //              /             \
    //     [Amazon 6800]      [Food 6700]
    //     /         \
    // [Uber 1200] [Food2 2200]
    // ══════════════════════════════════════════════════════════════════
    private static PriorityQueue<Transaction> expenseHeap =
            new PriorityQueue<>((a, b) -> Double.compare(b.expense, a.expense));

    // ══════════════════════════════════════════════════════════════════
    // DATA STRUCTURE 6 : Binary Search Tree (BST)
    // ══════════════════════════════════════════════════════════════════
    // Defined in BSTNode.java.
    // Keyed on transactionId.
    // Supports Inorder / Preorder / Postorder traversal.
    // Average Insert/Search: O(log n)
    // ══════════════════════════════════════════════════════════════════
    private static BST bst = new BST();

    // ── APP STATE ──────────────────────────────────────────────────
    private static int    nextId        = 1;    // auto-increment ID counter
    private static double monthlyBudget = 0.0;  // user-defined budget limit
    private static Scanner sc           = new Scanner(System.in);

    // ══════════════════════════════════════════════════════════════════
    // MAIN — Application Entry Point
    // ══════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        printBanner();

        // Load saved data from file; if none, load sample data
        List<Transaction> saved = FileManager.load();
        if (!saved.isEmpty()) {
            for (Transaction t : saved) registerTransaction(t);
            if (nextId <= 1) nextId = transactions.size() + 1;
            System.out.println("  Loaded " + transactions.size() + " saved transaction(s).\n");
        } else {
            System.out.println("  No save file found. Loading sample data...\n");
            loadSampleData();
        }

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");
            System.out.println();

            switch (choice) {
                case  1 -> addTransaction();
                case  2 -> showAll();
                case  3 -> showBalance();
                case  4 -> setBudget();
                case  5 -> deleteTransaction();
                case  6 -> editTransaction();
                case  7 -> linearSearch();
                case  8 -> binarySearch();
                case  9 -> bubbleSortIncome();
                case 10 -> insertionSortExpense();
                case 11 -> mergeSortDate();
                case 12 -> categoryAnalysis();
                case 13 -> undoLast();
                case 14 -> highestExpense();
                case 15 -> asciiFinancialGraph();
                case 16 -> asciiCategoryGraph();
                case 17 -> saveData();
                case 18 -> reloadData();
                case 19 -> monthlyReport();
                case 20 -> bstTraversal();
                case 21 -> linkedListDemo();
                case 22 -> showAllDSA();
                case  0 -> {
                    saveData();
                    System.out.println("  Goodbye! Come back soon.");
                    running = false;
                }
                default -> System.out.println("  Invalid option. Enter 0-22.");
            }
            // ── PAUSE after every option ───────────────────────────
            // Prevents the menu from immediately redrawing and wiping
            // the output from screen (fixes the "blip" problem on Windows)
            if (running && choice != 0) pause();
        }
        sc.close();
    }

    // ══════════════════════════════════════════════════════════════════
    // SAMPLE DATA — Pre-loaded so ALL options work on first run
    // ══════════════════════════════════════════════════════════════════
    private static void loadSampleData() {
        addSample(1, "Monthly Salary",   85000, 0,     "Salary",     "2026-03-01");
        addSample(2, "Rent Payment",     0,     18000, "Housing",    "2026-03-02");
        addSample(3, "Grocery Shopping", 0,     4500,  "Food",       "2026-03-05");
        addSample(4, "Freelance Work",   25000, 0,     "Salary",     "2026-03-07");
        addSample(5, "Uber Rides",       0,     1200,  "Transport",  "2026-03-08");
        addSample(6, "Amazon Shopping",  0,     6800,  "Shopping",   "2026-03-10");
        addSample(7, "Restaurant Lunch", 0,     2200,  "Food",       "2026-03-11");
        addSample(8, "Stock Dividend",   12000, 0,     "Investment", "2026-03-12");
        System.out.println("  8 sample transactions loaded. All 22 options ready!\n");
    }

    private static void addSample(int id, String name, double inc,
                                   double exp, String cat, String dateStr) {
        Transaction t = new Transaction(id, name, inc, exp, cat,
                LocalDate.parse(dateStr, Transaction.DATE_FMT));
        if (id >= nextId) nextId = id + 1;
        registerTransaction(t);
    }

    // ══════════════════════════════════════════════════════════════════
    // REGISTER TRANSACTION — adds to ALL data structures at once
    // ══════════════════════════════════════════════════════════════════
    private static void registerTransaction(Transaction t) {
        transactions.add(t);              // ArrayList  : O(1) amortised
        undoStack.push(t);                // Stack      : O(1) push
        txQueue.offer(t);                 // Queue      : O(1) enqueue at rear
        txLinkedList.addLast(t);          // LinkedList : O(1) append to tail
        expenseHeap.offer(t);             // MaxHeap    : O(log n) insert
        bst.insert(t);                    // BST        : O(log n) average
        categoryMap.merge(t.category, t.expense, Double::sum); // HashMap O(1)
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 1 — ADD TRANSACTION
    // ══════════════════════════════════════════════════════════════════
    private static void addTransaction() {
        System.out.println("  --- Add New Transaction ---");
        String    name = readStr ("  Name          : ");
        double    inc  = readDbl ("  Income    ($) : ");
        double    exp  = readDbl ("  Expense   ($) : ");
        String    cat  = readStr ("  Category      : ");
        LocalDate date = readDate("  Date (yyyy-MM-dd) [Enter = today] : ");

        Transaction t = new Transaction(nextId++, name, inc, exp, cat, date);
        registerTransaction(t);

        System.out.println();
        System.out.println("  Transaction added successfully!");
        System.out.println("  " + t);
        System.out.println();

        // ── Show which DSA operations just ran ────────────────────
        System.out.println("  ┌─────────────────────────────────────────────────┐");
        System.out.println("  │       DSA OPERATIONS PERFORMED                  │");
        System.out.println("  ├──────────────────┬──────────────────────────────┤");
        System.out.printf ("  │ ArrayList        │ add() at index %-3d → O(1)   │%n", transactions.size()-1);
        System.out.println("  │ Stack            │ push() at TOP      → O(1)   │");
        System.out.println("  │ LinkedList/Queue │ addLast()/offer()  → O(1)   │");
        System.out.println("  │ HashMap          │ merge() category   → O(1)   │");
        System.out.println("  │ PriorityQueue    │ offer() into heap  → O(log n)│");
        System.out.println("  │ BST              │ insert() by ID     → O(log n)│");
        System.out.println("  └──────────────────┴──────────────────────────────┘");
        System.out.println("  Stack TOP  : " + undoStack.peek().name);
        System.out.println("  Queue FRONT: " + ((LinkedList<Transaction>)txQueue).peekFirst().name);
        System.out.printf ("  Heap ROOT  : $%,.2f (max expense so far)%n",
                expenseHeap.peek().expense);

        checkBudget();
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 2 — SHOW ALL TRANSACTIONS
    // Iterates ArrayList from index 0 to n-1 → O(n)
    // ══════════════════════════════════════════════════════════════════
    private static void showAll() {
        line('=', 95);
        System.out.printf("  ALL TRANSACTIONS  (%d total)  [ArrayList — O(n) traversal]%n",
                transactions.size());
        line('=', 95);
        if (transactions.isEmpty()) {
            System.out.println("  No transactions yet. Use option 1 to add one.");
        } else {
            for (Transaction t : transactions) {
                System.out.println("  " + t);
            }
        }
        line('=', 95);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 3 — TOTAL BALANCE
    // Single O(n) pass through ArrayList
    // ══════════════════════════════════════════════════════════════════
    private static void showBalance() {
        double totalInc = 0, totalExp = 0;
        for (Transaction t : transactions) {
            totalInc += t.income;
            totalExp += t.expense;
        }
        double balance = totalInc - totalExp;

        line('=', 48);
        System.out.println("          FINANCIAL SUMMARY");
        line('=', 48);
        System.out.printf("  Total Income  :  $%,14.2f%n", totalInc);
        System.out.printf("  Total Expense :  $%,14.2f%n", totalExp);
        line('-', 48);
        System.out.printf("  Net Balance   :  $%,14.2f%n", balance);
        if (monthlyBudget > 0) {
            line('-', 48);
            System.out.printf("  Monthly Budget:  $%,14.2f%n", monthlyBudget);
            System.out.printf("  Budget Used   :  %.1f%%%n",
                    (totalExp / monthlyBudget) * 100);
            if (totalExp > monthlyBudget)
                System.out.println("  *** WARNING: Budget Exceeded! ***");
        }
        line('=', 48);
        if (transactions.isEmpty())
            System.out.println("  (Add transactions with option 1)");
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 4 — SET MONTHLY BUDGET
    // ══════════════════════════════════════════════════════════════════
    private static void setBudget() {
        System.out.printf("  Current budget: $%.2f%n", monthlyBudget);
        monthlyBudget = readDbl("  Enter new monthly budget ($): ");
        System.out.printf("  Budget set to $%.2f%n", monthlyBudget);
        checkBudget();
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 5 — DELETE TRANSACTION
    // ArrayList remove → O(n) due to shifting
    // BST delete       → O(log n) average
    // ══════════════════════════════════════════════════════════════════
    private static void deleteTransaction() {
        if (transactions.isEmpty()) {
            System.out.println("  No transactions to delete."); return;
        }
        showAll();
        int id = readInt("  Enter ID to delete: ");
        int idx = findById(id);
        if (idx < 0) { System.out.println("  ID " + id + " not found."); return; }

        Transaction removed = transactions.remove(idx);  // ArrayList O(n)
        txLinkedList.removeIf(t -> t.id == id);          // LinkedList O(n)
        bst.delete(id);                                   // BST O(log n) avg
        rebuildHeap();
        rebuildCategoryMap();

        System.out.println("  Deleted: " + removed);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 6 — EDIT TRANSACTION
    // ══════════════════════════════════════════════════════════════════
    private static void editTransaction() {
        if (transactions.isEmpty()) {
            System.out.println("  No transactions to edit."); return;
        }
        showAll();
        int id = readInt("  Enter ID to edit: ");
        int idx = findById(id);
        if (idx < 0) { System.out.println("  ID not found."); return; }

        Transaction t = transactions.get(idx);
        System.out.println("  Current: " + t);
        System.out.println("  (Press Enter to keep current value)");

        String nm = readStrOpt("  Name     [" + t.name     + "]: ");
        if (!nm.isEmpty()) t.name = nm;

        String is = readStrOpt("  Income   [" + t.income   + "]: ");
        if (!is.isEmpty()) try { t.income = Double.parseDouble(is); }
                           catch (Exception e) { System.out.println("  Invalid — kept."); }

        String es = readStrOpt("  Expense  [" + t.expense  + "]: ");
        if (!es.isEmpty()) try { t.expense = Double.parseDouble(es); }
                           catch (Exception e) { System.out.println("  Invalid — kept."); }

        String ct = readStrOpt("  Category [" + t.category + "]: ");
        if (!ct.isEmpty()) t.category = ct;

        String ds = readStrOpt("  Date     [" + t.date     + "]: ");
        if (!ds.isEmpty()) try {
            t.date = LocalDate.parse(ds, Transaction.DATE_FMT);
        } catch (Exception e) { System.out.println("  Invalid date — kept."); }

        // Rebuild structures that depend on field values
        rebuildHeap();
        rebuildBST();
        rebuildCategoryMap();

        System.out.println("  Updated: " + t);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 7 — LINEAR SEARCH
    // ══════════════════════════════════════════════════════════════════
    // ┌──────────────────────────────────────────────────────────────┐
    // │  ALGORITHM : Linear Search (Sequential Search)              │
    // │  STRATEGY  : Start from index 0 and check every element     │
    // │              one by one until the target is found or the    │
    // │              list ends. No pre-sorting needed.              │
    // │                                                              │
    // │  STEP BY STEP:                                              │
    // │    for i = 0 to n-1:                                        │
    // │       if list[i] matches keyword → FOUND, print it         │
    // │       else → move to next element                           │
    // │                                                              │
    // │  TIME COMPLEXITY:                                            │
    // │    Best Case  → O(1)   target is at index 0                 │
    // │    Average    → O(n/2) target is somewhere in middle        │
    // │    Worst Case → O(n)   target is at last index or not found │
    // │                                                              │
    // │  SPACE COMPLEXITY: O(1) — no extra memory used              │
    // │                                                              │
    // │  WHEN TO USE:                                               │
    // │    ✓ List is UNSORTED                                       │
    // │    ✓ List is small                                          │
    // │    ✓ Searching by a non-key field (like name, category)     │
    // └──────────────────────────────────────────────────────────────┘
    private static void linearSearch() {
        if (transactions.isEmpty()) {
            System.out.println("  No transactions to search."); return;
        }
        String kw = readStr("  Enter keyword (name or category): ").toLowerCase();

        line('=', 70);
        System.out.println("  LINEAR SEARCH — O(n)");
        System.out.println("  Keyword: \"" + kw + "\"");
        System.out.println("  Scanning all " + transactions.size() + " records one by one...");
        line('-', 70);

        boolean found = false;
        for (int i = 0; i < transactions.size(); i++) {
            // ── Check every single element — this is what makes it O(n) ──
            Transaction t = transactions.get(i);
            System.out.printf("  Checking index [%d]: %-20s ... ", i, t.name);

            if (t.name.toLowerCase().contains(kw) ||
                t.category.toLowerCase().contains(kw)) {
                System.out.println("MATCH FOUND!");
                System.out.println("  >> " + t);
                found = true;
            } else {
                System.out.println("no match");
            }
        }

        line('-', 70);
        if (!found)
            System.out.println("  No match found for \"" + kw + "\".");
        else
            System.out.println("  Search complete. Every element was checked — O(n).");
        line('=', 70);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 8 — BINARY SEARCH
    // ══════════════════════════════════════════════════════════════════
    // ┌──────────────────────────────────────────────────────────────┐
    // │  ALGORITHM : Binary Search (Divide and Conquer)             │
    // │  PRE-CONDITION : List MUST be SORTED by the search key.     │
    // │                  We sort a copy by ID before searching.     │
    // │                                                              │
    // │  STRATEGY:                                                   │
    // │    1. Look at the MIDDLE element.                           │
    // │    2. If middle == target → FOUND!                          │
    // │    3. If middle  < target → target is in RIGHT half         │
    // │                             set low = mid + 1              │
    // │    4. If middle  > target → target is in LEFT half          │
    // │                             set high = mid - 1             │
    // │    5. Repeat with the new half until found or low > high.  │
    // │                                                              │
    // │  EXAMPLE: Search ID=6 in [1,2,3,4,5,6,7,8]                │
    // │    Step 1: mid=4 (ID=4). 6>4 → go RIGHT [5,6,7,8]         │
    // │    Step 2: mid=6 (ID=6). 6=6 → FOUND in 2 steps!          │
    // │    Without binary search: would need 6 comparisons         │
    // │                                                              │
    // │  TIME COMPLEXITY:                                            │
    // │    Best Case  → O(1)      target is the middle element      │
    // │    Average    → O(log n)  each step halves the search space │
    // │    Worst Case → O(log n)  target not found                  │
    // │                                                              │
    // │  SPACE COMPLEXITY: O(1) — only 3 variables (low,mid,high)  │
    // │                                                              │
    // │  WHEN TO USE:                                               │
    // │    ✓ List is SORTED                                         │
    // │    ✓ Random access is O(1) (arrays/ArrayLists)             │
    // │    ✗ Do NOT use on LinkedList (get(i) is O(n))             │
    // └──────────────────────────────────────────────────────────────┘
    private static void binarySearch() {
        if (transactions.isEmpty()) {
            System.out.println("  No transactions to search."); return;
        }
        int id = readInt("  Enter Transaction ID to find: ");

        // Step 1: Sort a copy by ID (prerequisite for binary search)
        ArrayList<Transaction> sorted = new ArrayList<>(transactions);
        sorted.sort(Comparator.comparingInt(t -> t.id));

        line('=', 70);
        System.out.println("  BINARY SEARCH — O(log n)");
        System.out.println("  Searching for ID = " + id);
        System.out.println("  List size = " + sorted.size()
                + "  |  Max steps needed = " + (int)(Math.log(sorted.size())/Math.log(2) + 1));
        line('-', 70);

        int low = 0, high = sorted.size() - 1, steps = 0;

        while (low <= high) {
            steps++;
            // ── Calculate mid (this avoids integer overflow) ──────
            int mid   = low + (high - low) / 2;
            int midId = sorted.get(mid).id;

            System.out.printf("  Step %d: low=%-2d high=%-2d mid=%-2d (ID=%d) → ",
                    steps, low, high, mid, midId);

            if (midId == id) {
                System.out.println("FOUND!");
                System.out.println();
                System.out.println("  >> " + sorted.get(mid));
                System.out.println("  Found in " + steps + " step(s)  [O(log n)]");
                line('=', 70);
                return;
            } else if (midId < id) {
                // ── Target is in the RIGHT half — discard left ────
                System.out.println("target > mid → search RIGHT half");
                low = mid + 1;
            } else {
                // ── Target is in the LEFT half — discard right ────
                System.out.println("target < mid → search LEFT half");
                high = mid - 1;
            }
        }

        System.out.println("  NOT FOUND after " + steps + " step(s).");
        line('=', 70);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 9 — BUBBLE SORT  (by Income, Descending)
    // ══════════════════════════════════════════════════════════════════
    // ┌──────────────────────────────────────────────────────────────┐
    // │  ALGORITHM : Bubble Sort                                    │
    // │                                                              │
    // │  STRATEGY:                                                   │
    // │    Compare adjacent elements and SWAP if out of order.      │
    // │    Repeat for n-1 passes. After each pass, the largest      │
    // │    unsorted element "bubbles up" to its correct position.   │
    // │                                                              │
    // │  EXAMPLE: Sort [3, 1, 4, 2] ascending                       │
    // │    Pass 1: [1,3,4,2] → [1,3,2,4]  (4 bubbled to end)       │
    // │    Pass 2: [1,2,3,4]              (3 bubbled to position)   │
    // │    Pass 3: no swaps → DONE early  (optimised exit)          │
    // │                                                              │
    // │  OPTIMISATION: if no swaps happen in a pass, the list is    │
    // │    already sorted → break early → best case O(n)            │
    // │                                                              │
    // │  TIME COMPLEXITY:                                            │
    // │    Best Case  → O(n)   already sorted (with swapped flag)   │
    // │    Average    → O(n²)                                       │
    // │    Worst Case → O(n²)  reverse sorted                       │
    // │                                                              │
    // │  SPACE COMPLEXITY: O(1) — sorts IN-PLACE, no extra array    │
    // │                                                              │
    // │  STABLE: YES — equal elements keep their original order     │
    // └──────────────────────────────────────────────────────────────┘
    private static void bubbleSortIncome() {
        if (transactions.isEmpty()) {
            System.out.println("  No transactions to sort."); return;
        }
        ArrayList<Transaction> arr = new ArrayList<>(transactions);
        int n = arr.size();
        int totalSwaps = 0;

        line('=', 75);
        System.out.println("  BUBBLE SORT — O(n²)  |  Sorting by Income (High → Low)");
        System.out.println("  Total elements: " + n);
        line('-', 75);

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;  // optimisation flag

            for (int j = 0; j < n - 1 - i; j++) {
                // ── Compare adjacent elements ──────────────────────
                // For descending: swap if LEFT is LESS than RIGHT
                if (arr.get(j).income < arr.get(j + 1).income) {

                    // ── SWAP ──────────────────────────────────────
                    Transaction temp = arr.get(j);
                    arr.set(j,     arr.get(j + 1));
                    arr.set(j + 1, temp);

                    swapped = true;
                    totalSwaps++;
                }
            }
            System.out.printf("  Pass %d complete. Swaps this pass: %s%n",
                    i + 1, swapped ? "yes" : "none");

            // ── Early exit optimisation ────────────────────────────
            // If no swaps happened, list is already fully sorted
            if (!swapped) {
                System.out.println("  No swaps in this pass → list is sorted! Exiting early.");
                break;
            }
        }

        line('-', 75);
        System.out.println("  BUBBLE SORT COMPLETE. Total swaps: " + totalSwaps);
        line('-', 75);
        for (Transaction t : arr) System.out.println("  " + t);
        line('=', 75);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 10 — INSERTION SORT  (by Expense, Descending)
    // ══════════════════════════════════════════════════════════════════
    // ┌──────────────────────────────────────────────────────────────┐
    // │  ALGORITHM : Insertion Sort                                 │
    // │                                                              │
    // │  STRATEGY:                                                   │
    // │    Build a sorted portion on the left, one element at a     │
    // │    time. Pick the next unsorted element (KEY) and insert    │
    // │    it into the correct position in the sorted portion by    │
    // │    shifting larger elements one position to the right.      │
    // │    Like sorting playing cards in your hand.                 │
    // │                                                              │
    // │  EXAMPLE: Sort [4,2,5,1] descending                         │
    // │    i=1: key=2. Sorted=[4]. 4>2 → shift. Insert: [4,2,5,1] │
    // │    i=2: key=5. Sorted=[4,2]. 4<5→ shift, 2<5→ shift.       │
    // │           Insert at front: [5,4,2,1]                        │
    // │    i=3: key=1. All bigger → stays at end: [5,4,2,1] ✓      │
    // │                                                              │
    // │  TIME COMPLEXITY:                                            │
    // │    Best Case  → O(n)   already sorted (no shifts needed)    │
    // │    Average    → O(n²)                                       │
    // │    Worst Case → O(n²)  reverse sorted (max shifts)         │
    // │                                                              │
    // │  SPACE COMPLEXITY: O(1) — sorts IN-PLACE                   │
    // │                                                              │
    // │  STABLE: YES                                                 │
    // │                                                              │
    // │  ADVANTAGE over Bubble Sort:                                 │
    // │    Fewer swaps — shifts are cheaper than swaps              │
    // │    Very fast on nearly sorted data                          │
    // └──────────────────────────────────────────────────────────────┘
    private static void insertionSortExpense() {
        if (transactions.isEmpty()) {
            System.out.println("  No transactions to sort."); return;
        }
        ArrayList<Transaction> arr = new ArrayList<>(transactions);
        int n = arr.size();

        line('=', 75);
        System.out.println("  INSERTION SORT — O(n²)  |  Sorting by Expense (High → Low)");
        System.out.println("  Total elements: " + n);
        line('-', 75);

        for (int i = 1; i < n; i++) {
            // ── KEY: the element being inserted into sorted portion ──
            Transaction key = arr.get(i);
            int j = i - 1;
            int shifts = 0;

            // ── Shift elements that are smaller than key rightward ──
            // (smaller expense means it should come AFTER key in desc order)
            while (j >= 0 && arr.get(j).expense < key.expense) {
                arr.set(j + 1, arr.get(j));  // shift right
                j--;
                shifts++;
            }

            // ── Insert key at its correct sorted position ──────────
            arr.set(j + 1, key);

            System.out.printf("  i=%d  key=[%s  $%.0f]  shifts=%d  sorted portion size=%d%n",
                    i,
                    key.name.length() > 15 ? key.name.substring(0, 14) + "." : key.name,
                    key.expense, shifts, i + 1);
        }

        line('-', 75);
        System.out.println("  INSERTION SORT COMPLETE.");
        line('-', 75);
        for (Transaction t : arr) System.out.println("  " + t);
        line('=', 75);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 11 — MERGE SORT  (by Date, Ascending)
    // ══════════════════════════════════════════════════════════════════
    // ┌──────────────────────────────────────────────────────────────┐
    // │  ALGORITHM : Merge Sort (Divide and Conquer)                │
    // │                                                              │
    // │  STRATEGY — 3 phases:                                        │
    // │    1. DIVIDE  : Split list in half recursively              │
    // │                 until each piece has only 1 element.        │
    // │    2. CONQUER : A single element is always sorted.          │
    // │    3. MERGE   : Combine two sorted halves into one sorted   │
    // │                 list by always picking the smaller element. │
    // │                                                              │
    // │  EXAMPLE: Sort [4,1,3,2] by date (simplified as numbers)   │
    // │    DIVIDE:  [4,1,3,2] → [4,1] + [3,2]                      │
    // │             [4,1] → [4] + [1]                               │
    // │             [3,2] → [3] + [2]                               │
    // │    MERGE:   [4]+[1] → [1,4]                                 │
    // │             [3]+[2] → [2,3]                                 │
    // │             [1,4]+[2,3] → [1,2,3,4] ✓                      │
    // │                                                              │
    // │  TIME COMPLEXITY:                                            │
    // │    Best Case  → O(n log n)  always the same!                │
    // │    Average    → O(n log n)                                  │
    // │    Worst Case → O(n log n)  ← consistent, unlike O(n²)     │
    // │                                                              │
    // │  SPACE COMPLEXITY: O(n) — needs temp arrays during merge    │
    // │                                                              │
    // │  STABLE: YES                                                 │
    // │                                                              │
    // │  WHY BETTER than Bubble/Insertion for large data?           │
    // │    n=1000: Bubble=1,000,000 ops. MergeSort≈10,000 ops       │
    // └──────────────────────────────────────────────────────────────┘
    private static void mergeSortDate() {
        if (transactions.isEmpty()) {
            System.out.println("  No transactions to sort."); return;
        }
        ArrayList<Transaction> arr = new ArrayList<>(transactions);

        line('=', 75);
        System.out.println("  MERGE SORT — O(n log n)  |  Sorting by Date (Oldest → Newest)");
        System.out.println("  Total elements: " + arr.size()
                + "  |  Approx steps: " + (int)(arr.size() * (Math.log(arr.size())/Math.log(2))));
        line('-', 75);

        mergeSort(arr, 0, arr.size() - 1);

        line('-', 75);
        System.out.println("  MERGE SORT COMPLETE.");
        line('-', 75);
        for (Transaction t : arr) System.out.println("  " + t);
        line('=', 75);
    }

    // ── Recursive split ────────────────────────────────────────────
    private static void mergeSort(ArrayList<Transaction> arr, int left, int right) {
        if (left >= right) return;  // base case: 1 element = already sorted

        int mid = left + (right - left) / 2;  // find midpoint safely

        mergeSort(arr, left, mid);              // recursively sort LEFT half
        mergeSort(arr, mid + 1, right);         // recursively sort RIGHT half
        merge(arr, left, mid, right);           // merge the two sorted halves
    }

    // ── Merge two sorted halves [left..mid] and [mid+1..right] ────
    private static void merge(ArrayList<Transaction> arr,
                               int left, int mid, int right) {
        // Copy both halves into temporary lists
        ArrayList<Transaction> L = new ArrayList<>(arr.subList(left, mid + 1));
        ArrayList<Transaction> R = new ArrayList<>(arr.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;

        // Pick the smaller date element from L or R each time
        while (i < L.size() && j < R.size()) {
            if (!L.get(i).date.isAfter(R.get(j).date)) {
                arr.set(k++, L.get(i++));  // L element is earlier or equal
            } else {
                arr.set(k++, R.get(j++));  // R element is earlier
            }
        }
        // Copy remaining elements (one side will still have leftovers)
        while (i < L.size())  arr.set(k++, L.get(i++));
        while (j < R.size())  arr.set(k++, R.get(j++));
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 12 — CATEGORY ANALYSIS  (HashMap)
    // ══════════════════════════════════════════════════════════════════
    // ┌──────────────────────────────────────────────────────────────┐
    // │  DATA STRUCTURE : HashMap<String, Double>                   │
    // │                                                              │
    // │  INTERNAL WORKING:                                           │
    // │    1. You call: map.put("Food", 4500.00)                    │
    // │    2. Java runs: "Food".hashCode() → e.g. 2301504           │
    // │    3. Maps to bucket: 2301504 % tableSize → bucket index    │
    // │    4. Stores value 4500.00 in that bucket                   │
    // │    5. Later: map.get("Food") → same hash → same bucket      │
    // │                              → retrieves 4500.00 in O(1)   │
    // │                                                              │
    // │  merge() used here:                                          │
    // │    map.merge(key, value, Double::sum)                       │
    // │    = if key exists: existing + value                        │
    // │    = if key new   : just store value                        │
    // │                                                              │
    // │  TIME COMPLEXITY:                                            │
    // │    put()        → O(1) average                              │
    // │    get()        → O(1) average                              │
    // │    merge()      → O(1) average                              │
    // │    entrySet()   → O(n) to iterate all pairs                 │
    // └──────────────────────────────────────────────────────────────┘
    private static void categoryAnalysis() {
        rebuildCategoryMap();
        line('=', 60);
        System.out.println("  CATEGORY EXPENSE ANALYSIS  [HashMap<String,Double>]");
        System.out.println("  Key = Category name  |  Value = Total expense");
        System.out.println("  put/get/merge → O(1) average");
        line('=', 60);
        if (categoryMap.isEmpty()) {
            System.out.println("  No expense data yet.");
        } else {
            System.out.println("  How your data is stored in the HashMap:");
            System.out.println("  ┌──────────────────┬──────────────┬──────────────┐");
            System.out.println("  │ KEY (Category)   │ hashCode()   │ VALUE ($)    │");
            System.out.println("  ├──────────────────┼──────────────┼──────────────┤");
            categoryMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(e -> System.out.printf(
                    "  │ %-16s │ %-12d │ %,12.2f │%n",
                    e.getKey(),
                    Math.abs(e.getKey().hashCode() % 9999),
                    e.getValue()));
            System.out.println("  └──────────────────┴──────────────┴──────────────┘");
            System.out.printf("%n  map.get(\"Food\") → $%,.2f  (O(1) lookup!)%n",
                    categoryMap.getOrDefault("Food", 0.0));
            System.out.println("  Total categories: " + categoryMap.size());
        }
        line('=', 60);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 13 — UNDO LAST TRANSACTION  (Stack)
    // ══════════════════════════════════════════════════════════════════
    // ┌──────────────────────────────────────────────────────────────┐
    // │  DATA STRUCTURE : Stack (LIFO — Last In First Out)          │
    // │                                                              │
    // │  HOW UNDO WORKS:                                            │
    // │    Every time a transaction is ADDED → push() onto stack   │
    // │    When UNDO is called → pop() from stack → remove it      │
    // │                                                              │
    // │  OPERATIONS:                                                 │
    // │    push(t) → O(1)  places t on TOP of stack                │
    // │    pop()   → O(1)  removes TOP element and returns it      │
    // │    peek()  → O(1)  reads TOP without removing              │
    // └──────────────────────────────────────────────────────────────┘
    private static void undoLast() {
        if (undoStack.isEmpty()) {
            System.out.println("  Nothing to undo."); return;
        }
        line('=', 60);
        System.out.println("  UNDO — STACK (LIFO) in action");
        line('-', 60);
        System.out.println("  Stack before UNDO:");
        System.out.println("  Stack size: " + undoStack.size());
        System.out.println("  TOP element (will be removed): " + undoStack.peek().name);
        System.out.println();

        Transaction last = undoStack.pop();  // O(1) LIFO pop
        transactions.removeIf(t -> t.id == last.id);
        txLinkedList.removeIf(t -> t.id == last.id);
        bst.delete(last.id);
        rebuildHeap();
        rebuildCategoryMap();

        System.out.println("  [Stack] pop() executed → O(1)");
        System.out.println("  Removed: " + last);
        System.out.println();
        System.out.println("  Stack after UNDO:");
        System.out.println("  Stack size: " + undoStack.size());
        System.out.println("  New TOP: " + (undoStack.isEmpty() ? "(stack is empty)" : undoStack.peek().name));
        line('=', 60);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 14 — HIGHEST EXPENSE  (PriorityQueue / Max-Heap)
    // ══════════════════════════════════════════════════════════════════
    // ┌──────────────────────────────────────────────────────────────┐
    // │  DATA STRUCTURE : PriorityQueue — Max-Heap                  │
    // │                                                              │
    // │  HEAP PROPERTY:                                             │
    // │    Parent node expense ALWAYS >= children's expense         │
    // │    ROOT is always the MAXIMUM element                       │
    // │                                                              │
    // │  INTERNAL ARRAY representation:                             │
    // │    Stored as array where:                                   │
    // │      parent of i = (i-1)/2                                  │
    // │      children of i = 2i+1 and 2i+2                         │
    // │                                                              │
    // │  OPERATIONS:                                                 │
    // │    offer(t)   → O(log n)  insert and "bubble up"           │
    // │    peek()     → O(1)      return root (max) instantly      │
    // │    poll()     → O(log n)  remove root and "sink down"      │
    // └──────────────────────────────────────────────────────────────┘
    private static void highestExpense() {
        rebuildHeap();
        line('=', 65);
        System.out.println("  HIGHEST EXPENSE — PRIORITY QUEUE (MAX-HEAP)");
        System.out.println("  peek() returns ROOT = MAX element in O(1)");
        line('=', 65);
        if (expenseHeap.isEmpty()) {
            System.out.println("  No expense data."); return;
        }
        System.out.println("  Heap contents ranked by expense (highest first):");
        System.out.println("  ┌──────┬───────────────────────┬──────────────────┐");
        System.out.println("  │ Rank │ Transaction Name      │ Expense          │");
        System.out.println("  ├──────┼───────────────────────┼──────────────────┤");

        PriorityQueue<Transaction> temp = new PriorityQueue<>(expenseHeap);
        int rank = 1;
        while (!temp.isEmpty() && rank <= 8) {
            Transaction t = temp.poll();
            String label = rank == 1 ? " ← MAX (HEAP ROOT)" : "";
            System.out.printf("  │  #%-2d │ %-21s │ $%,14.2f │%s%n",
                    rank,
                    t.name.length() > 21 ? t.name.substring(0, 20) + "." : t.name,
                    t.expense, label);
            rank++;
        }
        System.out.println("  └──────┴───────────────────────┴──────────────────┘");
        System.out.println();
        System.out.printf("  peek() result  → %s%n", expenseHeap.peek());
        System.out.printf("  Max expense    → $%,.2f  (O(1) retrieval from heap root)%n",
                expenseHeap.peek().expense);
        line('=', 65);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 15 — ASCII FINANCIAL GRAPH
    // ══════════════════════════════════════════════════════════════════
    private static void asciiFinancialGraph() {
        double totalInc = 0, totalExp = 0;
        for (Transaction t : transactions) {
            totalInc += t.income;
            totalExp += t.expense;
        }
        double max = Math.max(totalInc, Math.max(totalExp, 1));

        line('=', 65);
        System.out.println("  ASCII FINANCIAL OVERVIEW GRAPH");
        line('=', 65);
        printBar("Income ", totalInc, max);
        printBar("Expense", totalExp, max);
        line('-', 65);
        System.out.printf("  Total Income  : $%,.2f%n", totalInc);
        System.out.printf("  Total Expense : $%,.2f%n", totalExp);
        System.out.printf("  Net Balance   : $%,.2f%n", totalInc - totalExp);
        line('=', 65);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 16 — ASCII CATEGORY GRAPH  (HashMap)
    // ══════════════════════════════════════════════════════════════════
    private static void asciiCategoryGraph() {
        rebuildCategoryMap();
        line('=', 65);
        System.out.println("  CATEGORY EXPENSE GRAPH  [HashMap<String,Double>]");
        line('=', 65);
        if (categoryMap.isEmpty()) {
            System.out.println("  No expense data yet."); return;
        }
        double max = categoryMap.values().stream()
                .mapToDouble(Double::doubleValue).max().orElse(1);
        categoryMap.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(e -> printBar(pad(e.getKey(), 12), e.getValue(), max));
        line('=', 65);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 17 — SAVE DATA
    // ══════════════════════════════════════════════════════════════════
    private static void saveData() {
        FileManager.save(transactions);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 18 — RELOAD DATA FROM FILE
    // ══════════════════════════════════════════════════════════════════
    private static void reloadData() {
        List<Transaction> loaded = FileManager.load();
        if (loaded.isEmpty()) {
            System.out.println("  No saved file found. Current data unchanged.");
            return;
        }
        transactions.clear();
        undoStack.clear();
        txQueue.clear();
        txLinkedList.clear();
        expenseHeap.clear();
        bst = new BST();
        categoryMap.clear();
        nextId = 1;

        for (Transaction t : loaded) {
            registerTransaction(t);
            if (t.id >= nextId) nextId = t.id + 1;
        }
        System.out.println("  Reloaded " + transactions.size() + " transaction(s).");
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 19 — MONTHLY REPORT
    // ══════════════════════════════════════════════════════════════════
    private static void monthlyReport() {
        int month = readInt("  Month (1-12): ");
        int year  = readInt("  Year  (e.g. 2026): ");
        if (month < 1 || month > 12) { System.out.println("  Invalid month."); return; }

        double inc = 0, exp = 0;
        Transaction maxT = null;
        Map<String, Double> cm = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.date.getMonthValue() != month || t.date.getYear() != year) continue;
            inc += t.income;
            exp += t.expense;
            cm.merge(t.category, t.expense, Double::sum);
            if (maxT == null || t.expense > maxT.expense) maxT = t;
        }

        String topCat = cm.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey).orElse("N/A");

        line('=', 55);
        System.out.printf("  MONTHLY REPORT — %s %d%n", Month.of(month).name(), year);
        line('=', 55);
        System.out.printf("  Total Income   : $%,.2f%n", inc);
        System.out.printf("  Total Expense  : $%,.2f%n", exp);
        System.out.printf("  Net Balance    : $%,.2f%n", inc - exp);
        System.out.printf("  Top Category   : %s%n", topCat);
        if (maxT != null)
            System.out.printf("  Highest Expense: %s ($%.2f)%n", maxT.name, maxT.expense);
        else
            System.out.println("  No transactions found for this period.");
        line('=', 55);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 20 — BST TRAVERSAL
    // ══════════════════════════════════════════════════════════════════
    private static void bstTraversal() {
        if (bst.isEmpty()) {
            System.out.println("  BST is empty."); return;
        }
        line('=', 65);
        System.out.println("  BINARY SEARCH TREE TRAVERSAL");
        System.out.println("  All traversals visit every node exactly once → O(n)");
        line('-', 65);
        System.out.println("  1. Inorder   [L→Root→R] → sorted ascending by ID");
        System.out.println("  2. Preorder  [Root→L→R] → root visited first");
        System.out.println("  3. Postorder [L→R→Root] → root visited last");
        line('-', 65);
        int ch = readInt("  Choice (1/2/3): ");
        System.out.println();
        switch (ch) {
            case 1 -> bst.inorder();
            case 2 -> bst.preorder();
            case 3 -> bst.postorder();
            default -> System.out.println("  Enter 1, 2 or 3.");
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 21 — LINKED LIST FULL DEMO
    // ══════════════════════════════════════════════════════════════════
    // ┌──────────────────────────────────────────────────────────────┐
    // │  DATA STRUCTURE : LinkedList                                │
    // │                                                              │
    // │  DEFINITION:                                                 │
    // │    A sequence of NODE objects connected by pointers.        │
    // │    Each node contains:                                      │
    // │      [ DATA  |  NEXT* ] → [ DATA  |  NEXT* ] → NULL        │
    // │                                                              │
    // │  Unlike ArrayList (stored in contiguous memory),            │
    // │  LinkedList nodes can be scattered in memory — they are     │
    // │  connected ONLY through the NEXT pointer.                   │
    // │                                                              │
    // │  TYPES:                                                      │
    // │    Singly Linked List : each node has ONE pointer (next)    │
    // │    Doubly Linked List : each node has TWO (prev + next)     │
    // │    Java's LinkedList  : doubly linked                       │
    // │                                                              │
    // │  OPERATIONS:                                                 │
    // │    addFirst(e)    → O(1)  update HEAD pointer only          │
    // │    addLast(e)     → O(1)  update TAIL pointer only          │
    // │    removeFirst()  → O(1)  update HEAD pointer               │
    // │    get(index)     → O(n)  must walk from HEAD               │
    // │    search         → O(n)  must scan node by node            │
    // │    insert at mid  → O(n)  to find position + O(1) to link  │
    // └──────────────────────────────────────────────────────────────┘
    private static void linkedListDemo() {
        line('=', 70);
        System.out.println("  LINKED LIST — FULL LIVE DEMONSTRATION");
        line('=', 70);
        System.out.println("  STRUCTURE: HEAD → [data|next*] → [data|next*] → NULL");
        System.out.println("  Java uses: LinkedList<Transaction> txLinkedList");
        System.out.println("  Current size: " + txLinkedList.size() + " nodes");
        System.out.println();

        // ── STEP 1: Show current chain ─────────────────────────────
        line('-', 70);
        System.out.println("  STEP 1: CURRENT LINKED LIST — Traversal O(n)");
        line('-', 70);
        if (txLinkedList.isEmpty()) {
            System.out.println("  HEAD → NULL  (list is empty)");
        } else {
            // ── Visual chain ──────────────────────────────────────
            System.out.print("  HEAD");
            for (Transaction t : txLinkedList) {
                String nm = t.name.length() > 8 ? t.name.substring(0, 7) + "." : t.name;
                System.out.printf(" → [ID:%d|%s|*]", t.id, nm);
            }
            System.out.println(" → NULL");
            System.out.println();

            // ── Node table with NEXT pointers ─────────────────────
            System.out.println("  Node details (showing data + next pointer):");
            System.out.println("  ┌──────┬──────────────────────┬──────────────┬──────────────┐");
            System.out.println("  │  ID  │ Name                 │ Expense ($)  │ NEXT PTR     │");
            System.out.println("  ├──────┼──────────────────────┼──────────────┼──────────────┤");
            List<Transaction> ll = new ArrayList<>(txLinkedList);
            for (int i = 0; i < ll.size(); i++) {
                Transaction t   = ll.get(i);
                String nextPtr  = (i < ll.size() - 1) ? "→ ID:" + ll.get(i+1).id : "→ NULL";
                System.out.printf("  │ %-4d │ %-20s │ %,12.2f │ %-12s │%n",
                        t.id,
                        t.name.length() > 20 ? t.name.substring(0, 19) + "." : t.name,
                        t.expense, nextPtr);
            }
            System.out.println("  └──────┴──────────────────────┴──────────────┴──────────────┘");
        }

        // ── STEP 2: Insert at HEAD ─────────────────────────────────
        System.out.println();
        line('-', 70);
        System.out.println("  STEP 2: INSERT AT HEAD → O(1)");
        System.out.println("  Only the HEAD pointer is updated — no shifting!");
        line('-', 70);
        Transaction headNode = new Transaction(901, "HEAD-Demo Node", 0, 999, "Demo", LocalDate.now());
        System.out.println("  BEFORE: HEAD → [" + (txLinkedList.isEmpty() ? "NULL" : "ID:" + txLinkedList.getFirst().id) + "] → ...");
        txLinkedList.addFirst(headNode);  // O(1) — just redirect HEAD pointer
        System.out.println("  AFTER:  HEAD → [ID:901|HEAD-Demo Node|*] → [previous head] → ...");
        System.out.println("  New HEAD: " + txLinkedList.getFirst().name + "  (size=" + txLinkedList.size() + ")");

        // ── STEP 3: Insert at TAIL ─────────────────────────────────
        System.out.println();
        line('-', 70);
        System.out.println("  STEP 3: INSERT AT TAIL → O(1)");
        System.out.println("  Only the TAIL pointer is updated — no scanning!");
        line('-', 70);
        Transaction tailNode = new Transaction(902, "TAIL-Demo Node", 0, 888, "Demo", LocalDate.now());
        System.out.println("  BEFORE: ... → [" + txLinkedList.getLast().name + "] → NULL");
        txLinkedList.addLast(tailNode);   // O(1) — just redirect TAIL pointer
        System.out.println("  AFTER:  ... → [previous tail] → [ID:902|TAIL-Demo Node|*] → NULL");
        System.out.println("  New TAIL: " + txLinkedList.getLast().name + "  (size=" + txLinkedList.size() + ")");

        // ── STEP 4: Traversal ──────────────────────────────────────
        System.out.println();
        line('-', 70);
        System.out.println("  STEP 4: TRAVERSAL FROM HEAD TO TAIL → O(n)");
        System.out.println("  Must visit every node one by one — no shortcuts");
        line('-', 70);
        int step = 1;
        for (Transaction t : txLinkedList) {
            System.out.printf("  Visit node %2d: ID=%-3d  %-20s  Expense=$%,.2f%n",
                    step++, t.id,
                    t.name.length() > 20 ? t.name.substring(0, 19) + "." : t.name,
                    t.expense);
        }
        System.out.println("  Reached NULL. Total nodes visited: " + txLinkedList.size());

        // ── STEP 5: Search ─────────────────────────────────────────
        System.out.println();
        line('-', 70);
        System.out.println("  STEP 5: SEARCH (ID=3) → O(n)  [no index access!]");
        System.out.println("  Must scan node by node from HEAD — this is O(n)");
        line('-', 70);
        int scanCount = 0;
        boolean nodeFound = false;
        for (Transaction t : txLinkedList) {
            scanCount++;
            System.out.printf("  Checking node %d (ID=%d) ... %s%n",
                    scanCount, t.id, t.id == 3 ? "FOUND!" : "skip");
            if (t.id == 3) { nodeFound = true; break; }
        }
        if (!nodeFound) System.out.println("  Not found after scanning " + scanCount + " nodes.");
        else System.out.println("  Found after scanning " + scanCount + " node(s) out of " + txLinkedList.size());

        // ── STEP 6: Delete HEAD ────────────────────────────────────
        System.out.println();
        line('-', 70);
        System.out.println("  STEP 6: DELETE HEAD NODE → O(1)");
        System.out.println("  Just redirect HEAD pointer to next node");
        line('-', 70);
        System.out.println("  BEFORE: HEAD → [" + txLinkedList.getFirst().name + "] → [next] → ...");
        txLinkedList.removeFirst();  // O(1) — just update HEAD pointer
        System.out.println("  AFTER:  HEAD → [" + (txLinkedList.isEmpty() ? "NULL" : txLinkedList.getFirst().name) + "] → ...");
        System.out.println("  List size now: " + txLinkedList.size());

        // ── STEP 7: LinkedList vs ArrayList comparison ─────────────
        System.out.println();
        line('-', 70);
        System.out.println("  STEP 7: LINKED LIST vs ARRAY LIST — Comparison");
        line('-', 70);
        System.out.println("  Operation            LinkedList       ArrayList");
        System.out.println("  ──────────────────────────────────────────────────");
        System.out.println("  Insert at HEAD       O(1) ✓           O(n) shifts ✗");
        System.out.println("  Insert at TAIL       O(1) ✓           O(1) amortised ✓");
        System.out.println("  Delete at HEAD       O(1) ✓           O(n) shifts ✗");
        System.out.println("  Access by index      O(n) ✗           O(1) ✓");
        System.out.println("  Search               O(n) ✗           O(n) ✗");
        System.out.println("  Memory layout        scattered nodes  contiguous block");
        System.out.println("  Extra memory         YES (pointers)   NO");
        System.out.println("  ──────────────────────────────────────────────────");
        System.out.println("  In this app: LinkedList backs the Queue because");
        System.out.println("  addLast() and removeFirst() are BOTH O(1).");
        line('=', 70);
    }

    // ══════════════════════════════════════════════════════════════════
    // OPTION 22 — SHOW ALL DSA LIVE
    // ══════════════════════════════════════════════════════════════════
    private static void showAllDSA() {
        line('*', 65);
        System.out.println("  ALL DATA STRUCTURES IN ACTION — LIVE SNAPSHOT");
        line('*', 65);

        // ── ArrayList ─────────────────────────────────────────────
        System.out.println();
        line('=', 65);
        System.out.println("  [1] ARRAYLIST  — Main Storage  O(1) add/get");
        line('-', 65);
        System.out.println("  Total transactions stored: " + transactions.size());
        System.out.println("  First: " + (transactions.isEmpty() ? "empty" : transactions.get(0).name));
        System.out.println("  Last : " + (transactions.isEmpty() ? "empty" : transactions.get(transactions.size()-1).name));

        // ── Stack ─────────────────────────────────────────────────
        System.out.println();
        line('=', 65);
        System.out.println("  [2] STACK (LIFO)  — Undo Feature  O(1) push/pop");
        line('-', 65);
        System.out.println("  Stack size: " + undoStack.size());
        System.out.println("  TOP (will undo first): " + (undoStack.isEmpty() ? "empty" : undoStack.peek().name));
        List<Transaction> sl = new ArrayList<>(undoStack);
        for (int i = sl.size()-1; i >= Math.max(0, sl.size()-4); i--) {
            System.out.println("    " + (i==sl.size()-1?"TOP → ":"       ") + "[" + sl.get(i).name + "]");
        }

        // ── LinkedList / Queue ────────────────────────────────────
        System.out.println();
        line('=', 65);
        System.out.println("  [3] LINKED LIST / QUEUE (FIFO)  O(1) add/remove");
        line('-', 65);
        System.out.print("  FRONT → ");
        List<Transaction> ql = new ArrayList<>(txQueue);
        for (int i = 0; i < Math.min(ql.size(), 5); i++) {
            String nm = ql.get(i).name.length()>10 ? ql.get(i).name.substring(0,9)+"." : ql.get(i).name;
            System.out.print("[" + nm + "]");
            if (i < Math.min(ql.size(),5)-1) System.out.print(" → ");
        }
        if (ql.size() > 5) System.out.print(" → ...(" + (ql.size()-5) + " more)");
        System.out.println(" → REAR");
        System.out.println("  LinkedList chain (first 3 nodes with pointers):");
        List<Transaction> ll = new ArrayList<>(txLinkedList);
        for (int i = 0; i < Math.min(ll.size(), 3); i++) {
            String next = i < ll.size()-1 ? "→ ID:"+ll.get(i+1).id : "→ NULL";
            System.out.printf("  [ID:%d|%s|*] %s%n", ll.get(i).id,
                    ll.get(i).name.length()>12 ? ll.get(i).name.substring(0,11)+"." : ll.get(i).name,
                    next);
        }

        // ── HashMap ───────────────────────────────────────────────
        System.out.println();
        line('=', 65);
        System.out.println("  [4] HASHMAP  — Category Totals  O(1) get/put");
        line('-', 65);
        rebuildCategoryMap();
        System.out.println("  ┌──────────────────┬──────────────────┐");
        System.out.println("  │ KEY              │ VALUE ($)        │");
        System.out.println("  ├──────────────────┼──────────────────┤");
        categoryMap.forEach((k,v) -> System.out.printf("  │ %-16s │ $%,14.2f │%n", k, v));
        System.out.println("  └──────────────────┴──────────────────┘");

        // ── PriorityQueue / Heap ───────────────────────────────────
        System.out.println();
        line('=', 65);
        System.out.println("  [5] PRIORITY QUEUE (MAX-HEAP)  peek()=O(1)");
        line('-', 65);
        rebuildHeap();
        if (!expenseHeap.isEmpty()) {
            System.out.printf("  Heap ROOT (max expense): %s → $%,.2f%n",
                    expenseHeap.peek().name, expenseHeap.peek().expense);
            PriorityQueue<Transaction> tmp = new PriorityQueue<>(expenseHeap);
            int r = 1;
            while (!tmp.isEmpty() && r <= 5) {
                Transaction t = tmp.poll();
                System.out.printf("  #%d %-22s $%,.2f%s%n", r,
                        t.name.length()>22?t.name.substring(0,21)+".":t.name,
                        t.expense, r==1?" ← ROOT (MAX)":"");
                r++;
            }
        }

        // ── BST ───────────────────────────────────────────────────
        System.out.println();
        line('=', 65);
        System.out.println("  [6] BINARY SEARCH TREE (BST)  O(log n) search");
        line('-', 65);
        if (!bst.isEmpty()) {
            System.out.println("  Inorder traversal (= sorted by ID):");
            bst.inorder();
        }

        line('*', 65);
        System.out.println("  ALL 6 DATA STRUCTURES SHOWN SUCCESSFULLY!");
        line('*', 65);
    }

    // ══════════════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════════════

    private static void checkBudget() {
        if (monthlyBudget <= 0) return;
        double totalExp = transactions.stream().mapToDouble(t -> t.expense).sum();
        if (totalExp > monthlyBudget)
            System.out.printf("  *** WARNING: Budget Exceeded! Spent $%.2f of $%.2f ***%n",
                    totalExp, monthlyBudget);
    }

    private static void rebuildHeap() {
        expenseHeap.clear();
        expenseHeap.addAll(transactions);
    }

    private static void rebuildBST() {
        bst = new BST();
        for (Transaction t : transactions) bst.insert(t);
    }

    private static void rebuildCategoryMap() {
        categoryMap.clear();
        for (Transaction t : transactions)
            categoryMap.merge(t.category, t.expense, Double::sum);
    }

    private static int findById(int id) {
        for (int i = 0; i < transactions.size(); i++)
            if (transactions.get(i).id == id) return i;
        return -1;
    }

    private static void printBar(String label, double val, double max) {
        int len = (int)((val / max) * 45);
        System.out.printf("  %-13s | %-45s $%,.2f%n", label, "*".repeat(len), val);
    }

    private static String pad(String s, int w) {
        return s.length() >= w ? s.substring(0, w) : s + " ".repeat(w - s.length());
    }

    private static void line(char c, int n) {
        System.out.println("  " + String.valueOf(c).repeat(n));
    }

    // ══════════════════════════════════════════════════════════════════
    // PAUSE — stops screen from wiping before user reads output
    // Fixes the "output blips and disappears" problem on Windows
    // ══════════════════════════════════════════════════════════════════
    private static void pause() {
        System.out.println();
        System.out.print("  >>> Press Enter to return to menu...");
        sc.nextLine();
    }

    private static void printBanner() {
        System.out.println("  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║       ULTIMATE FINANCE TRACKER                  ║");
        System.out.println("  ║    Data Structures & Algorithms — Java          ║");
        System.out.println("  ╚══════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║          ULTIMATE FINANCE TRACKER               ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.println("  ║  TRANSACTIONS                                   ║");
        System.out.println("  ║   1  Add Transaction                            ║");
        System.out.println("  ║   2  Show All Transactions                      ║");
        System.out.println("  ║   3  Show Total Balance                         ║");
        System.out.println("  ║   4  Set Monthly Budget                         ║");
        System.out.println("  ║   5  Delete Transaction                         ║");
        System.out.println("  ║   6  Edit Transaction                           ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.println("  ║  SEARCHING ALGORITHMS                           ║");
        System.out.println("  ║   7  Search by Name/Category  [Linear  O(n)]   ║");
        System.out.println("  ║   8  Search by ID             [Binary  O(logn)]║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.println("  ║  SORTING ALGORITHMS                             ║");
        System.out.println("  ║   9  Sort by Income   [Bubble Sort   O(n²)]    ║");
        System.out.println("  ║  10  Sort by Expense  [Insertion Sort O(n²)]   ║");
        System.out.println("  ║  11  Sort by Date     [Merge Sort  O(nlogn)]   ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.println("  ║  DATA STRUCTURES                                ║");
        System.out.println("  ║  12  Category Analysis    [HashMap]            ║");
        System.out.println("  ║  13  Undo Last             [Stack LIFO]        ║");
        System.out.println("  ║  14  Highest Expense       [Priority Queue]    ║");
        System.out.println("  ║  15  ASCII Financial Graph                      ║");
        System.out.println("  ║  16  ASCII Category Graph  [HashMap]           ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.println("  ║  FILES & REPORTS                                ║");
        System.out.println("  ║  17  Save Data                                  ║");
        System.out.println("  ║  18  Load Data                                  ║");
        System.out.println("  ║  19  Monthly Report                             ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.println("  ║  DSA DEMOS                                      ║");
        System.out.println("  ║  20  BST Traversal         [BST O(n)]          ║");
        System.out.println("  ║  21  Linked List Demo      [LinkedList]        ║");
        System.out.println("  ║  22  Show ALL DSA Live     [All 6 structures]  ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.println("  ║   0  Exit                                       ║");
        System.out.println("  ╚══════════════════════════════════════════════════╝");
    }

    // ══════════════════════════════════════════════════════════════════
    // INPUT HELPERS
    // All use nextLine() to avoid Scanner newline bugs
    // ══════════════════════════════════════════════════════════════════
    private static String readStr(String prompt) {
        String s = "";
        while (s.isEmpty()) {
            System.out.print(prompt);
            s = sc.nextLine().trim();
            if (s.isEmpty()) System.out.println("  Cannot be empty.");
        }
        return s;
    }

    private static String readStrOpt(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  Enter a whole number."); }
        }
    }

    private static double readDbl(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double v = Double.parseDouble(sc.nextLine().trim());
                if (v >= 0) return v;
                System.out.println("  Enter 0 or a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("  Enter a valid number. Example: 1500.00");
            }
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (s.isEmpty()) return LocalDate.now();
            try { return LocalDate.parse(s, Transaction.DATE_FMT); }
            catch (DateTimeParseException e) {
                System.out.println("  Use format yyyy-MM-dd  e.g. 2026-03-15");
            }
        }
    }
}
