package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     *
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // TODO Task 1: Complete this method based on its provided documentation
        //      and the documentation for the dog.ceo API. You may find it helpful
        //      to refer to the examples of using OkHttpClient from the last lab,
        //      as well as the code for parsing JSON responses.
        // return statement included so that the starter code can compile and run.
        String normalized = (breed == null ? "" : breed.trim().toLowerCase(Locale.ROOT));
        if (normalized.isEmpty()) {
            throw new BreedNotFoundException("Breed name is empty.");
        }

        String url = "https://dog.ceo/api/breed/" + normalized + "/list";
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new BreedNotFoundException("Failed to fetch sub-breeds for: " + breed);
            }

            String body = response.body().string();
            JSONObject json = new JSONObject(body);

            // Dog CEO returns { "status": "success", "message": [...] } on success
            String status = json.optString("status", "error");
            if (!"success".equalsIgnoreCase(status)) {
                // When breed doesn't exist, API typically returns status "error" with a message
                String apiMsg = json.optString("message", "Breed not found.");
                throw new BreedNotFoundException(apiMsg);
            }

            JSONArray message = json.optJSONArray("message");
            List<String> result = new ArrayList<>();
            if (message != null) {
                for (int i = 0; i < message.length(); i++) {
                    result.add(message.getString(i));
                }
            }
            return result;
        } catch (Exception e) {
            // All failures must be reported as BreedNotFoundException
            throw new BreedNotFoundException("Could not fetch sub-breeds for: " + breed + " (" + e.getMessage() + ")");
        }
    }
}