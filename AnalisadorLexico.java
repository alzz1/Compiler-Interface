package compiler;

import java.util.*;

public class AnalisadorLexico {

    private static final Set<String> PALAVRAS_RESERVADAS = new HashSet<>(Arrays.asList(
            "ask", "bool", "char", "define", "end", "elif", "else",
            "false", "float", "if", "int", "main", "repeat",
            "string", "tell", "true", "until", "while"
    ));

    private final String fonte;
    private final int    tamanho;
    private int pos;
    private int linha;

    public AnalisadorLexico(String fonte) {
        this.fonte   = fonte.replace("\r\n", "\n").replace("\r", "\n");
        this.tamanho = this.fonte.length();
        this.pos     = 0;
        this.linha   = 1;
    }

    // ── API pública ───────────────────────────────────────────────────────────

    public List<Token> analisar() throws ErroLexico {
        List<Token> tokens = new ArrayList<>();
        while (pos < tamanho) {
            Token t = proximoToken();
            if (t != null) tokens.add(t);
        }
        return tokens;
    }

    // ── Motor principal ───────────────────────────────────────────────────────

    private Token proximoToken() throws ErroLexico {
        // Pula espaços e tabulações
        while (pos < tamanho && (atual() == ' ' || atual() == '\t')) pos++;
        if (pos >= tamanho) return null;

        // Quebra de linha
        if (atual() == '\n') { pos++; linha++; return null; }

        // Comentário de linha: $ ...
        if (atual() == '$') return lerComentarioLinha();

        // Comentário de bloco: { \n ... \n }
        if (atual() == '{') return lerComentarioBloco();

        // cte_string
        if (atual() == '"') return lerString();

        // cte_char: barra invertida literal seguida de n, s ou t
        if (atual() == '\\') return lerChar();

        // número
        if (Character.isDigit(atual())) return lerNumero();

        // identificador ou palavra reservada (só minúsculas)
        if (atual() >= 'a' && atual() <= 'z') return lerIdentificador();

        // letra maiúscula → identificador inválido
        if (atual() >= 'A' && atual() <= 'Z') {
            int linhaToken = linha;
            while (pos < tamanho && !isSeparador(atual())) pos++;
            throw new ErroLexico(linhaToken, "linha " + linhaToken + ": identificador inválido");
        }

        // símbolos especiais
        return lerSimbolo();
    }

    // ── Comentários ───────────────────────────────────────────────────────────

    private Token lerComentarioLinha() {
        pos++; // consome '$'
        while (pos < tamanho && atual() != '\n') pos++;
        return null;
    }

    /**
     * Comentário de bloco: { \n conteúdo \n }
     * A chave de abertura deve ser seguida de \n e a de fechamento precedida de \n.
     */
    private Token lerComentarioBloco() throws ErroLexico {
        int linhaInicio = linha;
        pos++; // consome '{'

        // Após '{' deve vir imediatamente '\n'
        if (pos >= tamanho || atual() != '\n') {
            // Não é comentário de bloco válido — consome até fim da linha
            while (pos < tamanho && atual() != '\n') pos++;
            throw new ErroLexico(linhaInicio, "linha " + linhaInicio + ": comentário inválido ou não finalizado");
        }
        pos++; linha++; // consome '\n' após '{'

        while (pos < tamanho) {
            // Procura '\n}'
            if (atual() == '\n') {
                pos++; linha++;
                if (pos < tamanho && atual() == '}') {
                    pos++; // consome '}'
                    return null;
                }
            } else if (atual() == '{' || atual() == '}') {
                // Chave dentro do bloco sem estar precedida de \n → inválido
                throw new ErroLexico(linhaInicio, "linha " + linhaInicio + ": comentário inválido ou não finalizado");
            } else {
                pos++;
            }
        }
        throw new ErroLexico(linhaInicio, "linha " + linhaInicio + ": comentário inválido ou não finalizado");
    }

    // ── Literais ──────────────────────────────────────────────────────────────

    /**
     * cte_string: " ([^\n\%"] | %x)* "
     * Aceita qualquer char exceto \n, \, % e " — ou a sequência %x (qualquer char após %).
     */
    private Token lerString() throws ErroLexico {
        int linhaToken = linha;
        pos++; // consome '"'
        StringBuilder sb = new StringBuilder("\"");

        while (pos < tamanho) {
            char c = atual();
            if (c == '\n') throw new ErroLexico(linhaToken, "linha " + linhaToken + ": constante_string inválida");
            if (c == '"')  { sb.append('"'); pos++; return new Token(linhaToken, Token.Classe.CONSTANTE_STRING, sb.toString()); }
            if (c == '%') {
                pos++;
                if (pos >= tamanho || atual() == '\n')
                    throw new ErroLexico(linhaToken, "linha " + linhaToken + ": constante_string inválida");
                sb.append('%').append(atual());
                pos++;
                continue;
            }
            if (c == '\\' || c == '%') {
                throw new ErroLexico(linhaToken, "linha " + linhaToken + ": constante_string inválida");
            }
            sb.append(c);
            pos++;
        }
        throw new ErroLexico(linhaToken, "linha " + linhaToken + ": constante_string inválida");
    }

    /**
     * cte_char: \n | \s | \t  (barra invertida literal seguida de n, s ou t)
     */
    private Token lerChar() throws ErroLexico {
        int linhaToken = linha;
        pos++; // consome '\'
        if (pos >= tamanho) throw new ErroLexico(linhaToken, "linha " + linhaToken + ": constante_char inválida");

        char c = atual();
        if (c == 'n' || c == 's' || c == 't') {
            pos++;
            return new Token(linhaToken, Token.Classe.CONSTANTE_CHAR, "\\" + c);
        }
        throw new ErroLexico(linhaToken, "linha " + linhaToken + ": constante_char inválida");
    }

