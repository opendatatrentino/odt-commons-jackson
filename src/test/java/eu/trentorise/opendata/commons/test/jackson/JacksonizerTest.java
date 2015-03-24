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

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.trentorise.opendata.commons.LocalizedString;
import eu.trentorise.opendata.commons.OdtConfig;
import eu.trentorise.opendata.commons.jackson.Jacksonizer;
import eu.trentorise.opendata.commons.jackson.OdtCommonsModule;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author David Leoni
 */
public class JacksonizerTest {

    private static final Logger LOG = Logger.getLogger(JacksonizerTest.class.getName());

    @BeforeClass
    public static void beforeClass() {
        OdtConfig.of(OdtCommonsModuleTest.class).loadLogConfig();
    }

    @Test
    public void testToFromJsonDefaultMapper() throws IOException {
        LocalizedString ls = LocalizedString.of(Locale.ITALIAN, "ciao");
        assertEquals(ls, Jacksonizer.of().fromJson(Jacksonizer.of().toJson(ls), LocalizedString.class));
    }

    /**
     * Class with no constructor, Jackson won't be able to deserialize it
     */
    private static class MyNastyClass {

        public int getProp() {
            throw new RuntimeException();
        }
    }

    @Test
    public void testToFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        OdtCommonsModule.registerModulesInto(objectMapper);
        LocalizedString ls = LocalizedString.of(Locale.ITALIAN, "ciao");
        assertEquals(ls, Jacksonizer.of().fromJson(Jacksonizer.of(objectMapper).toJson(ls), LocalizedString.class));

        try {
            Jacksonizer.of().toJson(new MyNastyClass());
            Assert.fail();
        }
        catch (IllegalArgumentException ex) {

        }

        try {
            Jacksonizer.of().fromJson("garbage", MyNastyClass.class);
            Assert.fail();
        }
        catch (IllegalArgumentException ex) {

        }

    }

    @Test
    public void testObjectMapperCopy() {
        ObjectMapper om1 = Jacksonizer.of().createJacksonMapper();
        ObjectMapper om2 = Jacksonizer.of().createJacksonMapper();
        assertTrue(om1 != om2);
    }
}
