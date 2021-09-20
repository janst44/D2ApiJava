import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/19/21
 */
public class JsonUtilities {

    private JsonUtilities() {}

    public static AllHeroStats getAllHeroObj(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, AllHeroStats.class);
    }

    public static String getAllHeroJson(AllHeroStats allHeroStats) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(allHeroStats);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

}
