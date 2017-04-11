package org.sunyata.quark.embed.springcloud;

import com.fasterxml.jackson.databind.*;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;

/**
 * Created by leo on 17/4/10.
 */
public class JacksonDecoder implements Decoder {

    Logger logger = LoggerFactory.getLogger(JacksonDecoder.class);
    private final ObjectMapper mapper;

    public JacksonDecoder() {
        this(Collections.<Module>emptyList());
    }

    public JacksonDecoder(Iterable<Module> modules) {
        this(new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .registerModules(modules));
    }

    public JacksonDecoder(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        if (response.status() == 404) return Util.emptyValueOf(type);
        if (response.body() == null) return null;
        Reader reader = response.body().asReader();
        if (!reader.markSupported()) {
            reader = new BufferedReader(reader, 1);
        }
        try {
            // Read the first byte to see if we have any data
            reader.mark(1);
            if (reader.read() == -1) {
                return null; // Eagerly returning null avoids "No content to map due to end-of-input"
            }
            reader.reset();
            Object o = mapper.readValue(reader, mapper.constructType(type));
            return o;
        } catch (RuntimeJsonMappingException e) {
            if (e.getCause() != null && e.getCause() instanceof IOException) {
                throw IOException.class.cast(e.getCause());
            }
            throw e;
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getFullStackTrace(ex));
            throw ex;
        }
    }
}