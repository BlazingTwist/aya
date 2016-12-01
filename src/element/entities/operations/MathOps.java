package element.entities.operations;

import static element.ElemTypes.bothNumeric;
import static element.ElemTypes.bothString;
import static element.ElemTypes.castString;
import static element.ElemTypes.getString;
import static element.ElemTypes.isBigNum;
import static element.ElemTypes.isChar;
import static element.ElemTypes.isList;
import static element.ElemTypes.isModule;
import static element.ElemTypes.isNum;
import static element.ElemTypes.isNumeric;
import static element.ElemTypes.isString;
import static element.ElemTypes.isUserObject;
import static element.ElemTypes.show;
import static element.ElemTypes.toBigNum;
import static element.ElemTypes.toChar;
import static element.ElemTypes.toList;
import static element.ElemTypes.toModule;
import static element.ElemTypes.toNumeric;
import static element.ElemTypes.toUserObject;

import java.awt.Color;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import element.ElemPrefs;
import element.ElemTypes;
import element.Element;
import element.entities.Block;
import element.entities.Operation;
import element.entities.UserObject;
import element.entities.number.Num;
import element.exceptions.ElementRuntimeException;
import element.exceptions.TypeError;
import element.parser.CharacterParser;
import element.util.ChartParams;
import element.util.FreeChartInterface;
import element.util.QuickDialog;

public class MathOps {	

	
	public static char FIRST_OP = '!';
	
	/** A list of all valid single character operations. 
	 *  Stored in final array for fast lookup.
	 *  Array indexes are always [(operator character) - FIRST_OP]
	 */
	public static Operation[] MATH_OPS = {
		/* 33 !  */ new OP_Fact(),
		/* 34 "  */ null,
		/* 35 #  */ null,
		/* 36 $  */ new OP_SysTime(),
		/* 37 %  */ null,
		/* 38 &  */ null,
		/* 39 '  */ null,
		/* 40 (  */ null,
		/* 41 )  */ null,
		/* 42 *  */ null,
		/* 43 +  */ null,
		/* 44 ,  */ null,
		/* 45 -  */ null,
		/* 46 .  */ null,
		/* 47 /  */ null,
		/* 48 0  */ null,
		/* 49 1  */ null,
		/* 50 2  */ null,
		/* 51 3  */ null,
		/* 52 4  */ null,
		/* 53 5  */ null,
		/* 54 6  */ null,
		/* 55 7  */ null,
		/* 56 8  */ null,
		/* 57 9  */ null,
		/* 58    */ null,
		/* 59 ;  */ null,
		/* 60 <  */ new OP_ModSet(),
		/* 61 =  */ null,
		/* 62 >  */ new OP_ModGet(),
		/* 63 ?  */ null,
		/* 64 @  */ null,
		/* 65 A  */ new OP_Abs(),
		/* 66 B  */ null,
		/* 67 C  */ new OP_Acosine(),
		/* 68 D  */ new OP_MDate(),
		/* 69 E  */ null, //new OP_ScientificNotation(),
		/* 70 F  */ null,
		/* 71 G  */ null,
		/* 72 H  */ new OP_MParse_Date(),
		/* 73 I  */ null,
		/* 74 J  */ null,
		/* 75 K  */ null,
		/* 76 L  */ new OP_Log(),
		/* 77 M  */ null,
		/* 78 N  */ null,
		/* 79 O  */ new OP_NewUserObject(),
		/* 80 P  */ new OP_PrintColor(),
		/* 81 Q  */ null,
		/* 82 R  */ null,
		/* 83 S  */ new OP_Asine(),
		/* 84 T  */ new OP_Atangent(),
		/* 85 U  */ null,
		/* 86 V  */ new OP_Dialog(),
		/* 87 W  */ null,
		/* 88 X  */ new OP_AdvPlot(),
		/* 89 Y  */ null,
		/* 90 Z  */ new OP_SysConfig(),
		/* 91 [  */ null, //Matrix Literal
		/* 92 \  */ null,
		/* 93 ]  */ null, //No used for matrix literal, but ptobably shouldnt be used for anything
		/* 94 ^  */ null,
		/* 95 _  */ null,
		/* 96 `  */ null,
		/* 97 a  */ null,
		/* 98 b  */ null,
		/* 99 c  */ new OP_Cosine(),
		/* 100 d */ new OP_CastDouble(),
		/* 101 e */ null,
		/* 102 f */ null,
		/* 103 g */ null,
		/* 104 h */ new OP_MShow_Date(),
		/* 105 i */ null, //new OP_CastInt(),
		/* 106 j */ null,
		/* 107 k */ new OP_AddParserChar(),
		/* 108 l */ new OP_Ln(),
		/* 109 m */ null,
		/* 110 n */ null,
		/* 111 o */ null,
		/* 112 p */ null,
		/* 113 q */ new OP_SquareRoot(),
		/* 114 r */ null,
		/* 115 s */ new OP_Sine(),
		/* 116 t */ new OP_Tangent(),
		/* 117 u */ null,
		/* 118 v */ null,
		/* 119 w */ new OP_TypeStr(),
		/* 120 x */ null,
		/* 121 y */ null,
		/* 122 z */ null,
		/* 123 { */ null,
		/* 124 | */ new OP_Constants(),
		/* 125 } */ null,
		/* 126 ~ */ null,
	};
	
