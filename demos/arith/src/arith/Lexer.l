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

	// 开始条件S表示字符串
%x S

%%
	// 识别注释和空白字符的规则
{WHITESPACE}		{ /* Just ignore */    }
{NEWLINE}			{ /* Just ignore */    }

{OPERATOR}  	    { return operator(yycharat(0));			}

	// 识别常数的规则
{INTEGER}			{ return intConst(yytext());			}

	// 上面规则不能识别的字符怎么处理
.					{ issueError(new UnrecogCharError(getLocation(), yycharat(0))); 		}
