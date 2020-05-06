/*
 * Copyright (c) 2008-2013, Matthias Mann
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Matthias Mann nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package co.paralleluniverse.fibers.instrument;

import co.paralleluniverse.fibers.Instrumented;
import co.paralleluniverse.fibers.SuspendExecution;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Locale;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test to check NPE in MethodDatabase.ClassEntry.equals.
 * See https://github.com/puniverse/quasar/issues/286
 * Duplicate DB entries are actually caused by a threading issue and a race in the instrumentation.
 * This leads to duplicate class name in the database but with different ClassEntry objects.
 * When testing ClassEntry objects for equality the equals operator is blowing up as the superName can be null.
 * @author Paul Hatcher
 */
@Instrumented
public class MethodDatabaseDuplicate {

    @Test
    public void testSuspend() throws IOException {
        final String className = MethodDatabaseDuplicate.class.getName().replace('.', '/');
        final QuasarInstrumentor instrumentor = new QuasarInstrumentor(false);
        final MethodDatabase db = instrumentor.getMethodDatabase(MethodDatabaseDuplicate.class.getClassLoader());

        // Create a DB entry for this class with null super.
        db.getOrCreateClassEntry(className, null);

        // Parse this class, just to exercise parsing and DB.
        // Not strictly necessary for exception to occur. But good for coverage.
        try (final InputStream in = MethodDatabaseDuplicate.class.getResourceAsStream("MethodDatabaseDuplicate.class")) {
            instrumentor.instrumentClass(MethodDatabaseDuplicate.class.getClassLoader(), MethodDatabaseDuplicate.class.getName(), in, true);
        }

        // Now make it blow up by calling recordSuspendableMethods with a null super.
        // This will find the duplicate but with a different (new) class entry.
        db.recordSuspendableMethods(className, new MethodDatabase.ClassEntry(null));
    }
}