	/** Returns a list of all the op descriptions **/
	public static ArrayList<String> getAllOpDescriptions() {
		ArrayList<String> out = new ArrayList<String>();
		for (char i = 0; i <= 126-Ops.FIRST_OP; i++) {
			if(MATH_OPS[i] != null) {
				out.add(MATH_OPS[i].name + " (" + MATH_OPS[i].argTypes + ")\n" + MATH_OPS[i].info + "\n(misc. operator)");
			}
		}
		return out;
	}
	
	/** Returns the operation bound to the character */
	public static Operation getOp(char op) {
		if(op >= 33 && op <= 126) {
			return MATH_OPS[op-FIRST_OP];
		} else {
			throw new ElementRuntimeException("Misc. operator 'M" + op + "' does not exist");
		}
	}
	
}

//! - 33
class OP_Fact extends Operation {
	public OP_Fact() {
		this.name = "M!";
		this.info = "factorial";
		this.argTypes = "N";
	}
	@Override
	public void execute(Block block) {
		Object n = block.pop();
		if(isNumeric(n)){
			block.push(toNumeric(n).factorial());
		} else {
			throw new TypeError(this.name, this.argTypes, n);
		}
	}
}

//! - 33
class OP_SysTime extends Operation {
	public OP_SysTime() {
		this.name = "M$";
		this.info = "system time in milliseconds as a double";
		this.argTypes = "";
	}
	@Override
	public void execute(Block block) {
		block.push(new Num(System.currentTimeMillis()));
	}
}

//< - 60 
class OP_ModSet extends Operation {
	public OP_ModSet() {
		this.name = "M<";
		this.info = "set a field from a user object";
		this.argTypes = "AUI";
	}
	@Override
	public void execute(Block block) {
		final Object index = block.pop();
		final Object list = block.pop();
		final Object o = block.pop();
		
		//Check if user object
		if (isUserObject(list) && isNumeric(index)) {
			final int ix = toNumeric(index).toInt();
			UserObject user_obj = toUserObject(list);
			if (ix <= user_obj.fieldCount() && ix>0) {
				user_obj.setField(ix-1, o);
				return;
			} else {
				throw new ElementRuntimeException("I: User object (" 
							+ user_obj.getModule().toString() + "): field " + ix 
							+ " cannot be set");
			}
		}
				
		throw new TypeError(this.name, this.argTypes, index, list, o);
	}
}


//> - 62
class OP_ModGet extends Operation {
	public OP_ModGet() {
		this.name = "M>";
		this.info = "get a field from a user object";
		this.argTypes = "UI";
	}
	@Override
	public void execute(Block block) {
		final Object index = block.pop();
		final Object list = block.pop();
		
		//Check if user object
		if (isUserObject(list) && isNumeric(index)) {
			final int ix = toNumeric(index).toInt();
			if (ix == 0) {
				block.push(toUserObject(list).getModule());
				return;
			} else {
				UserObject user_obj = toUserObject(list);
				if (ix <= user_obj.fieldCount() && ix>0) {
					block.push(toUserObject(list).getField(ix-1));
					return;
				} else {
					throw new ElementRuntimeException("I: User object (" 
								+ user_obj.getModule().toString() + "): field " + ix 
								+ " does not exist.");
				}
			}
		}
				
		throw new TypeError(this.name, this.argTypes, index, list);
	}
}

