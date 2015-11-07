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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Optional;
import eu.trentorise.opendata.commons.TodConfig;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Generic tests to understand Jackson inner mysteries
 *
 * @author David Leoni
 */
public class JacksonTest {

    private static final Logger LOG = Logger.getLogger(JacksonTest.class.getName());

    @BeforeClass
    public static void beforeClass() {
        TodConfig.init(JacksonTest.class);
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

    private static class RootLocale {

        public Locale locale = Locale.ROOT;
    }

    
     /**
     * Shows {@link Locale.ROOT} is correctly serializaed to empty string
     * @see #testLocaleDeser() 
     * @since 1.1.0 
     */
    @Test
    public void testLocaleSer() throws JsonProcessingException, IOException {
        ObjectMapper om = new ObjectMapper();
        RootLocale rootLocale = new RootLocale();        
        String json = om.writeValueAsString(rootLocale);
        assertTrue(!json.contains("null"));
    }
    
    /**
     * Shows that nasty Jackson deserializes "" into null instead of
     * {@link Locale.ROOT} !!!
     *
     */
    @Test(expected = AssertionError.class)
    public void testLocaleDeser() throws JsonProcessingException, IOException {
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(new RootLocale());               
        LOG.log(Level.FINE, "json = {0}", json);
        RootLocale res = om.readValue(json, RootLocale.class);
        assertNotNull(res.locale);
        assertEquals(Locale.ROOT, res.locale);
    }

    private static class WithText<T> {

        private String description;
        private T value;

        public WithText() {
        }

        public String getDescription() {
            return description;
        }

        public T getValue() {
            return value;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setValue(T value) {
            this.value = value;
        }

    }

    @Test
    public void testGenerics() throws JsonProcessingException, IOException {
        WithText<Date> d = new WithText();
        d.setValue(new Date());
        d.setDescription("bla");

        String json = new ObjectMapper().writeValueAsString(d);
        LOG.fine(json);
        WithText<Date> readValue = new ObjectMapper().readValue(json, new TypeReference<WithText<Date>>() {
        });

    }

    /**
     * Shows default convertValue is not very smart, it causes causes null null     {@code java.lang.IllegalArgumentException: Can not deserialize instance of
     * java.util.ArrayList out of VALUE_STRING token}
     */
    @Test
    public void testJacksonConverter() throws IOException {
        try {
            Object convertValue = new ObjectMapper().convertValue("[\"it\"]", new TypeReference<List<Locale>>() {
            });
            Assert.fail();
        }
        catch (Exception ex) {

        }
    }

    /**
     * Shows we absolutely need the damned dot '.' before the type field when
     * using JsonTypeInfo.Id.MINIMAL_CLASS
     *
     * @throws JsonProcessingException
     * @throws IOException
     */
    @Test
    public void testMinimalAbs() throws JsonProcessingException, IOException {
        String json = new ObjectMapper().writeValueAsString(new Impl());
        LOG.log(Level.INFO, "json  = {0}", json);

        Abs retJson = new ObjectMapper().readValue(json, Abs.class);

    }

}

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
abstract class Abs {

    @JsonProperty()
    public String getType() {
        return "." + this.getClass().getSimpleName();
    }

    private void setType(String s) {

    }
}

class Impl extends Abs {

    public Impl() {
    }

}
