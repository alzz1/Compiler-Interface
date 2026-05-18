package compiler;

public interface Constants extends ScannerConstants
{
    int EPSILON  = 0;
    int DOLLAR   = 1;

    int t_identificador       = 2;
    int t_cte_int             = 3;
    int t_cte_float           = 4;
    int t_cte_char            = 5;
    int t_cte_string          = 6;
    int t_comentario_de_linha = 7;
    int t_comentario_de_bloco = 8;

    // Palavras reservadas
    int t_ask    = 9;
    int t_bool   = 10;
    int t_char   = 11;
    int t_define = 12;
    int t_end    = 13;
    int t_elif   = 14;
    int t_else   = 15;
    int t_false  = 16;
    int t_float  = 17;
    int t_if     = 18;
    int t_int    = 19;
    int t_main   = 20;
    int t_repeat = 21;
    int t_string = 22;
    int t_tell   = 23;
    int t_true   = 24;
    int t_until  = 25;
    int t_while  = 26;

    // Símbolos especiais
    int t_dois_pontos   = 27;  // :
    int t_ponto_virgula = 28;  // ;
    int t_virgula       = 29;  // ,
    int t_igual         = 30;  // =
    int t_atribuicao    = 31;  // <-
    int t_abre_par      = 32;  // (
    int t_fecha_par     = 33;  // )
    int t_e_logico      = 34;  // &&
    int t_ou_logico     = 35;  // ||
    int t_negacao       = 36;  // !
    int t_igual_igual   = 37;  // ==
    int t_diferente     = 38;  // !=
    int t_menor         = 39;  //
    int t_menor_igual   = 40;  // <=
    int t_maior         = 41;  // >
    int t_maior_igual   = 42;  // >=
    int t_mais          = 43;  // +
    int t_menos         = 44;  // -
    int t_vezes         = 45;  // *
    int t_divisao       = 46;  // /
    int t_potencia      = 47;  // ^
}