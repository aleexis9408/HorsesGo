
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
    final public ImageIcon CABALLO_JUGADOR_IMG = scalar_imagen("/img/horseHuman.png");
    final public ImageIcon CABALLO_PC_IMG = scalar_imagen("/img/horsePc.png");
    final public ImageIcon AYUDA_IMG = scalar_imagen("/img/verde.png");
    final public ImageIcon DELETE_IMG = scalar_imagen("/img/delete.png");

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
        frame.setLayout(new MigLayout());
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1000, 700));
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
        pGlobal.add(pMapa, "growx, growy");

        /**
         * acciones listener *
         */
        menuCargar.addActionListener(this);
        menuReiniciar.addActionListener(this);

        frame.pack();
    }

    public void construirMapa(int[][] mp) {
        pMapa.removeAll();
        pMapa.updateUI();
        pMapa.setLayout(new GridLayout(mp.length, mp.length));
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
        Image newimg = image.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
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
        }
    }
    
   
    

    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getSource() == Labels.get(CABALLO_JUGADOR)) {
            MOVIMIENTOS_JUGADOR_POINT = Nodo.getMovimientosPosibles(CABALLO_JUGADOR, mp);
            MOVIMIENTOS_JUGADOR_POINT.parallelStream().map((p) -> Labels.get(p)).forEach((lb) -> {
                lb.setIcon(AYUDA_IMG);
            });
            if (MOVIMIENTOS_JUGADOR_POINT.size() == 0) {
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
                    JLabel anterior = Labels.get(CABALLO_JUGADOR);
                    anterior.setIcon(DELETE_IMG);
                    JLabel nuevo = (JLabel) e.getSource();
                    nuevo.setIcon(CABALLO_JUGADOR_IMG);
                    mp.setMapa(CABALLO_JUGADOR, Mapa.DESTRUIDA);
                    CABALLO_JUGADOR = p;
                    mp.setMapa(CABALLO_JUGADOR, Mapa.CABALLO_JUGADOR_INT);

                    //movimiento de la maquina....
                    new Timer(300, new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            Minimax m = new Minimax(NIVEL);
                            Nodo n = m.desicionMinimax();
                            JLabel anterior = Labels.get(CABALLO_PC);
                            anterior.setIcon(DELETE_IMG);
                            JLabel nuevo = Labels.get(n.getPosicionPc());
                            nuevo.setIcon(CABALLO_PC_IMG);
                            mp.setMapa(CABALLO_PC, Mapa.DESTRUIDA);
                            CABALLO_PC = n.getPosicionPc();
                            mp.setMapa(CABALLO_PC, Mapa.CABALLO_PC_INT);                            
                            ((Timer) ae.getSource()).stop();
                        }

                    }).start();
                }
            } catch (java.util.NoSuchElementException n) {

            }
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
