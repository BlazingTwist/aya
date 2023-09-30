package aya.instruction.index;

import aya.ReprStream;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.parser.SourceStringRef;

public class GetExprIndexInstruction extends GetIndexInstruction {
	
	private StaticBlock _index;

	public GetExprIndexInstruction(SourceStringRef source, StaticBlock index) {
		super(source);
		_index = index;
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(".[");
		BlockUtils.repr(stream, _index, false, null);
		stream.print("]");
		return stream;
	}
	
	protected Obj getIndex() {
		Block evaluator = new Block(_index);
		evaluator.eval();
		if (evaluator.getStack().size() == 1) {
			return evaluator.getStack().pop();
		} else if (evaluator.getStack().size() > 1) {
			throw new ValueError("Error attempting to index object with " + _index.repr() 
										  + ". Expression returned more than one item");
		} else {
			throw new ValueError("Error attempting to index object with " + _index.repr() 
										  + ". Expression returned nothing");
		}
	}
}
