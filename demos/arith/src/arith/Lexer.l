package arith;

import arith.Location;
import arith.error.*;

%%
%public
%class Lexer
%extends BaseLexer
%byaccj
%line
%column
%switch
%unicode

%{
	private Location sloc = null;
	private StringBuilder buffer = new StringBuilder();

	public Location getLocation() {
		return new Location(yyline + 1, yycolumn + 1);
	}
%}

NEWLINE				= (\r|\n|\r\n)
DIGIT 				= ([0-9])
INTEGER             = ({DIGIT}+)
OPERATOR    		= ("+"|"*"|"("|")")
WHITESPACE			= ([ \t]+)

%x S

%%

{WHITESPACE}		{ /* Just ignore */    }
{NEWLINE}			{ /* Just ignore */    }

{OPERATOR}  	    { return operator(yycharat(0));			}

{INTEGER}			{ return intConst(yytext());			}

.					{ issueError(new UnrecogCharError(getLocation(), yycharat(0))); 		}
