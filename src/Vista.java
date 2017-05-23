
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.Timer;
import net.miginfocom.swing.MigLayout;

public class Vista implements ActionListener, MouseListener {

    public static final int NIVEL_PRINCIPIANTE = 4;
    public static final int NIVEL_INTERMEDIO = 6;
    public static final int NIVEL_AVANZADO = 8;
    public ImageIcon CABALLO_JUGADOR_IMG;
    public ImageIcon CABALLO_PC_IMG;
    public ImageIcon AYUDA_IMG;
    public ImageIcon DELETE_IMG;

    public static void main(String[] args) {
        new Vista().init();
    }

    private JFrame frame;
    private JPanel pGlobal;
    private JMenuBar menuBar;
    private JMenu menuMapa, menuNivel;
    private JMenuItem menuCargar, menuReiniciar;
    private ButtonGroup niveles;
    private JRadioButtonMenuItem radioPrincipiante, radioIntermedio, radioAvanzado;
    private JPanel pMapa;
    private HashMap<Point, JLabel> Labels;
    public static Point CABALLO_JUGADOR;
    public static Point CABALLO_PC;
    public static Mapa mp;
    public static ArrayList<Point> MOVIMIENTOS_JUGADOR_POINT = new ArrayList<>();
    public static int NIVEL;

    public void init() {
        frame = new JFrame();
        frame.setTitle("Horses Go");
        frame.setLayout(new MigLayout());
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(700, 700));
        frame.setResizable(false);
        pGlobal = new JPanel(new MigLayout());
        frame.add(pGlobal, "span, width max(100%, 100%) ");

        /**
         * creacion de menu *
         */
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        menuMapa = new JMenu("Juego");
        menuBar.add(menuMapa);
        menuCargar = new JMenuItem("Cargar Mapa");
        menuMapa.add(menuCargar);
        menuReiniciar = new JMenuItem("Reiniciar Mapa");
        menuMapa.add(menuReiniciar);

        menuNivel = new JMenu("Nivel");
        menuBar.add(menuNivel);
        niveles = new ButtonGroup();
        radioPrincipiante = new JRadioButtonMenuItem("Principiante");
        radioPrincipiante.setSelected(true);
        niveles.add(radioPrincipiante);
        menuNivel.add(radioPrincipiante);

        radioIntermedio = new JRadioButtonMenuItem("Intermedio");
        niveles.add(radioIntermedio);
        menuNivel.add(radioIntermedio);

        radioAvanzado = new JRadioButtonMenuItem("Avanzado");
        niveles.add(radioAvanzado);
        menuNivel.add(radioAvanzado);

        /**
         * creacion panel mapa *
         */
        pMapa = new JPanel();
        pMapa.setLayout(new GridLayout());
        pMapa.setVisible(true);
        pMapa.setPreferredSize(new Dimension(600, 600));
        pMapa.setBorder(BorderFactory.createTitledBorder("Mapa"));
        pGlobal.add(pMapa, "span, width max(80%, 90%)");

