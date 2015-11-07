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

import com.google.common.collect.ImmutableList;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.PathCompiler;
import com.jayway.jsonpath.internal.token.PathToken;
import eu.trentorise.opendata.commons.TodConfig;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author David Leoni
 * @since 1.1.0
 */
public class JsonPathTest {
    private static final Logger LOG = Logger.getLogger(JsonPathTest.class.getName());
        
        @BeforeClass
    public static void beforeClass() {
        TodConfig.init(JsonPathTest.class);
    }
    
    @Test
    public void testGrammar(){
        assertEquals("c", JsonPath.read("{a:{b:\"c\"}}", "$.a.b"));
        assertEquals(ImmutableList.of("c"), JsonPath.read("{a:{b:\"c\"}}", "*.b"));
        
        Path path = PathCompiler.compile("$.a.b");
        
        Field f; 
        try {
            f = path.getClass().getDeclaredField("root"); //NoSuchFieldException
            f.setAccessible(true);
            PathToken root = (PathToken) f.get(path); //IllegalAccessException
            //root.
            LOG.fine(root.toString());
        }
        catch (NoSuchFieldException |SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(JsonPathTest.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        
        
        LOG.fine(path.toString());
    }
}
