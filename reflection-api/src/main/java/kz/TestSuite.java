package kz;

import kz.annotation.AfterSuite;
import kz.annotation.BeforeSuite;
import kz.annotation.Disabled;
import kz.annotation.Test;

//@Disabled(cause = "Haha!")
public class TestSuite {
    @BeforeSuite
    public static void init() {
        System.out.println("init");
    }

    @Test
    @Disabled(cause = "just for test")
    public static void test1() {
        System.out.println(1);
    }

    @Test(priority = 3)
    public static void test2() {
        System.out.println(3);
    }

    @Test(priority = 4)
    public static void test3() throws Exception {
        throw new Exception("This method throws exception!");
    }

    @Test(priority = 4)
    public static void test4() {
        System.out.println("4.1");
    }

//    @Test(priority = 11) //this must not work!
//    public static void test5() {
//        System.out.println("11");
//    }

    @AfterSuite
    public static void afterSuite() {
        System.out.println("after suite");
    }
}