// A - 65
class OP_Abs extends Operation {
	public OP_Abs() {
		this.name = "MA";
		this.info = "absolute value";
		this.argTypes = "N";
	}
	@Override
	public void execute(Block block) {
		Object n = block.pop();
		
		if(isNumeric(n)) {
			block.push(toNumeric(n).abs());
		} else {
			throw new TypeError(this.name, this.argTypes, n);
		}
	}
}

// C - 67
class OP_Acosine extends Operation {
	public OP_Acosine() {
		this.name = "MC";
		this.info = "trigonometric inverse cosine";
		this.argTypes = "N";
	}
	@Override
	public void execute(Block block) {
		Object n = block.pop();
		if(isNumeric(n)) {
			block.push(toNumeric(n).acos());
		} else {
			throw new TypeError(this.name, this.argTypes, n);
		}
	}
}

//D - 68
class OP_MDate extends Operation {
	private Calendar cal = Calendar.getInstance();

	public OP_MDate() {
		this.name = "MD";
		this.info = "input time in ms (M$) and return date params [day_of_week, year, month, day_of_month, hour, min, s]";
		this.argTypes = "N";
	}
	@Override
	public void execute(Block block) {
		Object a = block.pop();
		if (isNumeric(a)) {
			long timeStamp = toNumeric(a).toLong();
			cal.setTimeInMillis(timeStamp);
			
			ArrayList<Object> fields = new ArrayList<Object>();
			
			fields.add(cal.get(Calendar.DAY_OF_WEEK));
			fields.add(cal.get(Calendar.YEAR));
			fields.add(cal.get(Calendar.MONTH));
			fields.add(cal.get(Calendar.DAY_OF_MONTH));
			fields.add(cal.get(Calendar.HOUR));
			fields.add(cal.get(Calendar.MINUTE));
			fields.add(cal.get(Calendar.SECOND));

			block.push(fields);
		} else {
			throw new TypeError(this.name, this.argTypes, a);
		}
		
	}
}


//H - 68
class OP_MParse_Date extends Operation {

	public OP_MParse_Date() {
		this.name = "MH";
		this.info = "parse a date using a given format and return the time in ms";
		this.argTypes = "SS";
	}
	@Override
	public void execute(Block block) {
		Object a = block.pop();
		Object b = block.pop();
		
		
		if (bothString(a,b)) {
			String df_str = castString(a);
			String date_str = castString(b);
			
			DateFormat df;
			try {
				df = new SimpleDateFormat(df_str, Locale.ENGLISH);
			} catch (IllegalArgumentException e) {
				throw new ElementRuntimeException("Invalid date format: '" + df_str + "'");
			}
			
			Date date;
			try {
				date = df.parse(date_str);
			} catch (ParseException e) {
				throw new ElementRuntimeException("Cannot parse date: '" + date_str + "' as '" + df_str + "'");
			}
			block.push(new Num(date.getTime()));
		} else {
			throw new TypeError(this.name, this.argTypes, a, b);
		}
		
	}
}


//l - 76
class OP_Log extends Operation {

	public OP_Log() {
		this.name = "ML";
		this.info = "base-10 logarithm";
		this.argTypes = "N";
	}
	@Override
	public void execute(Block block) {
		Object a = block.pop();
		if(isNumeric(a)) {
			block.push(toNumeric(a).log());
		} else {
			throw new TypeError(this, a);
		}
	}
}

//P - 79
class OP_NewUserObject extends Operation {
	public OP_NewUserObject() {
		this.name = "MO";
		this.info = "create a new user object";
		this.argTypes = "LM";
	}
	@Override
	public void execute(Block block) {
		final Object mod = block.pop();
		final Object list = block.pop();

		//<list> <module> MO
		if(isModule(mod) && isList(list) && !isString(list)) {
			block.push(new UserObject(toModule(mod), toList(list)));
			return;
		}
		
		
		throw new TypeError(this.name, this.argTypes, list, mod);
	}
}

