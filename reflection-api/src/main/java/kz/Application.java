package kz;

public class Application {
    public static void main(String[] args) {
        try {
            new TestRunner().run(TestSuite.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}