    // ── Números ───────────────────────────────────────────────────────────────

    private Token lerNumero() throws ErroLexico {
        int linhaToken = linha;
        StringBuilder sb = new StringBuilder();
        while (pos < tamanho && Character.isDigit(atual())) sb.append(fonte.charAt(pos++));

        // Float?
        if (pos < tamanho && atual() == '.') {
            sb.append('.');
            pos++;
            if (pos >= tamanho || !Character.isDigit(atual())) {
                while (pos < tamanho && !isSeparador(atual())) pos++;
                throw new ErroLexico(linhaToken, "linha " + linhaToken + ": constante_float inválida");
            }
            while (pos < tamanho && Character.isDigit(atual())) sb.append(fonte.charAt(pos++));
            if (pos < tamanho && (atual() >= 'a' && atual() <= 'z' || atual() == '_')) {
                while (pos < tamanho && !isSeparador(atual())) pos++;
                throw new ErroLexico(linhaToken, "linha " + linhaToken + ": constante_float inválida");
            }
            return new Token(linhaToken, Token.Classe.CONSTANTE_FLOAT, sb.toString());
        }

        // Letra colada = inválido
        if (pos < tamanho && (atual() >= 'a' && atual() <= 'z' || atual() == '_')) {
            while (pos < tamanho && !isSeparador(atual())) pos++;
            throw new ErroLexico(linhaToken, "linha " + linhaToken + ": identificador inválido");
        }

        return new Token(linhaToken, Token.Classe.CONSTANTE_INT, sb.toString());
    }

    // ── Identificadores ───────────────────────────────────────────────────────

    /**
     * identificador: [a-z] ( _[0-9]+ | [a-z] )*
     * Ou seja: começa com minúscula, seguido de (letra minúscula) ou (_dígitos).
     */
    private Token lerIdentificador() throws ErroLexico {
        int linhaToken = linha;
        StringBuilder sb = new StringBuilder();
        sb.append(atual()); pos++; // primeira letra minúscula

        while (pos < tamanho) {
            char c = atual();
            if (c >= 'a' && c <= 'z') {
                sb.append(c); pos++;
            } else if (c == '_') {
                // Deve ser seguido de um ou mais dígitos
                int savedPos = pos;
                sb.append('_'); pos++;
                if (pos >= tamanho || !Character.isDigit(atual())) {
                    // '_' sem dígitos depois → inválido
                    while (pos < tamanho && !isSeparador(atual())) pos++;
                    throw new ErroLexico(linhaToken, "linha " + linhaToken + ": identificador inválido");
                }
                while (pos < tamanho && Character.isDigit(atual())) { sb.append(atual()); pos++; }
                // Depois de _dígitos só pode vir separador, letra minúscula ou outro _
                if (pos < tamanho && atual() >= 'A' && atual() <= 'Z') {
                    while (pos < tamanho && !isSeparador(atual())) pos++;
                    throw new ErroLexico(linhaToken, "linha " + linhaToken + ": identificador inválido");
                }
            } else {
                break;
            }
        }

        String lexema = sb.toString();
        if (PALAVRAS_RESERVADAS.contains(lexema))
            return new Token(linhaToken, Token.Classe.PALAVRA_RESERVADA, lexema);
        return new Token(linhaToken, Token.Classe.IDENTIFICADOR, lexema);
    }

    // ── Símbolos especiais ────────────────────────────────────────────────────

    private Token lerSimbolo() throws ErroLexico {
        int linhaToken = linha;
        char c    = atual();
        char prox = (pos + 1 < tamanho) ? fonte.charAt(pos + 1) : 0;

        if (c == '<' && prox == '-') { pos += 2; return new Token(linhaToken, Token.Classe.SIMBOLO_ESPECIAL, "<-"); }
        if (c == '<' && prox == '=') { pos += 2; return new Token(linhaToken, Token.Classe.SIMBOLO_ESPECIAL, "<="); }
        if (c == '>' && prox == '=') { pos += 2; return new Token(linhaToken, Token.Classe.SIMBOLO_ESPECIAL, ">="); }
        if (c == '=' && prox == '=') { pos += 2; return new Token(linhaToken, Token.Classe.SIMBOLO_ESPECIAL, "=="); }
        if (c == '!' && prox == '=') { pos += 2; return new Token(linhaToken, Token.Classe.SIMBOLO_ESPECIAL, "!="); }
        if (c == '&' && prox == '&') { pos += 2; return new Token(linhaToken, Token.Classe.SIMBOLO_ESPECIAL, "&&"); }
        if (c == '|' && prox == '|') { pos += 2; return new Token(linhaToken, Token.Classe.SIMBOLO_ESPECIAL, "||"); }

        switch (c) {
            case ':': case ';': case ',': case '=': case '(':
            case ')': case '!': case '<': case '>': case '+':
            case '-': case '*': case '/': case '^':
                pos++;
                return new Token(linhaToken, Token.Classe.SIMBOLO_ESPECIAL, String.valueOf(c));
        }

        pos++;
        throw new ErroLexico(linhaToken, "linha " + linhaToken + ": " + c + " símbolo inválido");
    }

    // ── Utilitários ───────────────────────────────────────────────────────────

    private char atual() { return fonte.charAt(pos); }

    private boolean isSeparador(char c) {
        return Character.isWhitespace(c) || c == ';' || c == ',' || c == '('
                || c == ')' || c == ':' || c == '+' || c == '-' || c == '*'
                || c == '/' || c == '=' || c == '<' || c == '>' || c == '!'
                || c == '&' || c == '|' || c == '^' || c == '"' || c == '\''
                || c == '{' || c == '$';
    }
}