//P - 80
class OP_PrintColor extends Operation {
	public OP_PrintColor() {
		this.name = "MP";
		this.info = "print a string to the console with the given color";
		this.argTypes = "SIII";
	}
	@Override
	public void execute(Block block) {
		final Object a = block.pop();
		final Object b = block.pop();
		final Object c = block.pop();
		final Object d = block.pop();
		
		if(bothNumeric(c,b) & isNumeric(a)) {
			try {
				Element.getInstance().getOut().printColor(castString(d), new Color(toNumeric(c).toInt(), toNumeric(b).toInt(), toNumeric(a).toInt()));
			} catch (IllegalArgumentException e) {
				throw new ElementRuntimeException("Cannot print using color (" + toNumeric(c).toInt() + ", " + toNumeric(b).toInt() + ", " + toNumeric(a).toInt() + ")" );
			}
			return;
		}
		
		throw new TypeError(this.name, this.argTypes, a,b,c,d);
	}
}

// S - 83
class OP_Asine extends Operation {
	public OP_Asine() {
		this.name = "MS";
		this.info = "trigonometric inverse sine";
		this.argTypes = "N";
	}
	@Override
	public void execute(Block block) {
		Object n = block.pop();
		if(isNumeric(n)) {
			block.push(toNumeric(n).asin());
			return;
		}
		throw new TypeError(this.name, this.argTypes, n);
	}
}

// T - 84
class OP_Atangent extends Operation {
	public OP_Atangent() {
		this.name = "MT";
		this.info = "trigonometric inverse tangent";
		this.argTypes = "N";
	}
	@Override
	public void execute(Block block) {
		Object n = block.pop();
		if(isNumeric(n)) {
			block.push(toNumeric(n).atan());
			return;
		}
		throw new TypeError(this.name, this.argTypes, n);
	}
}


//V - 86
class OP_Dialog extends Operation {
	public OP_Dialog() {
		this.name = "MV";
		this.info = "options title windowhdr msgtype dialogtype MV\n"
				+ "  dialogtype:\n"
				+ "    1: request string\n"
				+ "    2: request number\n"
				+ "    3: alert\n"
				+ "    4: yes or no\n"
				+ "    5: option buttons\n"
				+ "    6: option dropdown\n"
				+ "    7: choose file\n"
				+ "  msgtype:\n"
				+ "    1: plain\n"
				+ "    2: question\n"
				+ "    3: warning\n"
				+ "    4: error";
		this.argTypes = "LSSII";
	}
	@Override
	public void execute(Block block) {
		final Object _dialogType = block.pop();
		final Object _msgType = block.pop();
		final Object _windowHdr = block.pop();
		final Object _title = block.pop();
		final Object _options = block.pop();
		
		//Check types
		if(!(	isNumeric(_dialogType)
				&& isNumeric(_msgType)
				&& isString(_windowHdr)
				&& isString(_title)
				&& isList(_options)
				)) {
			throw new TypeError(this, _dialogType, _msgType, _windowHdr, _title, _options);
		}
		
		//Cast values
		final int dialogType = toNumeric(_dialogType).toInt();
		final int msgType = toNumeric(_msgType).toInt();
		final String windowHdr = getString(_windowHdr);
		final String title = getString(_title);
		final ArrayList<Object> options = toList(_options);
		
		//Error checking
		if (dialogType < QuickDialog.MIN_OPT || dialogType > QuickDialog.MAX_OPT) {
			throw new ElementRuntimeException("MV: invalid dialog type: " + dialogType);
		}
		if (msgType < 1 || msgType > 4) {
			throw new ElementRuntimeException("MV: invalid message type: " + msgType);
		}
		if ((dialogType == QuickDialog.OPTION_BUTTONS || dialogType == QuickDialog.OPTION_DROPDOWN)
				&& options.size() <= 0) {
			throw new ElementRuntimeException("MV: options list must not be empty");
		}
		if (dialogType == QuickDialog.YES_OR_NO && options.size() != 2) {
			throw new ElementRuntimeException("MV: yes or no dialog options list length must be 2");
		}

		//Convert arraylist to string array
		String[] optionsArr = new String[options.size()];
		for (int i = 0; i < options.size(); i++) {
			optionsArr[i] = castString(options.get(i));
		}
		
		//show dialog
		final Object out = QuickDialog.showDialog(dialogType, title, optionsArr, windowHdr, msgType);
		if (out != null) {
			block.push(out);
		}
	}
}



