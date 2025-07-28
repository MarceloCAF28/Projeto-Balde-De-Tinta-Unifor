package trabalhoav1;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Stack;
import java.util.ArrayList;
import javax.swing.*;
import java.util.List;

public class Main extends JPanel {
    private final int[][] matrix;
    private final boolean[][] visited;
    private final int rows, cols;
    private final int cellSize = 1;
    private final List<List<Integer>> adjacencyList;

    private int startRow, startCol, startValue, newValue = 128;

    public Main(int[][] matrix) {
        this.matrix = matrix;
        this.rows = matrix.length;
        this.cols = matrix[0].length;
        this.visited = new boolean[rows][cols];
        this.adjacencyList = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                List<Integer> neighbors = new ArrayList<>();
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) continue;
                        int nr = r + dr, nc = c + dc;
                        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                            neighbors.add(nr * cols + nc);
                        }
                    }
                }
                adjacencyList.add(neighbors);
            }
        }

        setPreferredSize(new Dimension(cols * cellSize, rows * cellSize));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() / cellSize, row = e.getY() / cellSize;
                if (row >= 0 && row < rows && col >= 0 && col < cols && matrix[row][col] != -1) {
                    startRow = row;
                    startCol = col;
                    startValue = matrix[row][col];

                    // Reinicializar matriz de visitados
                    for (boolean[] rowVisited : visited) {
                        java.util.Arrays.fill(rowVisited, false);
                    }

                    // Iniciar DFS em uma nova thread
                    new Thread(Main.this::dfsTraversal).start();
                }
            }
        });
    }

    private void dfsTraversal() {
        Stack<Integer> stack = new Stack<>();
        stack.push(startRow * cols + startCol);

        while (!stack.isEmpty()) {
            int index = stack.pop(), r = index / cols, c = index % cols;

            if (visited[r][c] || matrix[r][c] != startValue) continue;

            visited[r][c] = true;
            matrix[r][c] = newValue;
            repaint(c * cellSize, r * cellSize, cellSize, cellSize);
            sleep(1);

            for (int neighborIndex : adjacencyList.get(index)) {
                stack.push(neighborIndex);
            }
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g2d.setColor(visited[r][c] ? Color.PINK : matrix[r][c] == 0 ? Color.WHITE : new Color(matrix[r][c], matrix[r][c], matrix[r][c]));
                g2d.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
            }
        }
    }

    public static int[][] loadMatrixFromFile(String filePath, int rows, int cols) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        int[][] matrix = new int[rows][cols];
        String line;

        for (int r = 0; r < rows; r++) {
            line = reader.readLine();
            String[] values = line.trim().split("\\s+");
            for (int c = 0; c < cols; c++) {
                matrix[r][c] = Integer.parseInt(values[c]);
            }
        }

        reader.close();
        return matrix;
    }

    public static void main(String[] args) {
        try {
            String filePath = "C:\\Users\\USER\\Desktop\\Engenheria Da Computação\\Grafos\\trabalhoav1\\UNIFOR_grayscale.txt";
            int rows = 1000, cols = 1000;
            int[][] matrix = loadMatrixFromFile(filePath, rows, cols);

            JFrame frame = new JFrame("Balde de Tinta");
            Main panel = new Main(matrix);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(panel);
            frame.pack();
            frame.setVisible(true);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}