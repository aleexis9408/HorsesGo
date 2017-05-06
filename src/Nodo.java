
import java.awt.Point;
import java.util.ArrayList;

public class Nodo {

    public static final int TIPO_NODO_MAX = 1;
    public static final int TIPO_NODO_MIN = 2;

    private Mapa mapaEstado;
    private int tipoNodo, profundidad, utilidad = -8;
    private Nodo padre;
    private Point posicionJugador, posicionPc;
    private boolean expandido, tieneHijos;

    public Nodo() {
    }

    @Override
    public String toString() {
        return "Nodo{ Utilidad=" + utilidad
                + ", profundidad=" + profundidad
               // + ", mapa=" + mapaEstado
                + ", PosicionJugador=" + posicionJugador
                + ", posicionPc=" + posicionPc
                + ", expandido=" + expandido
                + ", TieneHijos=" + tieneHijos
                + '}';
    }

    public Mapa getMapaEstado() {
        return mapaEstado;
    }

    public boolean isTieneHijos() {
        return tieneHijos;
    }

    public void setTieneHijos(boolean tieneHijos) {
        this.tieneHijos = tieneHijos;
    }

    public boolean isExpandido() {
        return expandido;
    }

    public void setExpandido(boolean expandido) {
        this.expandido = expandido;
    }

    public void setMapaEstado(Mapa mapaEstado) {
        this.mapaEstado = mapaEstado;
    }

    public int getTipoNodo() {
        return tipoNodo;
    }

    public void setTipoNodo(int tipoNodo) {
        this.tipoNodo = tipoNodo;
    }

    public int getProfundidad() {
        return profundidad;
    }

    public void setProfundidad(int profundidad) {
        this.profundidad = profundidad;
    }

    public int getUtilidad() {
        return utilidad;
    }

    public void setUtilidad(int utilidad) {
        this.utilidad = utilidad;
    }

    public Nodo getPadre() {
        return padre;
    }

    public void setPadre(Nodo padre) {
        this.padre = padre;
    }

    public Point getPosicionJugador() {
        return posicionJugador;
    }

    public void setPosicionJugador(Point posicionJugador) {
        this.posicionJugador = posicionJugador;
    }

    public Point getPosicionPc() {
        return posicionPc;
    }

    public void setPosicionPc(Point posicionPc) {
        this.posicionPc = posicionPc;
    }

    public int heuristica() {
        int MovimientosJugador = getMovimientosPosibles(this.posicionJugador, this.mapaEstado).size();
        int MovimientosPc = getMovimientosPosibles(this.posicionPc, this.mapaEstado).size();
        if (MovimientosJugador == 0 && MovimientosPc == 0) {
            this.setUtilidad(tipoNodo == TIPO_NODO_MAX ? -99 : 99);
            return getUtilidad();
        }
        if (MovimientosJugador == 0) {
            this.setUtilidad(99);
            return getUtilidad();
        }
        if (MovimientosPc == 0) {
            this.setUtilidad(-99);
            return getUtilidad();
        }
        this.setUtilidad(MovimientosPc - MovimientosJugador);
        return getUtilidad();
    }

    public ArrayList expandir() {
        ArrayList<Nodo> hijos = new ArrayList();
        //System.out.println("al expandir-> " + this.posicionPc + this.posicionJugador);
        ArrayList<Point> hijosMovimientos = getMovimientosPosibles(
                this.getTipoNodo() == TIPO_NODO_MAX ? this.posicionPc : this.posicionJugador,
                this.mapaEstado);
        //System.out.println("al expandir-> " + hijosMovimientos.size());
        hijosMovimientos.parallelStream().forEach((p) -> {
            hijos.add(this.auxiliarHijo(p));
        });
        //System.out.println("termina de expandir................ hijos->"+hijos.size());
        this.setTieneHijos(!hijos.isEmpty());
        this.setExpandido(true);
        return hijos;
    }

    public Nodo auxiliarHijo(Point p) {
        Nodo n = new Nodo();
        n.setPadre(this);
        n.setProfundidad(this.getProfundidad() + 1);
        n.setTipoNodo(this.getTipoNodo() == TIPO_NODO_MAX ? TIPO_NODO_MIN : TIPO_NODO_MAX);
        Mapa mapa = this.getMapaEstado().clone();
        n.setMapaEstado(mapa);
        if (n.getTipoNodo() == TIPO_NODO_MAX) {
            mapa.setMapa(p, Mapa.CABALLO_JUGADOR_INT);
            mapa.setMapa(this.getPosicionJugador(), Mapa.DESTRUIDA);
            n.setPosicionJugador(p);
            n.setPosicionPc(this.posicionPc);
        } else {
            mapa.setMapa(p, Mapa.CABALLO_PC_INT);
            mapa.setMapa(this.getPosicionPc(), Mapa.DESTRUIDA);
            n.setPosicionPc(p);
            n.setPosicionJugador(this.posicionJugador);
        }
        //System.out.println("auxiliar de expadir->"+n);
        return n;
    }

    public static ArrayList<Point> getMovimientosPosibles(Point ptPosicion, Mapa mapa) {
        ArrayList<Point> listMovimientos = new ArrayList<>();
        int nuFila = (int) ptPosicion.getX();
        int nuColumna = (int) ptPosicion.getY();
        for (int i = 1; i < 9; i++) {
            try {
                int x = 0;
                int y = 0;
                switch (i) {
                    case 1:
                        x = nuFila + 1;
                        y = nuColumna + 2;
                        break;
                    case 2:
                        x = nuFila + 1;
                        y = nuColumna - 2;
                        break;
                    case 3:
                        x = nuFila - 1;
                        y = nuColumna - 2;
                        break;
                    case 4:
                        x = nuFila - 1;
                        y = nuColumna + 2;
                        break;
                    case 5:
                        x = nuFila + 2;
                        y = nuColumna - 1;
                        break;
                    case 6:
                        x = nuFila + 2;
                        y = nuColumna + 1;
                        break;
                    case 7:
                        x = nuFila - 2;
                        y = nuColumna - 1;
                        break;
                    case 8:
                        x = nuFila - 2;
                        y = nuColumna + 1;
                        break;
                }
                int n = mapa.getMapa()[y][x];
                if (n == Mapa.LIBRE) {
                    listMovimientos.add(new Point(x, y));
                }
            } catch (Exception e) {
            }
        }
        return listMovimientos;
    }
}