//X - 88
class OP_AdvPlot extends Operation {
	public OP_AdvPlot() {
		this.name = "MX";
		this.info = "plot\n"
				+ "  parameters:\n"
				+ "    type 0=line, 0=scatter\n"
				+ "    title S\n"
				+ "    xlabel S\n"
				+ "    ylabel S\n"
				+ "    height D\n"
				+ "    width D\n"
				+ "    xaxis [minD maxD]\n"
				+ "    yaxis [minD maxD]\n"
				+ "    x L<N>\n"
				+ "    y [[nameS strokeD color[r g b] dataL], ..]"
				+ "    show B\n"
				+ "    legend B\n"
				+ "    horizontal B\n"
				+ "    filename S\n";
		this.argTypes = "L";
	}
	@Override
	public void execute(Block block) {
		Object a = block.pop();
		if (a instanceof ArrayList) {
			try {
				ChartParams cp = ChartParams.parseParams(toList(a));
				FreeChartInterface.drawChart(cp);
			} catch (ClassCastException e) {
				throw new ElementRuntimeException("MX: Invalid parameter type");
			}
		}
	}
}

//Z - 9
class OP_SysConfig extends Operation {
	public OP_SysConfig() {
		this.name = "MZ";
		this.info = "system functions\n"
				+ "  S1: change prompt text\n"
				+ "  A2: get working dir\n"
				+ "  S3: set working dir\n"
				+ "  \"\"3: reset working dir\n"
				+ "  S4: list files in working dir + S\n"
				+ "  S5: create dir in working dir + S\n"
				+ "  S6: delete file or dir";
		this.argTypes = "AI";
	}
	@Override
	public void execute(Block block) {
		Object cmd = block.pop();
		Object arg = block.pop();
		
		if(isNumeric(cmd)) {
			doCommand(toNumeric(cmd).toInt(), arg, block);
		} else {	
			throw new TypeError(this.name, this.argTypes, cmd, arg);
		}
	}
	
	private void doCommand(int cmdID, Object arg, Block b) {
		switch(cmdID) {
		
		//Change the prompt
		case 1:
			if(isString(arg)) {
				ElemPrefs.setPrompt(getString(arg));
			} else {
				throw new ElementRuntimeException("arg 1 MZ: arg must be a string. Recieved:\n" + show(arg));
			}
			break;
		
		//Return working directory
		case 2:
			b.push(ElemPrefs.getWorkingDir());
			break;
			
		//Set working directory
		case 3:
			if (isString(arg)) {
				String dir = getString(arg);
				if(dir.equals("")) {
					ElemPrefs.resetWorkingDir();
				} else {
					if (!ElemPrefs.setWorkingDir(getString(arg))) {
						throw new ElementRuntimeException("arg 3 MZ: arg is not a valid path."
								+ " Did you include a '/' or '\' at the end? Recieved:\n" + show(arg));
					}
				}
			}else {
				throw new ElementRuntimeException("arg 3 MZ: arg must be a string. Recieved:\n" + show(arg));
			}
			break;
		
		//List files in working directory
		case 4:
			if (isString(arg)) {
				String fstr = ElemPrefs.getWorkingDir() + getString(arg);
				try {
					b.push(ElemPrefs.listFilesAndDirsForFolder(new File(fstr)));
				} catch (NullPointerException e) {
					throw new ElementRuntimeException("arg 4 MZ: arg is not a valid location. Recieved:\n" + fstr);
				}
			} else {
				throw new ElementRuntimeException("arg 4 MZ: arg must be a string. Recieved:\n" + show(arg));
			}
			break;
			
		//Create dir
		case 5:
			if(isString(arg)) {
				String fstr = ElemPrefs.getWorkingDir() + getString(arg);
				if(!ElemPrefs.mkDir(fstr)) {
					throw new ElementRuntimeException("arg 5 MZ: arg must be a valid name. Recieved:\n" + fstr);
				}
			} else {
				throw new ElementRuntimeException("arg 5 MZ: arg must be a string. Recieved:\n" + show(arg));
			}

		break;
		
		//Delete
		case 6:
			if(isString(arg)) {
				String arg_str = getString(arg);
				if(arg_str.equals("")) {
					throw new ElementRuntimeException("arg 5 MZ: arg must be a valid name. Recieved:\n" + arg_str);
				}
				String fstr = ElemPrefs.getWorkingDir() + getString(arg);
				if(!ElemPrefs.deleteFile(fstr)) {
					throw new ElementRuntimeException("arg 5 MZ: arg must be a valid name. Recieved:\n" + fstr);
				}
			} else {
				throw new ElementRuntimeException("arg 5 MZ: arg must be a string. Recieved:\n" + show(arg));
			}

		break;
		
		default:
			throw new ElementRuntimeException("arg " + cmdID + " MZ: is not a valid command ID");

		}
	}
}

