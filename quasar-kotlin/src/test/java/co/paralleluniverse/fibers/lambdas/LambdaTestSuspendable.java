package co.paralleluniverse.fibers.lambdas;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.kotlin.fibers.StaticPropertiesTest;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * Test for java Suspendable using the JavaAgent.
 */
public class LambdaTestSuspendable {

    @Suspendable
    private Integer supplier(Supplier<Integer> s) {
        // When the call is made quasar does not know that implementation of get is @Suspendable since interface is not annotated.
        // However, we can add this method to a file suspendables-super so that quasar then considers the interface @Suspendable and
        // changes the call site appropriately, this then works.
        return s.get() + 1;
    }

    @Suspendable
    private void yield() {
        try {
            Fiber.yield();
        } catch (final SuspendExecution e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public void lambdaSupplier1() {
        int n = 10;
        assertThat(
        StaticPropertiesTest.fiberWithVerifyInstrumentationOn(
                () -> supplier(
                        () -> {
                            yield();
                            return n;
                        }
                )
        ), is(n+1));
    }

    @Test
    public void lambdaSupplier2() {
        int n = 10;
        // This works, despite the fact the lambda is not marked Suspendable.
        // So this implies that the lambda is instrumented because of suspendable-supers.
        LambdaTest1 test1 = new LambdaTest1(() -> {yield(); return n;});
        assertThat(
                StaticPropertiesTest.fiberWithVerifyInstrumentationOn(
                        () -> test1.addSusp(1)
                ), is(n+1));
    }

    @Test
    public void lambdaSupplier3() {
        int n = 10;
        // So now create a normal lambda that just uses the SupplierInterface.
        // If it is being instrumented then does this break outside of a Fiber context?
        LambdaTest1 test1 = new LambdaTest1(() -> {return n;});
        assertThat(
                test1.addNonSusp(1),
                is(n+1)
        );
    }
}
