grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
stylesheet: astnode*;
astnode: stylerule | declaration | variable_assignment; // Ast node = alles. 
stylerule: selector OPEN_BRACE astnode* CLOSE_BRACE; // Stylerule is bijvoorbeeld h2 { color: #000000; }
selector: CLASS_IDENT | ID_IDENT | LOWER_IDENT; // | CAPITAL_IDENT; // Selector is bijvoorbeeld h2
declaration: LOWER_IDENT COLON expression SEMICOLON; // Declaration is bijvoorbeeld color: #000000;
expression: literal; 
literal: COLOR | PIXELSIZE | PERCENTAGE | SCALAR | TRUE | FALSE;

// Variable assignment is bijvoorbeeld UseLinkColor := FALSE;
variable_assignment: CAPITAL_IDENT ASSIGNMENT_OPERATOR literal SEMICOLON;