//// { - 91
//class OP_Ceiling extends Operation {
//	public OP_Ceiling() {
//		this.name = "M{";
//		this.info = "numerical ceiling function";
//		this.argTypes = "N";
//	}
//	@Override
//	public void execute(Block block) {
//		Object n = block.pop();
//		
//		if (isNumeric(n)) {
//			block.push(toNumeric(n).ceil());
//		} else {
//			throw new TypeError(this.name, this.argTypes, n);
//		}
//	}
//}

//// } - 93
//class OP_Floor extends Operation {
//	public OP_Floor() {
//		this.name = "M}";
//		this.info = "numerical floor function";
//		this.argTypes = "N";
//	}
//	@Override
//	public void execute(Block block) {
//		Object n = block.pop();
//		if(isNumeric(n)) {
//			block.push(toNumeric(n).floor());
//		} else {
//			throw new TypeError(this.name, this.argTypes, n);
//		}
//	}
//}

// c - 99
class OP_Cosine extends Operation {
	public OP_Cosine() {
		this.name = "Mc";
		this.info = "trigonometric cosine";
		this.argTypes = "N";
	}
	@Override
	public void execute(Block block) {
		Object n = block.pop();
		if(isNumeric(n)) {
			block.push(toNumeric(n).cos());
			return;
		}
		throw new TypeError(this.name, this.argTypes, n);
	}
}

//d - 100
class OP_CastDouble extends Operation {
	public OP_CastDouble() {
		this.name = "Md";
		this.info = "cast number to double. if input not number, return 0.0";
		this.argTypes = "SN";
	}
	@Override
	public void execute(Block block) {
		final Object a = block.pop();
		
		if(isString(a)) {
			try {
				block.push(new Num(Double.parseDouble(castString(a))));
			} catch (NumberFormatException e) {
				throw new ElementRuntimeException("Cannot cast string \""+ castString(a) + "\" to a double.");
			}
		} else if (isBigNum(a)){
			block.push(new Num(toBigNum(a).toDouble()));
		} else if (isNum(a)) {
			block.push(a); //Already a double
		} else {
			throw new TypeError(this, a);
		}
	}
}

//h -104
class OP_MShow_Date extends Operation {

	public OP_MShow_Date() {
		this.name = "Mh";
		this.info = "convert the time in ms to a date string according to a given format";
		this.argTypes = "NS";
	}
	@Override
	public void execute(Block block) {
		Object a = block.pop();
		Object b = block.pop();
		if (isString(a) && isNumeric(b)) {
			String df_str = castString(a);
			long time = toNumeric(b).toLong();
			
			DateFormat df;
			try {
				df = new SimpleDateFormat(df_str, Locale.ENGLISH);
			} catch (IllegalArgumentException e) {
				throw new ElementRuntimeException("Invalid date format: '" + df_str + "'");
			}
			
			Date date = new Date(time);
			String out;
			try {
				out = df.format(date);
			} catch (Exception e) {
				throw new ElementRuntimeException("Cannot parse time: '" + time + "' as date '" + df_str + "'");
			}
			block.push(out);
		} else {
			throw new TypeError(this.name, this.argTypes, a, b);
		}
		
	}
}

////i - 105
//class OP_CastInt extends Operation {
//	public OP_CastInt() {
//		this.name = "Mi";
//		this.info = "cast number to int. if input not number, return 0";
//		this.argTypes = "N";
//	}
//	@Override
//	public void execute(Block block) {
//		final Object a = block.pop();
//		if(isString(a)) {
//			try {
//				block.push(Integer.parseInt(castString(a)));
//			} catch (NumberFormatException e) {
//				throw new ElementRuntimeException("Cannot cast string \""+ castString(a) + "\" to int.");
//			}
//		} else {
//			block.push(castInt(a));
//		}
//	}
//}  

