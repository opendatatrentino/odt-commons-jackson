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
package eu.trentorise.opendata.commons.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.annotations.Beta;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

/**
 * Utility class to provide a simple interface to Jackson methods
 *
 * @author David Leoni
 */
@ParametersAreNonnullByDefault
@Immutable
@Beta
public final class Jacksonizer {

    /**
     * The singleton instance of the Jacksonizer
     */
    private static final Jacksonizer INSTANCE = new Jacksonizer();

    private ObjectMapper objectMapper;

    private Jacksonizer() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new GuavaModule());
        objectMapper.registerModule(new TodCommonsModule());
    }

    private Jacksonizer(ObjectMapper objectMapper) {
        this();
        checkNotNull(objectMapper);
        this.objectMapper = objectMapper;
    }

    /**
     * Returns a clone of the json object mapper used internally.
     */
    public ObjectMapper createJacksonMapper() {
        return objectMapper.copy();
    }

    /**
     * Returns a JSON representation of the provided object.
     *
     * @return the provided object in JSON format
     * @throws IllegalArgumentException on json error.
     */
    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        }
        catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Couldn't serialize provided object!", ex);
        }
    }

    /**
     * Reconstructs an object from provided json representation.
     *
     * @param clazz the Java class of the object to reconstruct.
     *
     * @throws IllegalArgumentException on json error.
     */
    public <T> T fromJson(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        }
        catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Couldn't deserialize provided SemText json: " + jsonString, ex);
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Couldn't deserialize provided SemText json: " + jsonString, ex);
        }
    }

    /**
     * Factory method, returning the Jacksonizer already configured for Tod
     * commons objects.
     */
    public static Jacksonizer of() {
        return INSTANCE;
    }

    /**
     * Factory method which returns the Jacksonizer wrapping the provided
     * object mapper.
     */
    public static Jacksonizer of(ObjectMapper objectMapper) {
        return new Jacksonizer(objectMapper);
    }
}
