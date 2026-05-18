package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexico implements Constants
{
    private static final Map<String, Integer> PALAVRAS_RESERVADAS = new HashMap<>();
    static {
        PALAVRAS_RESERVADAS.put("main",   t_main);
        PALAVRAS_RESERVADAS.put("define", t_define);
        PALAVRAS_RESERVADAS.put("if",     t_if);
        PALAVRAS_RESERVADAS.put("elif",   t_elif);
        PALAVRAS_RESERVADAS.put("else",   t_else);
        PALAVRAS_RESERVADAS.put("end",    t_end);
        PALAVRAS_RESERVADAS.put("repeat", t_repeat);
        PALAVRAS_RESERVADAS.put("while",  t_while);
        PALAVRAS_RESERVADAS.put("until",  t_until);
        PALAVRAS_RESERVADAS.put("ask",    t_ask);
        PALAVRAS_RESERVADAS.put("tell",   t_tell);
        PALAVRAS_RESERVADAS.put("true",   t_true);
        PALAVRAS_RESERVADAS.put("false",  t_false);
        PALAVRAS_RESERVADAS.put("int",    t_int);
        PALAVRAS_RESERVADAS.put("float",  t_float);
        PALAVRAS_RESERVADAS.put("string", t_string);
        PALAVRAS_RESERVADAS.put("bool",   t_bool);
        PALAVRAS_RESERVADAS.put("char",   t_char);
    }

    private List<Token> tokensPreProcessados;
    private int         tokenIndex;
    private LexicalError erroLexico = null;

    public Lexico() {}

    public Lexico(java.io.Reader reader) { setInput(reader); }

    public void setInput(java.io.Reader reader)
    {
        StringBuilder bfr = new StringBuilder();
        try
        {
            int c = reader.read();
            while (c != -1) { bfr.append((char) c); c = reader.read(); }
        }
        catch (java.io.IOException e) { e.printStackTrace(); }
        setInput(bfr.toString());
    }

    public void setInput(String src)
    {
        src = src.replace("\r\n", "\n").replace("\r", "\n");
        tokensPreProcessados = new ArrayList<>();
        tokenIndex = 0;
        erroLexico = null;
        try {
            processarTexto(src);
        } catch (LexicalError e) {
            erroLexico = e;
        }
    }

    private void processarTexto(String src) throws LexicalError
    {
        int i     = 0;
        int linha = 1;

        while (i < src.length())
        {
            char c = src.charAt(i);

            // Espaços e tabulações
            if (c == ' ' || c == '\t') { i++; continue; }

            // Quebra de linha
            if (c == '\n') { linha++; i++; continue; }

            // Comentário de linha: $ até \n
            if (c == '$')
            {
                while (i < src.length() && src.charAt(i) != '\n') i++;
                continue;
            }

            // Comentário de bloco: { \n ... \n }
            if (c == '{')
            {
                int linhaInicio = linha;
                i++;
                if (i >= src.length() || src.charAt(i) != '\n')
                    throw new LexicalError("linha " + linhaInicio + ": comentário inválido ou não finalizado", linhaInicio);
                i++; linha++;

                boolean fechou = false;
                while (i < src.length())
                {
                    char cc = src.charAt(i);
                    if (cc == '\n')
                    {
                        i++; linha++;
                        if (i < src.length() && src.charAt(i) == '}')
                        {
                            i++;
                            fechou = true;
                            break;
                        }
                    }
                    else if (cc == '{')
                    {
                        throw new LexicalError("linha " + linhaInicio + ": comentário inválido ou não finalizado", linhaInicio);
                    }
                    else { i++; }
                }
                if (!fechou)
                    throw new LexicalError("linha " + linhaInicio + ": comentário inválido ou não finalizado", linhaInicio);
                continue;
            }

            // <- atribuição
            if (c == '<' && i + 1 < src.length() && src.charAt(i + 1) == '-')
            {
                tokensPreProcessados.add(new Token(t_atribuicao, "<-", linha));
                i += 2;
                continue;
            }

            // Operadores de dois caracteres
            if (i + 1 < src.length())
            {
                String dois = "" + c + src.charAt(i + 1);
                Integer id = simboloDoisChars(dois);
                if (id != null)
                {
                    tokensPreProcessados.add(new Token(id, dois, linha));
                    i += 2;
                    continue;
                }
            }

            // Operadores de um caractere
            Integer idSimples = simboloUmChar(c);
            if (idSimples != null)
            {
                tokensPreProcessados.add(new Token(idSimples, String.valueOf(c), linha));
                i++;
                continue;
            }

            // constante_int ou constante_float
            if (Character.isDigit(c))
            {
                int start = i;
                while (i < src.length() && Character.isDigit(src.charAt(i))) i++;
                if (i < src.length() && src.charAt(i) == '.')
                {
                    i++;
                    if (i < src.length() && Character.isDigit(src.charAt(i)))
                    {
                        while (i < src.length() && Character.isDigit(src.charAt(i))) i++;
                        tokensPreProcessados.add(new Token(t_cte_float, src.substring(start, i), linha));
                    }
                    else
                    {
                        throw new LexicalError("linha " + linha + ": constante_float inválida", linha);
                    }
                }
                else
                {
                    tokensPreProcessados.add(new Token(t_cte_int, src.substring(start, i), linha));
                }
                continue;
            }

            // Identificador ou palavra reservada
            if (Character.isLowerCase(c))
            {
                int start = i;
                i++;
                while (i < src.length())
                {
                    char nc = src.charAt(i);
                    if (Character.isLowerCase(nc)) { i++; continue; }
                    if (nc == '_')
                    {
                        i++;
                        if (i >= src.length() || !Character.isDigit(src.charAt(i)))
                            throw new LexicalError("linha " + linha + ": identificador inválido", linha);
                        while (i < src.length() && Character.isDigit(src.charAt(i))) i++;
                        continue;
                    }
                    break;
                }
                String lexema = src.substring(start, i);
                if (PALAVRAS_RESERVADAS.containsKey(lexema))
                    tokensPreProcessados.add(new Token(PALAVRAS_RESERVADAS.get(lexema), lexema, linha));
                else
                    tokensPreProcessados.add(new Token(t_identificador, lexema, linha));
                continue;
            }

            // constante_char: \n \s \t
            if (c == '\\')
            {
                i++;
                if (i < src.length() && (src.charAt(i) == 'n' || src.charAt(i) == 's' || src.charAt(i) == 't'))
                {
                    tokensPreProcessados.add(new Token(t_cte_char, "\\" + src.charAt(i), linha));
                    i++;
                }
                else
                    throw new LexicalError("linha " + linha + ": constante_char inválida", linha);
                continue;
            }

            // constante_string
            if (c == '"')
            {
                int linhaStr = linha;
                i++;
                StringBuilder sb = new StringBuilder("\"");
                boolean fechou = false;
                while (i < src.length())
                {
                    char sc = src.charAt(i);
                    if (sc == '\n')
                        throw new LexicalError("linha " + linhaStr + ": constante_string inválida", linhaStr);
                    if (sc == '"') { sb.append('"'); i++; fechou = true; break; }
                    if (sc == '%')
                    {
                        i++;
                        if (i >= src.length() || src.charAt(i) == '\n')
                            throw new LexicalError("linha " + linhaStr + ": constante_string inválida", linhaStr);
                        sb.append('%').append(src.charAt(i)); i++;
                        continue;
                    }
                    if (sc == '\\')
                        throw new LexicalError("linha " + linhaStr + ": constante_string inválida", linhaStr);
                    sb.append(sc); i++;
                }
                if (!fechou)
                    throw new LexicalError("linha " + linhaStr + ": constante_string inválida", linhaStr);
                tokensPreProcessados.add(new Token(t_cte_string, sb.toString(), linhaStr));
                continue;
            }

            // Letra maiúscula = identificador inválido
            if (Character.isUpperCase(c))
            {
                while (i < src.length() && !Character.isWhitespace(src.charAt(i))) i++;
                throw new LexicalError("linha " + linha + ": identificador inválido", linha);
            }

            // Símbolo inválido
            throw new LexicalError("linha " + linha + ": " + c + " símbolo inválido", linha);
        }
    }

    private Integer simboloDoisChars(String s)
    {
        switch (s)
        {
            case "==": return t_igual_igual;
            case "!=": return t_diferente;
            case "<=": return t_menor_igual;
            case ">=": return t_maior_igual;
            case "&&": return t_e_logico;
            case "||": return t_ou_logico;
            default:   return null;
        }
    }

    private Integer simboloUmChar(char c)
    {
        switch (c)
        {
            case ':': return t_dois_pontos;
            case ';': return t_ponto_virgula;
            case ',': return t_virgula;
            case '=': return t_igual;
            case '(': return t_abre_par;
            case ')': return t_fecha_par;
            case '!': return t_negacao;
            case '<': return t_menor;
            case '>': return t_maior;
            case '+': return t_mais;
            case '-': return t_menos;
            case '*': return t_vezes;
            case '/': return t_divisao;
            case '^': return t_potencia;
            default:  return null;
        }
    }

    public Token nextToken() throws LexicalError
    {
        if (erroLexico != null) throw erroLexico;
        if (tokenIndex >= tokensPreProcessados.size()) return null;
        return tokensPreProcessados.get(tokenIndex++);
    }

    public void setPosition(int pos) { tokenIndex = pos; }

    public static String nomeClasse(int tokenId)
    {
        switch (tokenId)
        {
            case t_identificador: return "identificador";
            case t_cte_int:       return "constante_int";
            case t_cte_float:     return "constante_float";
            case t_cte_char:      return "constante_char";
            case t_cte_string:    return "constante_string";
            case t_main: case t_define: case t_if: case t_elif: case t_else:
            case t_end: case t_repeat: case t_while: case t_until: case t_ask:
            case t_tell: case t_true: case t_false: case t_int: case t_float:
            case t_string: case t_bool: case t_char:
            return "palavra reservada";
            default:
                return "símbolo especial";
        }
    }
}