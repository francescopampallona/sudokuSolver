import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int statoIniziale[][] = {{9, 0, 0, 0, 0, 5, 8, 0, 0},
                {0, 0, 0, 4, 0, 0, 0, 6, 0},
                {0, 0, 0, 9, 0, 0, 0, 0, 0},
                {0, 0, 0, 6, 3, 0, 0, 7, 0},
                {2, 0, 8, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 2, 4, 0, 0},
                {0, 3, 2, 0, 8, 0, 0, 0, 0},
                {0, 6, 7, 0, 0, 0, 0, 0, 0}};

        // Esegui l'algoritmo AC-3
        SudokuAC3 sudoku = new SudokuAC3(statoIniziale);
        System.out.println("SUDOKU DA RISOLVERE:");
        sudoku.printBoard();
        System.out.println("Scegli algoritmo di risoluzione");
        System.out.println("1)PROPAGAZIONE DEI VINCOLI CON AC3");
        System.out.println("2)MAC: Ricerca in avanti con AC3");
        Scanner scanner = new Scanner(System.in);
        String scelta = scanner.nextLine();

        if (scelta.equals("1")) {
            if (sudoku.ac3() && sudoku.propagateSolution()) {
                System.out.println("SUDOKU RISOLTO!");
                sudoku.printBoard();
            } else {
                System.out.println("SUDOKU NON RISOLTO. CI SONO ANCORA CELLE VUOTE");
                sudoku.printBoard();
            }
        } else if (scelta.equals("2")) {
            SudokuMAC solver = new SudokuMAC(statoIniziale);
            if (solver.macSearch()) {
                System.out.println("Sudoku risolto:");
                solver.printBoard();
            } else {
                System.out.println("Impossibile risolvere il Sudoku.");
            }
        }

    }

}
