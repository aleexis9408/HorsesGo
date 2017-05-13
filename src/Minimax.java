
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

public class Minimax {

    ArrayList<Nodo> arbolMinimax= new ArrayList<>();
    final private int nivel;

    public Minimax(int nivel) {
        this.nivel = nivel;    
    }

    public ArrayList<Nodo> getArbol() {
        return arbolMinimax;
    }

    public void crearArbol() {
        Nodo padre = new Nodo();
        padre.setProfundidad(0);
        padre.setTipoNodo(Nodo.TIPO_NODO_MAX);
        padre.setPosicionJugador(Vista.CABALLO_JUGADOR);
        padre.setPosicionPc(Vista.CABALLO_PC);
        padre.setMapaEstado(Vista.mp);
        arbolMinimax.add(padre);
        System.out.println("crear arbol");        
        for (int i = 0; i < nivel; i++) {
            int profundidad = i;
            ArrayList<Nodo> hijos = new ArrayList();
            //System.out.println(arbolMinimax.size()+"profundida="+profundidad);
            arbolMinimax.stream()
                    .filter((Nodo x) -> x.getProfundidad() == profundidad)
                    .forEach((Nodo x) -> {
                        hijos.addAll(x.expandir());
                    });
            arbolMinimax.addAll(hijos);
        }
        System.out.println("Termino de crear arbol ->"+ arbolMinimax.size() );
    }

    private int max_valor(Nodo nodo) {
        return nodo.isTieneHijos() ? sucesores(nodo).parallelStream().map((Nodo n) -> {
            n.setUtilidad(min_valor(n));
            return n;
        }).max((x, y) -> x.getUtilidad() - y.getUtilidad()).get().getUtilidad() : nodo.heuristica();
    }

    private int min_valor(Nodo nodo) {
        return nodo.isTieneHijos() ? sucesores(nodo).parallelStream().map((Nodo n) -> {
            n.setUtilidad(max_valor(n));
            return n;
        }).min((x, y) -> x.getUtilidad() - y.getUtilidad()).get().getUtilidad() : nodo.heuristica();
    }

    private ArrayList<Nodo> sucesores(Nodo padre) {
        return arbolMinimax.parallelStream()
                .filter(x -> padre.equals(x.getPadre()))
                .collect(Collectors.toCollection(ArrayList<Nodo>::new));
    }

    public Nodo desicionMinimax() {
        try {
            crearArbol();
            //arbolMinimax.forEach(x -> System.out.println("arbol minimax ->" + x));
            int valor_desicion_minimax = max_valor(arbolMinimax.get(0));
            Nodo raiz = arbolMinimax.get(0);
            raiz.setUtilidad(valor_desicion_minimax);
            System.out.println("Desicion minimax ="+valor_desicion_minimax);           
            //arbolMinimax.forEach(x -> System.out.println("Desicion minimax ->" + x));
            return sucesores(raiz).parallelStream()
                    .filter(x->x.getUtilidad()==valor_desicion_minimax)
                    .findFirst()
                    .get();            
        } catch (java.util.NoSuchElementException n) {
            JOptionPane.showMessageDialog(null, "Jugador Gana");
            return arbolMinimax.get(0);
        }
    }

}
