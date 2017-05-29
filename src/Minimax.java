
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Minimax {

    ArrayList<Nodo> arbolMinimax = new ArrayList<>();
    final private int nivel;

    public Minimax(int nivel) {
        this.nivel = nivel;
    }

    public ArrayList<Nodo> getArbol() {
        return arbolMinimax;
    }

    public Nodo estado_inicial() {
        Nodo padre = new Nodo();
        padre.setProfundidad(0);
        padre.setTipoNodo(Nodo.TIPO_NODO_MAX);
        padre.setPosicionJugador(Vista.CABALLO_JUGADOR);
        padre.setPosicionPc(Vista.CABALLO_PC);
        padre.setMapaEstado(Vista.mp);
        arbolMinimax.add(padre);
        return padre;
    }

    private int max_valor(Nodo nodo) {
        try{
        if(nodo.getProfundidad() < nivel)
            nodo.expandir();
        return nodo.getProfundidad() < nivel && nodo.isTieneHijos()
                ? nodo.getHijos().stream().mapToInt((Nodo n) -> {
                    arbolMinimax.add(n);
                    n.setUtilidad(min_valor(n));
                    return n.getUtilidad();
                }).max().getAsInt(): nodo.heuristica();
        }catch(ArrayIndexOutOfBoundsException n){
            System.err.println(nodo);
            return max_valor(nodo);
        }
    }

    private int min_valor(Nodo nodo) {
        try{
        if(nodo.getProfundidad() < nivel)
            nodo.expandir();
        return nodo.getProfundidad() < nivel && nodo.isTieneHijos()
                ? nodo.getHijos().stream().mapToInt((Nodo n) -> {
                    arbolMinimax.add(n);
                    n.setUtilidad(max_valor(n));
                    return n.getUtilidad();
                }).min().getAsInt() : nodo.heuristica();
        }catch(ArrayIndexOutOfBoundsException n){
            System.err.println(nodo);
            return min_valor(nodo);
        }
    }

    public Nodo desicionMinimax() {
        try {
            int valor_desicion_minimax = max_valor(estado_inicial());
            Nodo raiz = arbolMinimax.get(0);
            raiz.setUtilidad(valor_desicion_minimax);
            /*
             * imprimir *
             */
            arbolMinimax.forEach(x -> System.out.println("arbol minimax ->" + x));
            System.out.println("Tamaño de arbol ->" + arbolMinimax.size());
            System.out.println("Desicion minimax =" + valor_desicion_minimax);
            return raiz.getHijos().parallelStream()
                    .filter(x -> x.getUtilidad() == valor_desicion_minimax)
                    .findFirst()
                    .get();
        } catch (java.util.NoSuchElementException n) {
            JOptionPane.showMessageDialog(null, "Jugador Gana");
            return arbolMinimax.get(0);
        }
    }

}
