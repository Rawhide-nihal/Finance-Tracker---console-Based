// ╔══════════════════════════════════════════════════════════════════╗
// ║                        BSTNode.java                             ║
// ║            BINARY SEARCH TREE — Node + BST Class                ║
// ╠══════════════════════════════════════════════════════════════════╣
// ║  DATA STRUCTURE: Binary Search Tree (BST)                       ║
// ║                                                                  ║
// ║  DEFINITION:                                                     ║
// ║    A rooted binary tree where every node satisfies:             ║
// ║      LEFT  child ID  <  parent ID                               ║
// ║      RIGHT child ID  >  parent ID                               ║
// ║                                                                  ║
// ║  VISUAL EXAMPLE (IDs: 4, 2, 6, 1, 3, 5, 7):                    ║
// ║                   [4]          <-- ROOT                         ║
// ║                  /   \                                           ║
// ║               [2]     [6]                                       ║
// ║              / \      / \                                        ║
// ║           [1] [3]  [5] [7]                                      ║
// ║                                                                  ║
// ║  TIME COMPLEXITY:                                                ║
// ║    Insert   → O(log n) average  |  O(n) worst (skewed tree)     ║
// ║    Search   → O(log n) average  |  O(n) worst                   ║
// ║    Delete   → O(log n) average  |  O(n) worst                   ║
// ║    Inorder  → O(n)  always  (visits every node once)            ║
// ║    Preorder → O(n)  always                                      ║
// ║    Postorder→ O(n)  always                                      ║
// ║                                                                  ║
// ║  INORDER traversal produces nodes in SORTED ascending order.    ║
// ╚══════════════════════════════════════════════════════════════════╝

// ── BST NODE ───────────────────────────────────────────────────────
// Each node in the BST holds:
//   data  = the Transaction object stored at this node
//   left  = pointer to left subtree  (smaller IDs)
//   right = pointer to right subtree (larger IDs)
class BSTNode {
    Transaction data;   // payload stored at this node
    BSTNode left;       // left child  (ID < this node's ID)
    BSTNode right;      // right child (ID > this node's ID)

    BSTNode(Transaction data) {
        this.data  = data;
        this.left  = null;   // null means no child in that direction
        this.right = null;
    }
}

// ── BST CLASS ──────────────────────────────────────────────────────
// Manages the entire tree. Keeps a reference to the ROOT node only.
// All operations navigate from root downward.
class BST {

    // ROOT — the topmost node. null when tree is empty.
    private BSTNode root;

    // ══════════════════════════════════════════════════════════════
    // INSERT OPERATION
    // ══════════════════════════════════════════════════════════════
    // PURPOSE  : Add a new Transaction into the BST.
    // STRATEGY : Compare ID with current node.
    //            If smaller → go LEFT.  If larger → go RIGHT.
    //            Repeat until an empty (null) spot is found.
    //            Place the new node there.
    //
    // EXAMPLE  : Inserting ID=5 into tree with root=4
    //   Step 1: 5 > 4 → go RIGHT
    //   Step 2: right is null → INSERT here
    //
    // TIME COMPLEXITY:
    //   Best/Average : O(log n) — balanced tree, halves each step
    //   Worst        : O(n)    — already sorted input (skewed tree)
    // ══════════════════════════════════════════════════════════════
    public void insert(Transaction t) {
        root = insertRec(root, t);   // start from root
    }

    private BSTNode insertRec(BSTNode node, Transaction t) {
        // BASE CASE: empty spot found → create new node here
        if (node == null) return new BSTNode(t);

        if (t.id < node.data.id) {
            // New ID is SMALLER → navigate to LEFT subtree
            node.left = insertRec(node.left, t);

        } else if (t.id > node.data.id) {
            // New ID is LARGER → navigate to RIGHT subtree
            node.right = insertRec(node.right, t);
        }
        // If equal ID → duplicate, ignored silently

        return node;  // return updated node back up the call stack
    }

