package co.paralleluniverse.fibers.lambdas;

import co.paralleluniverse.fibers.Suspendable;

import java.util.function.Supplier;

public class LambdaTest1 {
    Supplier<Integer> supplierInt;

    LambdaTest1(Supplier<Integer> s) {
        supplierInt = s;
    }

    @Suspendable
    Integer add(Integer n) {
        return supplierInt.get() + n;
    }
}
