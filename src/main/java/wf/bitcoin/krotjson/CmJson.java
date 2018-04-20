package wf.bitcoin.krotjson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CmJson {
  public static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY); // allow Jackson to see private fields
    MAPPER.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    MAPPER.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
    MAPPER.registerModule(new AfterburnerModule());
  }

  public static <T> String writeToStringQuietly(ObjectMapper mapper, T item) {
    try {
      return mapper.writeValueAsString(item);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> void writeToStreamQuietly(ObjectMapper mapper, T item, OutputStream os) {
    try {
      mapper.writeValue(os, item);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T readFromStreamQuietly(ObjectMapper mapper, InputStream is, Class<T> type) {
    try {
      return mapper.readValue(is, type);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T readFromStringQuietly(ObjectMapper mapper, String s, Class<T> type) {
    try {
      return mapper.readValue(s, type);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}