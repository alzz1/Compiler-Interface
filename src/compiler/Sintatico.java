package compiler;

import java.util.Stack;

public class Sintatico implements Constants
{
    private Stack<Integer> stack = new Stack<>();
    private Token currentToken;
    private Token previousToken;
    private Lexico scanner;

    private static boolean isTerminal(int x)
    {
        return x < FIRST_NON_TERMINAL;
    }

    private static boolean isNonTerminal(int x)
    {
        return x >= FIRST_NON_TERMINAL && x < FIRST_SEMANTIC_ACTION;
    }

    private boolean step() throws LexicalError, SyntaticError
    {
        // Se não há mais tokens, representa EOF ($)
        if (currentToken == null)
        {
            int pos = (previousToken != null) ? previousToken.getPosition() : 1;
            currentToken = new Token(DOLLAR, "$", pos);
        }

        int x = stack.pop();
        int a = currentToken.getId();

        if (x == EPSILON)
        {
            return false;
        }
        else if (isTerminal(x))
        {
            if (x == a)
            {
                if (stack.empty())
                    return true;
                else
                {
                    previousToken = currentToken;
                    currentToken = scanner.nextToken();
                    return false;
                }
            }
            else
            {
                // Erro: terminal esperado não encontrado
                String msg = String.format(
                        "linha %d: " + PARSER_ERROR[x],
                        currentToken.getPosition(),
                        foundLabel(currentToken));
                throw new SyntaticError(msg, currentToken.getPosition());
            }
        }
        else if (isNonTerminal(x))
        {
            if (pushProduction(x, a))
                return false;
            else
            {
                // x é o índice direto em PARSER_ERROR
                // terminais: índices 0..45, não-terminais: índices 46+
                String msg = String.format(
                        "linha %d: " + PARSER_ERROR[x],
                        currentToken.getPosition(),
                        foundLabel(currentToken));
                throw new SyntaticError(msg, currentToken.getPosition());
            }
        }
        else
        {
            // Ação semântica — ignorada nesta fase
            return false;
        }
    }

    private boolean pushProduction(int topStack, int tokenInput)
    {
        int p = PARSER_TABLE[topStack - FIRST_NON_TERMINAL][tokenInput - 1];
        if (p >= 0)
        {
            int[] production = PRODUCTIONS[p];
            for (int i = production.length - 1; i >= 0; i--)
                stack.push(production[i]);
            return true;
        }
        return false;
    }

    /**
     * Retorna o rótulo do token encontrado conforme as regras do enunciado:
     *  - EOF ($)          → "EOF"
     *  - constante_string → "constante_string"
     *  - qualquer outro   → lexema (identificador, palavra reservada,
     *                               símbolo especial, constante_int,
     *                               constante_float, constante_char)
     */
    private String foundLabel(Token t)
    {
        if (t == null) return "EOF";
        int id = t.getId();
        if (id == DOLLAR)       return "EOF";
        if (id == t_cte_string) return "constante_string";
        return t.getLexeme();
    }

    public void parse(Lexico scanner) throws LexicalError, SyntaticError
    {
        this.scanner = scanner;

        stack.clear();
        stack.push(DOLLAR);
        stack.push(START_SYMBOL);

        currentToken = scanner.nextToken();

        while (!step())
            ;
    }
}