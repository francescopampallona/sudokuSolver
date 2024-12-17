import java.util.*;

public class SudokuAC3 {
    private static final int SIZE = 9; // Dimensione del Sudoku
    private int[][] board; // Griglia del Sudoku
    private Map<Integer, Set<Integer>> domains; // Mappa dei domini per ogni cella

    public SudokuAC3(int[][] board) {
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
                        if (isValid(value, row, col)) { // Calcola solo valori validi
                            domain.add(value);
                        }
                    }
                    if (domain.isEmpty()) {
                        throw new IllegalStateException("Sudoku non risolvibile: dominio vuoto in cella " + cell);
                    }
                    domains.put(cell, domain);
                } else {
                    domains.put(cell, new HashSet<>(Collections.singleton(board[row][col])));
                }
            }
        }
    }

    private boolean isValid(int value, int row, int col) {
        for (int c = 0; c < SIZE; c++) {
            if (board[row][c] == value) return false;
        }
        for (int r = 0; r < SIZE; r++) {
            if (board[r][col] == value) return false;
        }
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if (board[r][c] == value) return false;
            }
        }
        return true;
    }

    public List<Integer> getNeighbors(int cell) {
        List<Integer> neighbors = new ArrayList<>();
        int row = cell / SIZE;
        int col = cell % SIZE;

        for (int c = 0; c < SIZE; c++) {
            if (c != col) neighbors.add(row * SIZE + c);
        }
        for (int r = 0; r < SIZE; r++) {
            if (r != row) neighbors.add(r * SIZE + col);
        }
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if (r != row || c != col) neighbors.add(r * SIZE + c);
            }
        }
        return neighbors;
    }

    public boolean ac3() {
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
            //Se in cell2 c'è almeno un valore diverso da quello corrente allora è consistente
            boolean consistent = domains.get(cell2).stream().anyMatch(neighborValue -> neighborValue != value);
            if (!consistent) {
                it.remove();
                revised = true;
            }
        }
        return revised;
    }

    public boolean propagateSolution() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int cell = row * SIZE + col;
                if (domains.get(cell).size() == 1) {
                    board[row][col] = domains.get(cell).iterator().next();
                }
            }
        }
        return Arrays.stream(board).flatMapToInt(Arrays::stream).allMatch(value -> value != 0);
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
