package com.example.locket_clone.utils.ModelMapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public class ModelMapperUtils {
    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    static {
        MODEL_MAPPER.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public ModelMapperUtils() {
    }

    public static <T> T toObject(Object obj, Class<T> type) {
        T t = null;
        if (obj != null) {
            try {
                t = MODEL_MAPPER.map(obj, type);
            } catch (Exception ex) {
                System.out.println(ex);
            }

        }
        return t;
    }
}
