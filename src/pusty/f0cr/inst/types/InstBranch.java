package pusty.f0cr.inst.types;

import pusty.f0cr.inst.InstructionReader;
import pusty.f0cr.inst.Opcodes;

public class InstBranch extends Instruction {
	protected int type;
	protected int branchPos;
	protected int poolIndex;
	protected int localVariable;
	/*
	 * Branches to memory location getBranch()
	 * either if integers on stack fulify Xcc
	 * or if integer on stack fulifies Xcc
	 * or unconditional jump
	 */
	public InstBranch(InstructionReader reader, byte inst, byte[] data) {
		super(reader, inst, data);
		if(!isInst(inst) && (inst&0xFF) != Opcodes.WIDE) {System.err.println("Error: Created Branch with OpCode: "+inst+" => " +Opcodes.getName(inst));}
		type = setType();
		if(type != BRANCH_SUBROUTINE && type != BRANCH_INVOKE && type != BRANCH_RET && type != BRANCH_RETURN) {
			if((inst&0xFF) == Opcodes.GOTO_W || (inst&0xFF) == Opcodes.JSR_W) {
				branchPos = InstructionHandler.readInt(data, 0);
			}else {
				branchPos = InstructionHandler.readShort(data, 0);
			}
		}else if(type == BRANCH_INVOKE) {
			poolIndex = InstructionHandler.readShort(data, 0);
		}else if(type == BRANCH_RET) {
			if((inst&0xFF) == Opcodes.WIDE) localVariable = InstructionHandler.readShort(data, 1);
			else                            localVariable = InstructionHandler.readByte(data, 0);
		}
			
	}
	public String toString() {
		if(type != BRANCH_SUBROUTINE && type != BRANCH_INVOKE && type != BRANCH_RET && type != BRANCH_RETURN) {
			return "Branch: "+(reader.getPosition(this,getBranchPos()));
		}else if(type == BRANCH_INVOKE) {
			return "Pool: "+pool.get(poolIndex);
		}
			return super.toString();
	}
	public int getBranchPos() {
		return branchPos;
	}
	public int getBranchInstructionPos() {
		return (reader.getPosition(this,getBranchPos()));
	}
	public Instruction getBranchInstruction() {
		return reader.getInstruction(reader.getPosition(this,getBranchPos()));
	}
	public int getPoolIndex() {
		return poolIndex;
	}
	public int getTypeOfBranch() {
		return type;
	}
	public static boolean isInst(byte inst) {
		switch(inst&0xFF) {
			case Opcodes.IFEQ:
	        case Opcodes.IFNE:
	        case Opcodes.IFLT:
	        case Opcodes.IFGE:
	        case Opcodes.IFGT:
	        case Opcodes.IFLE:
	        case Opcodes.IF_ICMPEQ:
	        case Opcodes.IF_ICMPNE:
	        case Opcodes.IF_ICMPLT:
	        case Opcodes.IF_ICMPGE:
	        case Opcodes.IF_ICMPGT:
	        case Opcodes.IF_ICMPLE:
	        case Opcodes.IF_ACMPEQ:
	        case Opcodes.IF_ACMPNE:
	        case Opcodes.IFNULL:
	        case Opcodes.IFNONNULL: 
	        case Opcodes.GOTO:
	        case Opcodes.GOTO_W:
	        case Opcodes.JSR:
	        case Opcodes.JSR_W:
	        //RETURN BRANCHES
	        case Opcodes.RET:
	        case Opcodes.RETURN:
	        case Opcodes.ARETURN:
	        case Opcodes.IRETURN:
	        case Opcodes.LRETURN:
	        case Opcodes.FRETURN:
	        case Opcodes.DRETURN:
	        //INVOKES
	        case Opcodes.INVOKEVIRTUAL:
	        case Opcodes.INVOKESPECIAL:
	        case Opcodes.INVOKESTATIC:
	        case Opcodes.INVOKEINTERFACE:
	        case Opcodes.INVOKEDYNAMIC:
				return true;
			default: return false;
		}
	}
	public static final int BRANCH_ERROR = -1;
	public static final int BRANCH_RET = 0; //returns from subroutine
	public static final int BRANCH_RETURN = 1; //returns with value
	public static final int BRANCH_SUBROUTINE = 2; //branches to subroutine
	public static final int BRANCH_GOTO = 3; //just branches
	public static final int BRANCH_NULL = 4; //compares reference with null
	public static final int BRANCH_ICMPCC = 5; //compares 2 integers
	public static final int BRANCH_ACMPCC = 6; //compares 2 references
	public static final int BRANCH_CC = 7; //checks value
	public static final int BRANCH_INVOKE = 8; //invokes method
	
	public int setType() {
		switch(inst&0xFF) {
		case Opcodes.IFEQ:
        case Opcodes.IFNE:
        case Opcodes.IFLT:
        case Opcodes.IFGE:
        case Opcodes.IFGT:
        case Opcodes.IFLE:
        	return BRANCH_CC;
        case Opcodes.IF_ICMPEQ:
        case Opcodes.IF_ICMPNE:
        case Opcodes.IF_ICMPLT:
        case Opcodes.IF_ICMPGE:
        case Opcodes.IF_ICMPGT:
        case Opcodes.IF_ICMPLE:
        	return BRANCH_ICMPCC;
        case Opcodes.IF_ACMPEQ:
        case Opcodes.IF_ACMPNE:
        	return BRANCH_ACMPCC;
        case Opcodes.IFNULL:
        case Opcodes.IFNONNULL: 
        	return BRANCH_NULL;
        case Opcodes.GOTO:
        case Opcodes.GOTO_W:
        	return BRANCH_GOTO;
        case Opcodes.JSR:
        case Opcodes.JSR_W:
        	return BRANCH_SUBROUTINE;
        //RETURN BRANCHES
        case Opcodes.RET:
        	return BRANCH_RET;
        case Opcodes.RETURN:
        case Opcodes.ARETURN:
        case Opcodes.IRETURN:
        case Opcodes.LRETURN:
        case Opcodes.FRETURN:
        case Opcodes.DRETURN:
        	return BRANCH_RETURN;
        case Opcodes.INVOKEVIRTUAL:
        case Opcodes.INVOKESPECIAL:
        case Opcodes.INVOKESTATIC:
        case Opcodes.INVOKEINTERFACE:
        case Opcodes.INVOKEDYNAMIC:
        	return BRANCH_INVOKE;
        case Opcodes.WIDE:
			switch(InstructionHandler.readByte(data, 0)) {
			case Opcodes.RET: return BRANCH_RET;
			default: return BRANCH_ERROR;
			}
        	
		default: return BRANCH_ERROR;
		}
	}
}
