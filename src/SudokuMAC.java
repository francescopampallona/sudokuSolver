import java.util.*;

public class SudokuMAC {
    private static final int SIZE = 9;
    private int[][] board;
    private Map<Integer, Set<Integer>> domains;

    public SudokuMAC(int[][] board) {
        this.board = board;
        this.domains = new HashMap<>();
        initializeDomains();
    }

    private void initializeDomains() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int cell = row * SIZE + col;
                if (board[row][col] == 0) {
                    Set<Integer> domain = new HashSet<>();
                    for (int value = 1; value <= SIZE; value++) {
                        domain.add(value);
                    }
                    domains.put(cell, domain);
                } else {
                    Set<Integer> domain = new HashSet<>();
                    domain.add(board[row][col]);
                    domains.put(cell, domain);
                }
            }
        }
    }

    public boolean macSearch() {
        return backtrack();
    }

    private boolean backtrack() {
        // Se tutte le celle sono assegnate, il Sudoku è risolto
        if (isComplete()) {
            return true;
        }

        // Seleziona una variabile non assegnata (cella con il dominio più piccolo)
        int cell = selectUnassignedVariable();

        // Prova tutti i valori nel dominio della cella selezionata
        for (int value : new HashSet<>(domains.get(cell))) {
            if (isConsistent(cell, value)) {
                // Prova ad assegnare il valore
                assign(cell, value);

                // Applica AC-3 per mantenere la consistenza degli archi
                if (ac3()) {
                    // Continua la ricerca
                    if (backtrack()) {
                        return true;
                    }
                }

                // Se fallisce, rimuove l'assegnazione e ripristina i domini
                unassign(cell);
            }
        }

        // Nessuna soluzione trovata per questa configurazione
        return false;
    }

    private boolean ac3() {
        Queue<int[]> queue = new LinkedList<>();
        for (int cell = 0; cell < SIZE * SIZE; cell++) {
            for (int neighbor : getNeighbors(cell)) {
                queue.add(new int[]{cell, neighbor});
            }
        }

        while (!queue.isEmpty()) {
            int[] arc = queue.poll();
            int cell1 = arc[0];
            int cell2 = arc[1];
            if (revise(cell1, cell2)) {
                if (domains.get(cell1).isEmpty()) {
                    return false;
                }

            }
        }
        return true;
    }

    private boolean revise(int cell1, int cell2) {
        boolean revised = false;
        Iterator<Integer> it = domains.get(cell1).iterator();
        while (it.hasNext()) {
            int value = it.next();
            boolean consistent = false;
            for (int neighborValue : domains.get(cell2)) {
                if (value != neighborValue) {
                    consistent = true;
                    break;
                }
            }
            if (!consistent) {
                it.remove();
                revised = true;
            }
        }
        return revised;
    }

    private int selectUnassignedVariable() {
        int minDomainSize = Integer.MAX_VALUE;
        int selectedCell = -1;
        for (int cell : domains.keySet()) {
            if (board[cell / SIZE][cell % SIZE] == 0 && domains.get(cell).size() < minDomainSize) {
                minDomainSize = domains.get(cell).size();
                selectedCell = cell;
            }
        }
        return selectedCell;
    }

    private void assign(int cell, int value) {
        board[cell / SIZE][cell % SIZE] = value;
        domains.put(cell, new HashSet<>(Collections.singleton(value)));
    }

    private void unassign(int cell) {
        board[cell / SIZE][cell % SIZE] = 0;
        initializeDomains();
    }

    private boolean isConsistent(int cell, int value) {
        int row = cell / SIZE;
        int col = cell % SIZE;

        for (int c = 0; c < SIZE; c++) {
            if (board[row][c] == value) {
                return false;
            }
        }

        for (int r = 0; r < SIZE; r++) {
            if (board[r][col] == value) {
                return false;
            }
        }

        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3;c++) {
                if (board[r][c] == value) {
                    return false;
                }
            }
        }

        return true;
    }

    private List<Integer> getNeighbors(int cell) {
        List<Integer> neighbors = new ArrayList<>();
        int row = cell / SIZE;
        int col = cell % SIZE;

        for (int c = 0; c < SIZE; c++) {
            if (c != col) {
                neighbors.add(row * SIZE + c);
            }
        }

        for (int r = 0; r < SIZE; r++) {
            if (r != row) {
                neighbors.add(r * SIZE + col);
            }
        }

        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                int neighbor = r * SIZE + c;
                if (neighbor != cell) {
                    neighbors.add(neighbor);
                }
            }
        }

        return neighbors;
    }

    private boolean isComplete() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void printBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                System.out.print(board[row][col] + " ");
            }
            System.out.println();
        }
    }
}

