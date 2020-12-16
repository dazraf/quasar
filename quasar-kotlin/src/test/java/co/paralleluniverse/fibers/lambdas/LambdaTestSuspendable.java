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

    @Ignore
    @Test
    public void lambdaSupplier() {
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
}
