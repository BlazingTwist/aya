package aya.instruction.flag;

import aya.Aya;
import aya.eval.BlockEvaluator;

/**
 *  Pop a variable set from the variable scope stack
 */
public class PopCallstackInstruction extends FlagInstruction {
	
	public static final PopCallstackInstruction INSTANCE = new PopCallstackInstruction();
	
	private PopCallstackInstruction() { }
	
	@Override
	public void execute(BlockEvaluator b) {
		Aya.getInstance().deleteme_getRoot().getCallStack().pop();
	}
	
	@Override
	public String toString() {
		//return "`POPVAR`";
		return "";
	}
}
