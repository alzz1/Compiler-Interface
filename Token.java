package compiler;
/** Representa um token reconhecido pelo analisador léxico. */
public class Token {

    public enum Classe {
        SIMBOLO_ESPECIAL  ("símbolo especial"),
        PALAVRA_RESERVADA ("palavra reservada"),
        IDENTIFICADOR     ("identificador"),
        CONSTANTE_INT     ("constante_int"),
        CONSTANTE_FLOAT   ("constante_float"),
        CONSTANTE_CHAR    ("constante_char"),
        CONSTANTE_STRING  ("constante_string");

        private final String descricao;
        Classe(String descricao) { this.descricao = descricao; }
        public String descricao() { return descricao; }
    }

    public final int    linha;
    public final Classe classe;
    public final String lexema;

    public Token(int linha, Classe classe, String lexema) {
        this.linha  = linha;
        this.classe = classe;
        this.lexema = lexema;
    }

    @Override
    public String toString() {
        return String.format("linha %d   %-20s %s", linha, classe.descricao(), lexema);
    }
}