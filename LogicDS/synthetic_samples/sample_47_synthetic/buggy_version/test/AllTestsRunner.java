import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class AllTestsRunner {
    public static void main(String... args) throws ClassNotFoundException {
        if (args.length == 0) {
            System.out.println("Usage: java AllTestsRunner <TestClass>");
            System.exit(1);
        }

        Class<?> testClass = Class.forName(args[0]);

        Result result = JUnitCore.runClasses(testClass);

        int totalTests = result.getRunCount();
        int failedTests = result.getFailureCount();
        int passedTests = totalTests - failedTests;

        System.out.println("Failing tests: " + failedTests);
        System.out.println("Passing tests: " + passedTests);

        if (!result.wasSuccessful()) {
            System.out.println("\n========== FAILURE DETAILS ==========");
            for (Failure failure : result.getFailures()) {
                System.out.println("Test: " + failure.getTestHeader());
                System.out.println("Error: " + failure.getMessage());

                String[] stackTraceLines = failure.getTrace().split("\n");
                System.out.println("Stack Trace:");
                for (int i = 0; i < Math.min(3, stackTraceLines.length); i++) {
                    System.out.println(stackTraceLines[i]);
                }

                System.out.println("-------------------------------------");
            }
        }
    }
}
