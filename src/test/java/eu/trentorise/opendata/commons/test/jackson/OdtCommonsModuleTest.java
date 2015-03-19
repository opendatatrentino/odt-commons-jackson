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
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.commons.LocalizedString;
import eu.trentorise.opendata.commons.OdtConfig;
import eu.trentorise.opendata.commons.jackson.Jacksonizer;
import eu.trentorise.opendata.commons.jackson.OdtCommonsModule;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author David Leoni
 */
public class OdtCommonsModuleTest {

    private static final Logger LOG = Logger.getLogger(OdtCommonsModuleTest.class.getName());

    private ObjectMapper objectMapper;

    @BeforeClass
    public static void beforeClass() {
        OdtConfig.of(OdtCommonsModuleTest.class).loadLogConfig();
    }

    @Before
    public void before() {
        objectMapper = new ObjectMapper();
        OdtCommonsModule.registerModulesInto(objectMapper);
    }

    @After
    public void after() {
        objectMapper = null;
    }

    @Test
    public void testDict() throws JsonProcessingException, IOException {

        JacksonTest.testJsonConv(objectMapper, Dict.of("a", "b"), LOG);
        JacksonTest.testJsonConv(objectMapper, Dict.of(Locale.FRENCH, "a", "b"), LOG);

        Dict dict = objectMapper.readValue("{}", Dict.class);
        assertEquals(Dict.of(), dict);

        try {
            Dict dict_2 = objectMapper.readValue("{\"it\":null}", Dict.class);
            Assert.fail("Should have validated the dict!");
        }
        catch (Exception ex) {

        }
    }

    @Test
    public void testLocalizedString() throws JsonProcessingException, IOException {

        JacksonTest.testJsonConv(objectMapper, LocalizedString.of(Locale.FRENCH, "a"), LOG);

        try {
            objectMapper.readValue("{\"string\":null, \"locale\":\"it\"}", LocalizedString.class);
            Assert.fail("Should not accept null values!");
        }
        catch (Exception ex) {

        }

        try {
            objectMapper.readValue("{\"string\":\"a\"}", LocalizedString.class);
            Assert.fail("Should have failed because no locale field was provided!");
        }
        catch (Exception ex) {

        }

    }

    /**
     * Seems it doesn't work with empty constructors
     */
    @Test
    @Ignore
    public void testEmptyConstructor() throws IOException {

        assertEquals(LocalizedString.of(), objectMapper.readValue("{}", LocalizedString.class));
    }

    @Test
    public void example1() throws JsonProcessingException, IOException {

        ObjectMapper om = new ObjectMapper();
        om.registerModule(new GuavaModule());
        om.registerModule(new OdtCommonsModule());

        String json = om.writeValueAsString(LocalizedString.of(Locale.ITALIAN, "ciao"));
        LocalizedString reconstructedLocalizedString = om.readValue(json, LocalizedString.class);
    }

    @Test
    public void example2() throws JsonProcessingException, IOException {

        ObjectMapper om = new ObjectMapper();
        OdtCommonsModule.registerModulesInto(om);

        String json = om.writeValueAsString(LocalizedString.of(Locale.ITALIAN, "ciao"));
        LocalizedString reconstructedLocalizedString = om.readValue(json, LocalizedString.class);
    }

}
