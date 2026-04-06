package compiler;

/** Erro léxico com linha e descrição. */
public class ErroLexico extends Exception {

    public final int linha;

    public ErroLexico(int linha, String mensagem) {
        super(mensagem);
        this.linha = linha;
    }
}