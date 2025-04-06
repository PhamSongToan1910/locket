package com.example.locket_clone.utils.ModelMapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public class ModelMapperUtils {
    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    static {
        MODEL_MAPPER.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);
    }

    public ModelMapperUtils() {
    }

    public static <T> void toObject(Object obj, T destination) {
        if (obj != null) {
            try {
                MODEL_MAPPER.map(obj, destination);
            } catch (Exception ex) {
                System.out.println(ex);
            }

        }
    }
}