//k - 107
class OP_AddParserChar extends Operation {
	public OP_AddParserChar() {
		this.name = "Mk";
		this.info = "add a special character";
		this.argTypes = "CS";
	}
	@Override
	public void execute(Block block) {
		final Object obj_name = block.pop();
		final Object obj_char = block.pop();
		
		if (isString(obj_name) && isChar(obj_char)) {
			String str = getString(obj_name);
			if (str.length() > 0 && CharacterParser.lalpha(str)) {
				CharacterParser.add_char(str, toChar(obj_char));
			} else {
				throw new ElementRuntimeException("Cannot create special character using " + str);
			}
		} else {
			throw new TypeError(this, obj_char, obj_name);
		}
	}
}


//l - 108
class OP_Ln extends Operation {
	public OP_Ln() {
		this.name = "Ml";
		this.info = "natural logarithm";
		this.argTypes = "N";
	}
	@Override
	public void execute(Block block) {
		Object a = block.pop();
		if(isNumeric(a)) {
			block.push(toNumeric(a).ln());
		} else {
			throw new TypeError(this, a);
		}
	}
}

// q - 113
class OP_SquareRoot extends Operation {
	public OP_SquareRoot() {
		this.name = "Mq";
		this.info = "square root function";
		this.argTypes = "N";
	}
	@Override
	public void execute(Block block) {
		Object n = block.pop();
		if(isNumeric(n)) {
			block.push(toNumeric(n).sqrt());
		} else {
			throw new TypeError(this, n);
		}
	}
}

// s - 115
class OP_Sine extends Operation {
	public OP_Sine() {
		this.name = "Ms";
		this.info = "trigonometric sine";
		this.argTypes = "N";
	}
	@Override
	public void execute(Block block) {
		Object n = block.pop();
		
		if(isNumeric(n)) {
			block.push(toNumeric(n).sin());
		} else {
			throw new TypeError(this.name, this.argTypes, n);
		}
	}
}




// t - 116
class OP_Tangent extends Operation {
	public OP_Tangent() {
		this.name = "Mt";
		this.info = "trigonometric tangent";
		this.argTypes = "N";
	}
	@Override
	public void execute(Block block) {
		Object n = block.pop();
		
		if(isNumeric(n)) {
			block.push(toNumeric(n).tan());
		} else {
			throw new TypeError(this.name, this.argTypes, n);
		}
	}
}

//w - 119
class OP_TypeStr extends Operation {
	public OP_TypeStr() {
		this.name = "Mw";
		this.info = "return string representation of the type\n  modules begin with a ':' and user types begin with a '.'";
		this.argTypes = "A";
	}
	@Override
	public void execute(Block block) {
		block.push(ElemTypes.getTypeName(block.pop()));
	}
}



//l - 108
class OP_Constants extends Operation {
	public OP_Constants() {
		this.name = "M|";
		this.info = "constants:\n"
				+ "  0: pi\n"
				+ "  1: e\n"
				+ "  2: double max\n"
				+ "  3: double min\n"
				+ "  4: NaN\n"
				+ "  5: +inf\n"
				+ "  6: -inf\n"
				+ "  7: int max\n"
				+ "  8: int min\n"
				+ "  9: system file separator\n"
				+ "  10: system path separator\n"
				+ "  11: char max code point\n"
				+ "  12: system line separator";
		this.argTypes = "N";
	}
	@Override
	public void execute(Block block) {
		Object a = block.pop();
		if(isNumeric(a)) {
			final int i = toNumeric(a).toInt();
			switch (i) {
			case 0: block.push(Num.PI); break;
			case 1: block.push(Num.E); break;
			case 2: block.push(Num.DOUBLE_MAX); break;
			case 3: block.push(Num.DOUBLE_MIN); break;
			case 4: block.push(Num.DOUBLE_NAN); break; 
			case 5: block.push(Num.DOUBLE_INF); break;
			case 6: block.push(Num.DOUBLE_NINF); break;
			case 7: block.push(Num.INT_MAX); break;
			case 8: block.push(Num.INT_MIN); break;
			case 9: block.push(File.separator); break;
			case 10: block.push(File.pathSeparator); break;
			case 11: block.push(Character.MAX_CODE_POINT); break;
			case 12: block.push(System.lineSeparator()); break;
			default:
				throw new ElementRuntimeException("M|: (" + i + ") is not a valid constant id.");
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}



