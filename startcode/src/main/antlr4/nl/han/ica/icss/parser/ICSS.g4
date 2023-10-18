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
astnode: variableAssignment | stylerule | declaration; // Ast node = alles. 
stylerule: selector OPEN_BRACE astnode* CLOSE_BRACE; // Stylerule is bijvoorbeeld h2 { color: #000000; }

classSelector: CLASS_IDENT; // Class selector is bijvoorbeeld .link
idSelector: ID_IDENT; // Id selector is bijvoorbeeld #link
tagSelector: LOWER_IDENT; // Tag selector is bijvoorbeeld h2
selector: classSelector | idSelector | tagSelector; // (',' selector)*

declaration: LOWER_IDENT COLON expression SEMICOLON; // Declaration is bijvoorbeeld color: #000000;
expression: literal; 
literal: COLOR | PIXELSIZE | PERCENTAGE | SCALAR | TRUE | FALSE | variableReference;

// Variable assignment is bijvoorbeeld UseLinkColor := FALSE;
variableName: CAPITAL_IDENT;
variableAssignment: variableName ASSIGNMENT_OPERATOR literal SEMICOLON;
variableReference: variableName; // Variable reference is bijvoorbeeld UseLinkColor