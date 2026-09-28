package fr.cours.weather.api.infra.out.cache;

import fr.cours.weather.api.domain.model.Geocoding;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryGeocodingCacheTest {

    private final InMemoryGeocodingCache cache = new InMemoryGeocodingCache();

    @Test
    void get_returnsEmpty_whenAddressWasNeverCached() {
        assertThat(cache.get("Alès")).isEmpty();
    }

    @Test
    void get_returnsCachedValue_afterPut() {
        Geocoding geocoding = new Geocoding("Alès", 44.1277, 4.0817);

        cache.put("Alès", geocoding);

        assertThat(cache.get("Alès")).contains(geocoding);
    }

    @Test
    void get_doesNotMixEntries_forDifferentAddresses() {
        Geocoding ales = new Geocoding("Alès", 44.1277, 4.0817);
        Geocoding paris = new Geocoding("Paris", 48.85, 2.35);

        cache.put("Alès", ales);
        cache.put("Paris", paris);

        assertThat(cache.get("Alès")).contains(ales);
        assertThat(cache.get("Paris")).contains(paris);
    }

    @Test
    void get_returnsEmptyForUnknownAddress_afterOtherAddressWasCached() {
        cache.put("Alès", new Geocoding("Alès", 44.1277, 4.0817));

        Optional<Geocoding> result = cache.get("Nowhereville");

        assertThat(result).isEmpty();
    }
}
