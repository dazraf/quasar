package co.paralleluniverse.fibers.lambdas;

import co.paralleluniverse.fibers.Suspendable;

import java.util.function.Supplier;

public class LambdaTest1 {
    Supplier<Integer> supplierInt;

    LambdaTest1(Supplier<Integer> s) {
        supplierInt = s;
    }

    @Suspendable
    Integer addSusp(Integer n) {
        return supplierInt.get() + n;
    }

    Integer addNonSusp(Integer n) {
        return supplierInt.get() + n;
    }
}
