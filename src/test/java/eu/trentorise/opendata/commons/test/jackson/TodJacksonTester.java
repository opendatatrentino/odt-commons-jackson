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
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import exceptions.TodException;

import static com.google.common.base.Preconditions.checkNotNull;
import static eu.trentorise.opendata.commons.validation.Preconditions.checkNotEmpty;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import static org.junit.Assert.assertEquals;

/**
 * Utility class to test Jackson serialization/deserializtion
 * @author David Leoni
 */
public class TodJacksonTester {

    /**
     * Converts {@code obj} to an {@link ObjectNode}, sets field
     * {@code fieldName} to {@code newNode} and returns the json string
     * representation of such new object. Also logs the json with the provided
     * logger at FINE level.
     */
    public static String changeField(ObjectMapper objectMapper, Logger logger, Object obj, String fieldName, JsonNode newNode) {
        checkNotNull(obj);
        checkNotEmpty(fieldName, "Invalid field name!");

        String string;
        try {
            string = objectMapper.writeValueAsString(obj);
        }
        catch (JsonProcessingException ex) {
            throw new RuntimeException("Error while jacksonizing object to json node!", ex);
        }
        TreeNode treeNode;
        try {
            treeNode = (ObjectNode) objectMapper.readTree(string);
        }
        catch (IOException ex) {
            throw new RuntimeException("Error while creating json tree from serialized object:" + string, ex);
        }
        if (!treeNode.isObject()) {
            throw new TodException("The provided object was jacksonized to a string which does not represent a JSON object! String is " + string);
        }
        ObjectNode jo = (ObjectNode) treeNode;
        jo.put(fieldName, newNode);

        String json = jo.toString();

        logger.log(Level.FINE, "converted json = {0}", json);

        return json;

    }

    /**
     * Tests that the provided object can be converted to json and
     * reconstructed. Also logs the json with the provided logger at FINE level.
     *
     * @return the reconstructed object
     */
    public static <T> T testJsonConv(ObjectMapper om, Logger logger, @Nullable T obj) {

        checkNotNull(om);
        checkNotNull(logger);

        T recObj;

        String json;

        try {
            json = om.writeValueAsString(obj);
            logger.log(Level.FINE, "json = {0}", json);
        }
        catch (Exception ex) {
            throw new RuntimeException("FAILED SERIALIZING!", ex);
        }
        try {
            Object ret = om.readValue(json, obj.getClass());
            recObj = (T) ret;
        }
        catch (Exception ex) {
            throw new RuntimeException("FAILED DESERIALIZING!", ex);
        }

        assertEquals(obj, recObj);
        return recObj;
    }

    /**
     * Tests that the provided object can be converted to json and reconstructed
     * as type T. Also logs the json with the provided logger at FINE level.
     *
     * @return the reconstructed object
     */
    public static <T> T testJsonConv(ObjectMapper om, Logger logger, @Nullable Object obj, Class<T> targetClass) {

        checkNotNull(om);
        checkNotNull(logger);

        T recObj;

        String json;

        try {
            json = om.writeValueAsString(obj);
            logger.log(Level.FINE, "json = {0}", json);
        }
        catch (Exception ex) {
            throw new RuntimeException("FAILED SERIALIZING!", ex);
        }
        try {
            Object ret = om.readValue(json, targetClass);
            recObj = (T) ret;
        }
        catch (Exception ex) {
            throw new RuntimeException("FAILED DESERIALIZING!", ex);
        }

        assertEquals(obj, recObj);
        return recObj;
    }
}
