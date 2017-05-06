
import java.awt.Point;
import java.util.Arrays;
import java.util.stream.Stream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Alexis Rojas
 */
public class Mapa implements Cloneable {

    
    public static final int LIBRE = 0;
    public static final int DESTRUIDA = 1;
    public static final int CABALLO_PC_INT = 2;
    public static final int CABALLO_JUGADOR_INT = 3;

    private int[][] mapa;

    @Override
    protected Mapa clone() {
        Mapa clon = new Mapa(this.deepCopy(this.mapa));
        return clon;
    }

    public int[][] deepCopy(int[][] mapaOriginal) {
        int[][] result = new int[mapaOriginal.length][];
        for (int i = 0; i < mapaOriginal.length; i++) {
            int[] aMatrix = mapaOriginal[i];
            int aLength = aMatrix.length;
            result[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, result[i], 0, aLength);
        }
        return result;
    }

    public Mapa(int[][] mapa) {
        this.mapa = mapa;
    }

    public Mapa(int nivel) {
        mapa = Stream.generate(() -> Stream.generate(() -> LIBRE).limit(nivel).mapToInt(x -> x).toArray())
                .limit(nivel)
                .toArray(int[][]::new);
//        Vista.CABALLO_JUGADOR = posicionesRandom(nivel);
//        do {
//            Vista.CABALLO_PC = posicionesRandom(nivel);
//        } while (Vista.CABALLO_JUGADOR.equals(Vista.CABALLO_PC));// no repitir posiciones

        Vista.CABALLO_JUGADOR = new Point(3, 1);
        Vista.CABALLO_PC = new Point(0, 0);
        //System.out.println(Vista.CABALLO_JUGADOR);
        mapa[Vista.CABALLO_JUGADOR.y][Vista.CABALLO_JUGADOR.x] = CABALLO_JUGADOR_INT;
        mapa[Vista.CABALLO_PC.y][Vista.CABALLO_PC.x] = CABALLO_PC_INT;
    }

    private Point posicionesRandom(int nivel) {
        return new Point(0 + (int) (Math.random() * nivel), 0 + (int) (Math.random() * nivel));
    }

    public int[][] getMapa() {
        return mapa;
    }

    public void setMapa(Point p, int valor) {
        mapa[p.y][p.x] = valor;
    }

    @Override
    public String toString() {
        System.out.println("Mapa {");
        Arrays.stream(mapa).forEach(x->System.out.println(Arrays.toString(x)));
        return "" ;
    }
    
    

}
