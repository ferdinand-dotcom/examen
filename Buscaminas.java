import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Buscaminas {

    private static final int FILAS = 10; // 10 filas
    private static final int COLUMNAS = 10; // 10 columnas
    private static final int MINAS = 10; // 10 minas
    private static JButton[][] botones = new JButton[FILAS][COLUMNAS];
    private static boolean[][] minas = new boolean[FILAS][COLUMNAS];
    private static int[][] conteoMinas = new int[FILAS][COLUMNAS];
    private static boolean[][] descubierto = new boolean[FILAS][COLUMNAS];
    private static boolean[][] marcado = new boolean[FILAS][COLUMNAS];

    private static JFrame frame;
    private static JTextArea textArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            inicializarGUI();
            inicializarTablero();
            colocarMinas();
            calcularConteoMinas();
        });
    }

    private static void inicializarGUI() {
        frame = new JFrame("Buscaminas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panelTablero = new JPanel();
        panelTablero.setLayout(new GridLayout(FILAS, COLUMNAS));
        frame.add(panelTablero, BorderLayout.CENTER);

        // Crear botones para el tablero
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                botones[i][j] = new JButton("■");
                botones[i][j].setFont(new Font("Arial", Font.PLAIN, 20));
                botones[i][j].setFocusPainted(false);
                botones[i][j].setBackground(Color.LIGHT_GRAY);
                botones[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton boton = (JButton) e.getSource();
                        int fila = -1, columna = -1;
                        // Encontrar la fila y columna del botón presionado
                        for (int i = 0; i < FILAS; i++) {
                            for (int j = 0; j < COLUMNAS; j++) {
                                if (botones[i][j] == boton) {
                                    fila = i;
                                    columna = j;
                                }
                            }
                        }
                        manejarAccion(fila, columna);
                    }
                });
                panelTablero.add(botones[i][j]);
            }
        }

        // Área de texto para mostrar mensajes
        textArea = new JTextArea(3, 30);
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea), BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null); // Centra la ventana
        frame.setVisible(true);
    }

    private static void inicializarTablero() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                minas[i][j] = false;
                conteoMinas[i][j] = 0;
                descubierto[i][j] = false;
                marcado[i][j] = false;
            }
        }
    }

    private static void colocarMinas() {
        Random rand = new Random();
        int minasColocadas = 0;

        while (minasColocadas < MINAS) {
            int fila = rand.nextInt(FILAS);
            int columna = rand.nextInt(COLUMNAS);

            if (!minas[fila][columna]) {
                minas[fila][columna] = true;
                minasColocadas++;
            }
        }
    }

    private static void calcularConteoMinas() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (minas[i][j]) {
                    for (int filaAdyacente = -1; filaAdyacente <= 1; filaAdyacente++) {
                        for (int colAdyacente = -1; colAdyacente <= 1; colAdyacente++) {
                            if (i + filaAdyacente >= 0 && i + filaAdyacente < FILAS &&
                                    j + colAdyacente >= 0 && j + colAdyacente < COLUMNAS) {
                                conteoMinas[i + filaAdyacente][j + colAdyacente]++;
                            }
                        }
                    }
                }
            }
        }
    }

    private static void manejarAccion(int fila, int columna) {
        if (descubierto[fila][columna] || marcado[fila][columna]) {
            return; // Ya está descubierta o marcada
        }

        if (minas[fila][columna]) {
            botones[fila][columna].setText("*");
            botones[fila][columna].setBackground(Color.RED);
            textArea.setText("¡Has perdido! Descubriste una mina.");
            mostrarTableroCompleto();
        } else {
            descubrirCasilla(fila, columna);
            if (verificarVictoria()) {
                textArea.setText("¡Has ganado! Descubriste todas las casillas sin minas.");
            }
        }
    }

    private static void descubrirCasilla(int fila, int columna) {
        if (fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS || descubierto[fila][columna]) {
            return;
        }

        descubierto[fila][columna] = true;
        botones[fila][columna]
                .setText(conteoMinas[fila][columna] == 0 ? " " : String.valueOf(conteoMinas[fila][columna]));
        botones[fila][columna].setEnabled(false); // Deshabilitar el botón

        if (conteoMinas[fila][columna] == 0) {
            // Descubrir casillas adyacentes
            for (int filaAdyacente = -1; filaAdyacente <= 1; filaAdyacente++) {
                for (int colAdyacente = -1; colAdyacente <= 1; colAdyacente++) {
                    descubrirCasilla(fila + filaAdyacente, columna + colAdyacente);
                }
            }
        }
    }

    private static boolean verificarVictoria() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (!minas[i][j] && !descubierto[i][j]) {
                    return false; // Si hay alguna casilla no descubierta que no sea mina
                }
            }
        }
        return true;
    }

    private static void mostrarTableroCompleto() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (minas[i][j]) {
                    botones[i][j].setText("*");
                    botones[i][j].setBackground(Color.RED);
                } else {
                    botones[i][j].setText(conteoMinas[i][j] == 0 ? " " : String.valueOf(conteoMinas[i][j]));
                    botones[i][j].setBackground(Color.GRAY);
                }
                botones[i][j].setEnabled(false); // Deshabilitar todos los botones al final
            }
        }
    }
}
