package dogapi;

import java.io.IOException;
import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    // TODO Task 2: Complete this class
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = Objects.requireNonNull(fetcher, "fetcher must not be null");
    }
    private static String normalize(String breed) {
        return breed == null ? "null" : breed.trim().toLowerCase(Locale.ROOT);
    }
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {

        final String key = normalize(breed);

        List<String> cached = cache.get(key);
        if (cached != null) {
            return cached;
        }

        callsMade++;
        List<String> result = fetcher.getSubBreeds(breed);
        List<String> toCache = List.copyOf(result);
        cache.put(key, toCache);
        return toCache;
    }

    public int getCallsMade() {
        return callsMade;
    }
}
