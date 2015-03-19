/*
 * Copyright 2015 Trento Rise.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.trentorise.opendata.commons.test.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Optional;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Generic tests to understand Jackson inner mysteries
 *
 * @author David Leoni
 */
public class JacksonTest {

    private static final Logger logger = Logger.getLogger(JacksonTest.class.getName());

    /**
     * Tests that the provided object can be converted to json and
     * reconstructed. Also prints the json with the provided logger at FINE
     * level.
     *
     * @return the reconstructed object
     */
    public static <T> T testJsonConv(ObjectMapper om, T obj, Logger logger) {

        T recObj;

        try {
            String json = om.writeValueAsString(obj);
            logger.log(Level.FINE, "json = {0}", json);
            Object ret = om.readValue(json, obj.getClass());
            recObj = (T) ret;
        }
        catch (Throwable ex) {
            throw new RuntimeException(ex);
        }

        assertEquals(obj, recObj);
        return recObj;
    }

    static class A {

        private Optional<String> opt;

        public A() {
        }

        public Optional<String> getOpt() {
            return opt;
        }

        public void setOpt(Optional<String> opt) {
            this.opt = opt;
        }

    }

    /**
     * Tests Guava Optional behaviour
     */
    @Test
    public void testOptional() throws JsonProcessingException, IOException {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new GuavaModule());
        A a = new A();
        a.setOpt(Optional.<String>absent());
        String s = om.writeValueAsString(a);
        assertTrue(s.contains("null"));

        A ra = om.readValue("{\"opt\":null}", A.class);
        assertEquals(Optional.absent(), ra.getOpt());

    }

    /**
     * This fails! As a workaround, we must initialize obj to 'absent' in the
     * object constructor!
     *
     * @throws IOException
     */
    @Test(expected = AssertionError.class)
    public void testEmptyOptional() throws IOException {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new GuavaModule());

        A rb = om.readValue("{}", A.class);
        assertEquals(Optional.absent(), rb.getOpt());
    }

    private static class NullLocale {

        public Locale locale = Locale.ROOT;
    }

    /**
     * Shows that nasty Jackson deserializes "" into null instead of
     * {@link Locale.ROOT} !!!
     *     
     */
    @Test(expected = AssertionError.class)
    public void localeDeser() throws JsonProcessingException, IOException {
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(new NullLocale());
        logger.log(Level.FINE, "json = {0}", json);
        NullLocale res = om.readValue(json, NullLocale.class);
        assertNotNull(res.locale);
        assertEquals(Locale.ROOT, res.locale);
    }

}
