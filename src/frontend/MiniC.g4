grammar MiniC;
// productions
prog:   funcDecl* EOF # ProgRoot
    ;

type : 'int';

funcDecl: type ID '(' params ')' funcBody;
funcBody: '{' varDecl* statement*  returnStm '}';

params : ( param (',' param)* )?;
param : type ID ;

varDecl : type ID (',' ID)* ';' ;

statement
    : '{' statement* '}'                                 # StmComp
    | 'if' '(' cond=expr ')' then=statement 'else' else=statement   # StmIf
    | 'while' '(' cond=expr ')' body=statement                      # StmWhile
    | ID '=' expr ';'                                    # StmAssign
    ;

returnStm: 'return' expr ';';

expr:   ID	                                          # ExpID
    |   INT                                           # ExpInt
    |   ID '(' (expr (',' expr)*)? ')'                # ExpCall
    |   left=expr op=('*'|'/') right=expr             # ExpMulOrDiv
    |	left=expr op=('+'|'-') right=expr             # ExpAddOrSub
    |   left=expr op=('>'|'>='|'<'|'<=') right=expr   # ExpLeOrGe
    |   left=expr op=('=='|'!=') right=expr           # ExpEqOrNe
    |	'(' expr ')'                                  # ExpParen
    ;

// lexical rules
NEWLINE : [\r\n]+ -> skip;
INT     : ('+' | '-')?[0-9]+ ;
ID      : [a-zA-Z_][a-zA-Z_0-9]* ;
WS      : [ \t\r\n]+ -> skip ; // 跳过空白字符
LC      : '//' ~[\r\n]* '\r'? '\n' -> skip; // 跳过单行注释
BC      : '/*' .*? '*/' -> skip;            // 跳过多行注释
