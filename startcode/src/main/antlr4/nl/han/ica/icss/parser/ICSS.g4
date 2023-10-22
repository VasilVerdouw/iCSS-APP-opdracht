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
astnode: variableAssignment | stylerule | declaration | ifClause; // Ast node = alles. 

stylerule: selector OPEN_BRACE astnode* CLOSE_BRACE; // Stylerule is bijvoorbeeld h2 { color: #000000; }

classSelector: CLASS_IDENT; // Class selector is bijvoorbeeld .link
idSelector: ID_IDENT; // Id selector is bijvoorbeeld #link
tagSelector: LOWER_IDENT; // Tag selector is bijvoorbeeld h2
selector: (tagSelector | classSelector | idSelector) (',' selector)*; // Selector is bijvoorbeeld h2, .link, #link 

propertyName: LOWER_IDENT;
declaration: propertyName COLON expression SEMICOLON; // Declaration is bijvoorbeeld color: #000000;

// Because recursion is only allowed within one rule we can't use the official AddOperation, MultiplyOperation, etc. rules.
expression: literal | variableReference | expression MUL expression | expression (PLUS | MIN) expression; // PA03: Door (MIN | PLUS) te scheiden van MUL wordt de volgorde juist toegepast

colorLiteral: COLOR;
pixelLiteral: PIXELSIZE;
percentageLiteral: PERCENTAGE;
scalarLiteral: SCALAR;
boolLiteral: TRUE | FALSE;
literal: colorLiteral | pixelLiteral | percentageLiteral | scalarLiteral | boolLiteral; // Literal is bijvoorbeeld #000000

// Variable assignment is bijvoorbeeld UseLinkColor := FALSE;
variableAssignment: variableReference ASSIGNMENT_OPERATOR expression SEMICOLON;
variableReference: CAPITAL_IDENT; // Variable reference is bijvoorbeeld UseLinkColor

// If clause is bijvoorbeeld if [UseLinkCoolor] { color: #000000; }
ifClause: IF BOX_BRACKET_OPEN (variableReference | boolLiteral) BOX_BRACKET_CLOSE OPEN_BRACE astnode* CLOSE_BRACE elseClause?;
elseClause: ELSE OPEN_BRACE astnode* CLOSE_BRACE; // PA04: Else clause toegevoegd
