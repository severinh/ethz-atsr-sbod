import soot.Local;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.sets.P2SetVisitor;

class ArraySizeIntervalCollector extends P2SetVisitor {
	
	private final BufferOverflowDetector detector;
	private final Local localPtr;
	private final IntervalPerVar context;
	private final String methodName;

	ArraySizeIntervalCollector(BufferOverflowDetector detector, Local localPtr,
			IntervalPerVar context, String methodName) {
		this.detector = detector;
		this.localPtr = localPtr;
		this.context = context;
		this.methodName = methodName;
	}

	@Override
	public void visit(Node n) {
		if(n instanceof AllocNode){
			AllocNode allocNode = (AllocNode) n;
			Object newExprRaw = allocNode.getNewExpr();
			if (newExprRaw instanceof NewArrayExpr) {
				NewArrayExpr alloc = (NewArrayExpr) newExprRaw;
				Value lenVal = alloc.getSize();
				// Retrieve analysis of method containing the allocation node.
				Analysis allocEnv = detector.getMethodAnalysis(allocNode
						.getMethod());
				if (lenVal instanceof IntConstant) {
					context.putIntervalForVar(localPtr.getName(),
							Interval.of(((IntConstant) lenVal).value));
				} else if (allocEnv != null) {
					// The allocation is in a method that we have analysed.
					// We should be able to give an interval for the array size.
					Interval size = allocEnv.getAllocationNodeMap().get(alloc);
					if (size != null) {
						context.putIntervalForVar(localPtr.getName(), size);
						BufferOverflowDetector.LOG.debug("Pointer analysis indicates that the array referred to by " 
								+ localPtr.getName() + " in method " + methodName 
								+ ", allocated as " + alloc + " in method " 
								+ allocNode.getMethod().getName() 
								+ " has size " + size + ".");
					} else {
						context.putIntervalForVar(localPtr.getName(), Interval.NON_NEGATIVE);
						BufferOverflowDetector.LOG.debug("No size on record for allocation site of the array referred to by " 
								+ localPtr.getName() + " in method " + methodName 
								+ ", allocated as " + alloc + " in method " 
								+ allocNode.getMethod().getName() + ".");
					}
				} else {
					// The allocation is in a method that we have not analysed. Will use TOP for array size.
					context.putIntervalForVar(localPtr.getName(), Interval.NON_NEGATIVE);
					BufferOverflowDetector.LOG.warn("Missing analysis for method " + allocNode.getMethod() + 
							" containing array allocation site " + alloc + 
							". Lookup triggered by reference " + localPtr + " in method " + methodName + ".");
				}
			}
		} else {
			BufferOverflowDetector.LOG.warn("Non-allocation node in points-to analysis results for pointer " 
					+ localPtr + " in method " 
					+ methodName + ".");
			context.putIntervalForVar(localPtr.getName(), Interval.NON_NEGATIVE);
		}
	}
	
}