        /**
         * acciones listener *
         */
        menuCargar.addActionListener(this);
        menuReiniciar.addActionListener(this);
        pMapa.addMouseListener(this);
        frame.pack();
    }

    public void construirMapa(int[][] mp) {
        pMapa.removeAll();
        pMapa.updateUI();
        pMapa.setLayout(new GridLayout(mp.length, mp.length));
        resize_imagen();
        JLabel lbl;
        Labels = new HashMap<>();
        for (int i = 0; i < mp.length; i++) {
            for (int j = 0; j < mp.length; j++) {
                lbl = new JLabel();
                lbl.setOpaque(true);
                lbl.setBackground(i % 2 == 0 ^ j % 2 == 0 ? Color.decode("#C19A6B") : Color.decode("#422918"));//XOR               

                if (mp[i][j] == Mapa.CABALLO_JUGADOR_INT) {
                    lbl.setIcon(CABALLO_JUGADOR_IMG);
                }
                if (mp[i][j] == Mapa.CABALLO_PC_INT) {
                    lbl.setIcon(CABALLO_PC_IMG);
                }

                lbl.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.addMouseListener(this);
                Labels.put(new Point(j, i), lbl);
                pMapa.add(lbl);
            }
        }
        pMapa.updateUI();
    }

    public ImageIcon scalar_imagen(String url) {
        ImageIcon imgIcoUV = new ImageIcon(this.getClass().getResource(url));
        Image image = imgIcoUV.getImage();
        Image newimg = image.getScaledInstance(pMapa.getWidth() / NIVEL - 5, pMapa.getWidth() / NIVEL - 5, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

    public void resize_imagen() {
        CABALLO_JUGADOR_IMG = scalar_imagen("/img/horseHuman.png");
        CABALLO_PC_IMG = scalar_imagen("/img/horsePc.png");
        AYUDA_IMG = scalar_imagen("/img/verde.png");
        DELETE_IMG = scalar_imagen("/img/delete.png");
    }

    public void LimpiarAyuda() {
        MOVIMIENTOS_JUGADOR_POINT.parallelStream().map((p) -> Labels.get(p)).forEach((lb) -> {
            lb.setIcon(null);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuCargar) {
            int nivel = radioPrincipiante.isSelected() ? 4 : 0;
            nivel = radioIntermedio.isSelected() ? 6 : nivel;
            nivel = radioAvanzado.isSelected() ? 8 : nivel;
            NIVEL = nivel;
            mp = new Mapa(nivel);
            construirMapa(mp.getMapa());
            new Timer(900, new ActionListener() {
                Minimax m = new Minimax(NIVEL);

                @Override
                public void actionPerformed(ActionEvent ae) {
                    Nodo n = m.desicionMinimax();
                    mover(CABALLO_PC, n.getPosicionPc(), Labels.get(n.getPosicionPc()), false);
                    ((Timer) ae.getSource()).stop();
                }
            }).start();
        }
    }

    public void mover(Point actual_posicion, Point nueva_posicion, JLabel nueva_posicion_label, boolean indicador) {
        JLabel anterior = Labels.get(actual_posicion);
        anterior.setIcon(DELETE_IMG);
        nueva_posicion_label.setIcon(indicador ? CABALLO_JUGADOR_IMG : CABALLO_PC_IMG);
        mp.setMapa(indicador ? CABALLO_JUGADOR : CABALLO_PC, Mapa.DESTRUIDA);
        if (indicador) {
            CABALLO_JUGADOR = nueva_posicion;
        } else {
            CABALLO_PC = nueva_posicion;
        }
        mp.setMapa(indicador ? CABALLO_JUGADOR : CABALLO_PC, indicador ? Mapa.CABALLO_JUGADOR_INT : Mapa.CABALLO_PC_INT);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            if (e.getSource() == Labels.get(CABALLO_JUGADOR)) {
                MOVIMIENTOS_JUGADOR_POINT = Nodo.getMovimientosPosibles(CABALLO_JUGADOR, mp);
                MOVIMIENTOS_JUGADOR_POINT.parallelStream().map((p) -> Labels.get(p)).forEach((lb) -> {
                    lb.setIcon(AYUDA_IMG);
                });
                if (MOVIMIENTOS_JUGADOR_POINT.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Maquina Gana");
                }
            }

            if (!MOVIMIENTOS_JUGADOR_POINT.isEmpty() && e.getSource() != Labels.get(CABALLO_JUGADOR)) {
                try {
                    Point p = MOVIMIENTOS_JUGADOR_POINT.parallelStream()
                            .filter(x -> e.getSource() == Labels.get(x))
                            .findFirst()
                            .get();
                    if (p != null) {
                        LimpiarAyuda();
                        mover(CABALLO_JUGADOR, p, (JLabel) e.getSource(), true);

                        //movimiento de la maquina....
                        new Timer(1000, new ActionListener() {
                            Minimax m = new Minimax(NIVEL);

                            @Override
                            public void actionPerformed(ActionEvent ae) {
                                Instant inicio = Instant.now();
                                Nodo n = m.desicionMinimax();
                                Instant fin = Instant.now();
                                System.out.println(Duration.between(inicio, fin).toMillis() + " milisegundos");
                                mover(CABALLO_PC, n.getPosicionPc(), Labels.get(n.getPosicionPc()), false);
                                ((Timer) ae.getSource()).stop();
                            }
                        }).start();
                    }
                } catch (java.util.NoSuchElementException n) {

                }
            }
        } catch (java.lang.NullPointerException n) {

        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