    // ══════════════════════════════════════════════════════════════
    // DELETE OPERATION
    // ══════════════════════════════════════════════════════════════
    // PURPOSE  : Remove a node by Transaction ID.
    // THREE CASES to handle:
    //
    //   CASE 1: Node has NO children (leaf node)
    //           → Simply remove it (return null)
    //
    //   CASE 2: Node has ONE child
    //           → Replace node with its child
    //
    //   CASE 3: Node has TWO children
    //           → Find the INORDER SUCCESSOR
    //             (smallest node in the right subtree)
    //           → Copy successor's data into this node
    //           → Delete the successor from right subtree
    //
    // TIME COMPLEXITY:
    //   Average : O(log n)
    //   Worst   : O(n) — skewed tree
    // ══════════════════════════════════════════════════════════════
    public void delete(int id) {
        root = deleteRec(root, id);
    }

    private BSTNode deleteRec(BSTNode node, int id) {
        if (node == null) return null;  // ID not found

        if (id < node.data.id) {
            // Target is in LEFT subtree
            node.left = deleteRec(node.left, id);

        } else if (id > node.data.id) {
            // Target is in RIGHT subtree
            node.right = deleteRec(node.right, id);

        } else {
            // ── FOUND THE NODE TO DELETE ──────────────────────
            // CASE 1 & 2: zero or one child
            if (node.left  == null) return node.right;
            if (node.right == null) return node.left;

            // CASE 3: two children
            // Find inorder successor = leftmost node in right subtree
            BSTNode successor = node.right;
            while (successor.left != null)
                successor = successor.left;

            node.data  = successor.data;                        // copy successor data up
            node.right = deleteRec(node.right, successor.data.id); // delete successor
        }
        return node;
    }

    // ══════════════════════════════════════════════════════════════
    // INORDER TRAVERSAL  →  Left, Root, Right
    // ══════════════════════════════════════════════════════════════
    // PURPOSE  : Visit all nodes in SORTED ASCENDING order by ID.
    // PATTERN  : Recursively visit LEFT subtree first,
    //            then print the CURRENT node,
    //            then recursively visit RIGHT subtree.
    //
    // WHY SORTED? Because BST property guarantees:
    //   all LEFT nodes < current < all RIGHT nodes
    //   So visiting Left → Root → Right gives sorted output.
    //
    // TIME COMPLEXITY: O(n) — every node visited exactly once
    // ══════════════════════════════════════════════════════════════
    public void inorder() {
        System.out.println("  [INORDER: Left → Root → Right  =  Sorted by ID]");
        inorderRec(root);
        System.out.println();
    }

    private void inorderRec(BSTNode node) {
        if (node == null) return;       // base case: empty subtree
        inorderRec(node.left);          // 1. visit LEFT subtree first
        System.out.println("    " + node.data);  // 2. print ROOT
        inorderRec(node.right);         // 3. visit RIGHT subtree
    }

    // ══════════════════════════════════════════════════════════════
    // PREORDER TRAVERSAL  →  Root, Left, Right
    // ══════════════════════════════════════════════════════════════
    // PURPOSE  : Visit root BEFORE its subtrees.
    // USE CASE : Useful for copying or serialising a tree —
    //            inserting nodes in preorder recreates same tree.
    //
    // TIME COMPLEXITY: O(n)
    // ══════════════════════════════════════════════════════════════
    public void preorder() {
        System.out.println("  [PREORDER: Root → Left → Right  =  Root visited first]");
        preorderRec(root);
        System.out.println();
    }

    private void preorderRec(BSTNode node) {
        if (node == null) return;
        System.out.println("    " + node.data);  // 1. print ROOT first
        preorderRec(node.left);                   // 2. visit LEFT
        preorderRec(node.right);                  // 3. visit RIGHT
    }

    // ══════════════════════════════════════════════════════════════
    // POSTORDER TRAVERSAL  →  Left, Right, Root
    // ══════════════════════════════════════════════════════════════
    // PURPOSE  : Visit root AFTER both subtrees.
    // USE CASE : Useful for safely deleting a tree node by node —
    //            children are processed before the parent.
    //
    // TIME COMPLEXITY: O(n)
    // ══════════════════════════════════════════════════════════════
    public void postorder() {
        System.out.println("  [POSTORDER: Left → Right → Root  =  Root visited last]");
        postorderRec(root);
        System.out.println();
    }

    private void postorderRec(BSTNode node) {
        if (node == null) return;
        postorderRec(node.left);                  // 1. visit LEFT
        postorderRec(node.right);                 // 2. visit RIGHT
        System.out.println("    " + node.data);  // 3. print ROOT last
    }

    public boolean isEmpty() { return root == null; }
}
