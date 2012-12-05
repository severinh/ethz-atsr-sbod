public class AllTests {
	
	public static void main(String[] args) {
		ArithmeticTests.main(args);
		ConditionalTests.main(args);
		ConstantTests.main(args);
		// IntervalTests is a proper unit test class, it does not require soot
		LoopTests.main(args);
		PointerAnalysisTests.main(args);
		StaticFieldTests.main(args);
		TemplateTests.main(args);
	}

}