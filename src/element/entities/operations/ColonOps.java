package element.entities.operations;

import static element.ElemTypes.bothList;
import static element.ElemTypes.getString;
import static element.ElemTypes.isBlock;
import static element.ElemTypes.isChar;
import static element.ElemTypes.isList;
import static element.ElemTypes.isNumeric;
import static element.ElemTypes.isString;
import static element.ElemTypes.toBlock;
import static element.ElemTypes.toChar;
import static element.ElemTypes.toList;
import static element.ElemTypes.toNumeric;

import java.util.ArrayList;

import element.Element;
import element.entities.Block;
import element.entities.Operation;
import element.exceptions.ElementRuntimeException;
import element.exceptions.SyntaxError;
import element.exceptions.TypeError;
import element.parser.CharacterParser;
import element.parser.Parser;
import element.variable.Variable;

public class ColonOps {	
	
	public static final char FIRST_OP = '!';

	
	/** A list of all valid single character operations. 
	 *  Stored in final array for fast lookup.
	 *  Array indexes are always [(operator character) - FIRST_OP]
	 */
	public static Operation[] COLON_OPS = {
		/* 33 !  */ null,
		/* 34 "  */ null,
		/* 35 #  */ null,
		/* 36 $  */ null,
		/* 37 %  */ null,
		/* 38 &  */ null,
		/* 39 '  */ null,
		/* 40 (  */ null, //List item assignment
		/* 41 )  */ null,
		/* 42 *  */ null,
		/* 43 +  */ null,
		/* 44 ,  */ null,
		/* 45 -  */ new OP_SetMinus(),
		/* 46 .  */ null,
		/* 47 /  */ null,
		/* 48 0  */ null, //Number Literal
		/* 49 1  */ null, //Number Literal
		/* 50 2  */ null, //Number Literal
		/* 51 3  */ null, //Number Literal
		/* 52 4  */ null, //Number Literal
		/* 53 5  */ null, //Number Literal
		/* 54 6  */ null, //Number Literal
		/* 55 7  */ null, //Number Literal
		/* 56 8  */ null, //Number Literal
		/* 57 9  */ null, //Number Literal
		/* 58    */ null,
		/* 59 ;  */ null,
		/* 60 <  */ null,
		/* 61 =  */ null,
		/* 62 >  */ null,
		/* 63 ?  */ null,
		/* 64 @  */ null,
		/* 65 A  */ null,
		/* 66 B  */ null,
		/* 67 C  */ null,
		/* 68 D  */ null,
		/* 69 E  */ null,
		/* 70 F  */ null,
		/* 71 G  */ null,
		/* 72 H  */ null,
		/* 73 I  */ null,
		/* 74 J  */ null,
		/* 75 K  */ null,
		/* 76 L  */ null,
		/* 77 M  */ null,
		/* 78 N  */ null,
		/* 79 O  */ null,
		/* 80 P  */ null,
		/* 81 Q  */ null,
		/* 82 R  */ null,
		/* 83 S  */ null,
		/* 84 T  */ null,
		/* 85 U  */ null,
		/* 86 V  */ null,
		/* 87 W  */ null,
		/* 88 X  */ null,
		/* 89 Y  */ null,
		/* 90 Z  */ null,
		/* 91 [  */ null,
		/* 92 \  */ null,
		/* 93 ]  */ null,
		/* 94 ^  */ null,
		/* 95 _  */ new OP_Colon_Underscore(),
		/* 96 `  */ null,
		/* 97 a  */ null, // Assignment
		/* 98 b  */ null, // Assignment
		/* 99 c  */ null, // Assignment
		/* 100 d */ null, // Assignment
		/* 101 e */ null, // Assignment
		/* 102 f */ null, // Assignment
		/* 103 g */ null, // Assignment
		/* 104 h */ null, // Assignment
		/* 105 i */ null, // Assignment
		/* 106 j */ null, // Assignment
		/* 107 k */ null, // Assignment
		/* 108 l */ null, // Assignment
		/* 109 m */ null, // Assignment
		/* 110 n */ null, // Assignment
		/* 111 o */ null, // Assignment
		/* 112 p */ null, // Assignment
		/* 113 q */ null, // Assignment
		/* 114 r */ null, // Assignment
		/* 115 s */ null, // Assignment
		/* 116 t */ null, // Assignment
		/* 117 u */ null, // Assignment
		/* 118 v */ null, // Assignment
		/* 119 w */ null, // Assignment
		/* 120 x */ null, // Assignment
		/* 121 y */ null, // Assignment
		/* 122 z */ null, // Assignment
		/* 123 { */ null,
		/* 124 | */ null,
		/* 125 } */ null,
		/* 126 ~ */ new OP_Colon_Tilde()
	};
	
	
	/** Returns a list of all the op descriptions **/
	public static ArrayList<String> getAllOpDescriptions() {
		ArrayList<String> out = new ArrayList<String>();
		for (char i = 0; i <= 126-Ops.FIRST_OP; i++) {
			if(COLON_OPS[i] != null) {
				out.add(COLON_OPS[i].name + " (" + COLON_OPS[i].argTypes + ")\n" + COLON_OPS[i].info + "\n(colon operator)");
			}
		}
		return out;
	}
	
	
	/** Returns the operation bound to the character */
	public static Operation getOp(char op) {
		if(op >= 33 && op <= 126) {
			return COLON_OPS[op-FIRST_OP];
		} else {
			throw new SyntaxError("Colon operator ':" + op + "' does not exist");
		}
	}
	
	public static boolean isColonOpChar(char c) {
		//A char is a colonOp if it is not a lowercase letter or a '('
		return ((c >= '!' && c < 'a') || (c > 'z' && c <= '~')) && c != '(' && c != ' ';
	}
	
}


//- - 45
class OP_SetMinus extends Operation {
	public OP_SetMinus() {
		this.name = ":-";
		this.info = "LL remove all elements in L2 from L1";
		this.argTypes = "LL";
	}
	@Override
	public void execute(Block block) {
		Object a = block.pop();
		Object b = block.pop();
		
		if (bothList(a,b)) {
			ListOperations.removeObjects(toList(b), toList(a));
			block.push(b);
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

//_ - 95
class OP_Colon_Underscore extends Operation {
	public OP_Colon_Underscore() {
		this.name = ":_";
		this.info = "copies the first N items on the stack (not including N)";
		this.argTypes = "N";
	}
	@Override public void execute (final Block block) {
		final Object a = block.pop();
		
		if (isNumeric(a)) {
			int size = block.getStack().size();
			int i = toNumeric(a).toInt();
			
			if (i > size || i <= 0) {
				throw new ElementRuntimeException(i + " :_ stack index out of bounds");
			} else {
				
				while (i > 0) {
					final Object cp = block.getStack().get(size - i);
					
					if(cp instanceof ArrayList) {
						block.push(toList(cp).clone());
					} else {
						block.push(cp);
					}
				i--;
				}
				
			}
			
		} else {
			
		}
	}
}

//~ - 126
class OP_Colon_Tilde extends Operation {
	public OP_Colon_Tilde() {
		this.name = ":~";
		this.info = "remove duplicate items from a list";
		this.argTypes = "L";
	}
	@Override
	public void execute(final Block block) {
		final Object a = block.pop();
		
		if (isList(a)) {
			block.push(ListOperations.removeDuplicates(toList(a)));
		} else {
			throw new TypeError(this, a);
		}
	}
}
	