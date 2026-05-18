package compiler;

public interface Constants extends ScannerConstants, ParserConstants
{
    int EPSILON  = 0;
    int DOLLAR   = 1;

    int t_identificador = 2;
    int t_cte_int       = 3;
    int t_cte_float     = 4;
    int t_cte_char      = 5;
    int t_cte_string    = 6;
    int t_main          = 7;
    int t_define        = 8;
    int t_if            = 9;
    int t_elif          = 10;
    int t_else          = 11;
    int t_end           = 12;
    int t_repeat        = 13;
    int t_while         = 14;
    int t_until         = 15;
    int t_ask           = 16;
    int t_tell          = 17;
    int t_true          = 18;
    int t_false         = 19;
    int t_int           = 20;
    int t_float         = 21;
    int t_string        = 22;
    int t_bool          = 23;
    int t_char          = 24;
    int t_dois_pontos   = 25;  // :
    int t_ponto_virgula = 26;  // ;
    int t_virgula       = 27;  // ,
    int t_igual         = 28;  // =
    int t_atribuicao    = 29;  // <-
    int t_abre_par      = 30;  // (
    int t_fecha_par     = 31;  // )
    int t_e_logico      = 32;  // &&
    int t_ou_logico     = 33;  // ||
    int t_negacao       = 34;  // !
    int t_igual_igual   = 35;  // ==
    int t_diferente     = 36;  // !=
    int t_menor         = 37;  // <
    int t_menor_igual   = 38;  // <=
    int t_maior         = 39;  // >
    int t_maior_igual   = 40;  // >=
    int t_mais          = 41;  // +
    int t_menos         = 42;  // -
    int t_vezes         = 43;  // *
    int t_divisao       = 44;  // /
    int t_potencia      = 45;  // ^
}