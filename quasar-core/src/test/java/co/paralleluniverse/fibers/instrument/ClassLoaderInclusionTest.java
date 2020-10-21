package co.paralleluniverse.fibers.instrument;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClassLoaderInclusionTest {
    private QuasarInstrumentor instrumentor;

    @Before
    public void setup() {
        instrumentor = new QuasarInstrumentor(false);
    }

    @Test
    public void testAcceptWhenNoExclusions() {
        assertFalse(instrumentor.isExcludedClassLoader("X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.c.X"));
    }

    @Test
    public void testAcceptingEverything() {
        instrumentor.addIncludedClassLoaders(new String[] {"**"});
        assertFalse(instrumentor.isExcludedClassLoader("X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.c.X"));
    }

    @Test
    public void testAcceptingDefaultPackageOnly() {
        instrumentor.addIncludedClassLoaders(new String[] {"*"});
        assertFalse(instrumentor.isExcludedClassLoader("X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.c.X"));
    }

    @Test
    public void testAcceptingFirstLevel() {
        instrumentor.addIncludedClassLoaders(new String[] {"*.*"});
        assertTrue(instrumentor.isExcludedClassLoader("X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.c.X"));
    }

    @Test
    public void testAcceptingFirstLevelAndBelow() {
        instrumentor.addIncludedClassLoaders(new String[] {"*.**"});
        assertTrue(instrumentor.isExcludedClassLoader("X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.c.X"));
    }

    @Test
    public void testAcceptingSecondLevel() {
        instrumentor.addIncludedClassLoaders(new String[] {"*.*.*"});
        assertTrue(instrumentor.isExcludedClassLoader("X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.c.X"));
    }

    @Test
    public void testRejectingSecondLevelAndBelow() {
        instrumentor.addIncludedClassLoaders(new String[] {"*.*.**"});
        assertTrue(instrumentor.isExcludedClassLoader("X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.c.X"));
    }

    @Test
    public void testRejectingThirdLevel() {
        instrumentor.addIncludedClassLoaders(new String[] {"*.*.*.*"});
        assertTrue(instrumentor.isExcludedClassLoader("X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.c.X"));
    }

    @Test
    public void testRejectingThirdLevelAndBelow() {
        instrumentor.addIncludedClassLoaders(new String[] {"*.*.*.**"});
        assertTrue(instrumentor.isExcludedClassLoader("X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.c.X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.c.d.X"));
    }

    @Test
    public void testRejectingSpecificLoader() {
        instrumentor.addIncludedClassLoaders(new String[] {"**.LOADER"});
        assertFalse(instrumentor.isExcludedClassLoader("a.LOADER"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.LOADER"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.c.LOADER"));

        assertTrue(instrumentor.isExcludedClassLoader("a.LOAD"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.LOAD"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.c.LOAD"));
    }

    @Test
    public void testRejectingBySingleCharcterGlobs() {
        instrumentor.addIncludedClassLoaders(new String[] {"**.?X"});
        assertFalse(instrumentor.isExcludedClassLoader("a.1X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.1X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.c.1X"));

        assertFalse(instrumentor.isExcludedClassLoader("a.2X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.2X"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.c.2X"));

        assertTrue(instrumentor.isExcludedClassLoader("a.X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.c.X"));
    }

    @Test
    public void testRejectingnamesWithDollars() {
        instrumentor.addIncludedClassLoaders(new String[] {"**.X$Y"});
        assertFalse(instrumentor.isExcludedClassLoader("a.X$Y"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.X$Y"));
        assertFalse(instrumentor.isExcludedClassLoader("a.b.c.X$Y"));

        assertTrue(instrumentor.isExcludedClassLoader("a.XY"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.XY"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.c.XY"));

        assertTrue(instrumentor.isExcludedClassLoader("a.X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.X"));
        assertTrue(instrumentor.isExcludedClassLoader("a.b.c.X"));
    }

}
