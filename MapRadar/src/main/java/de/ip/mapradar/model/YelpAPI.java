package de.ip.mapradar.model;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

public class YelpAPI {
    // REFERENCE SEE http://www.yelp.com/developers/
    private final String
            REQ_LIMIT = "limit",
            REQ_SEARCHTERM = "term",
            REQ_SORTING = "sort",
            REQ_RADIUS = "radius_filter",
            REQ_CATEGORIES = "category_filter",
            REQ_LOCATION = "location";
    private static final String API_HOST = "http://api.yelp.com/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";
    OAuthService service;
    Token accessToken;

    /**
     * Setup the Yelp API OAuth credentials.
     *
     * @param consumerKey    Consumer key
     * @param consumerSecret Consumer secret
     * @param token          Token
     * @param tokenSecret    Token secret
     */
    public YelpAPI(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service = new ServiceBuilder()
                .provider(TwoStepOAuth.class)
                .apiKey(consumerKey)
                .apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
    }

    public String searchForBusiness(String searchTerm, String location, String category, int maxResults, int sort, int radius) {
        StringBuilder sb = new StringBuilder(API_HOST).append("?").append(REQ_LOCATION).append("=").append(location);
        if (maxResults > 0) {
            sb.append(param(REQ_LIMIT, String.valueOf(maxResults)));
        }
        if (sort >= 0 && sort <= 2) {
            sb.append(param(REQ_SORTING, String.valueOf(sort)));
        }
        if (radius > 1 && radius < 40000) { //40000 is the max allowed radius
            sb.append(param(REQ_RADIUS, String.valueOf(radius)));
        }
        if (category != null) {
            sb.append(param(REQ_CATEGORIES, category));
        }
        OAuthRequest request = new OAuthRequest(Verb.GET, sb.toString());
        if (searchTerm != null) {
            request.addQuerystringParameter(REQ_SEARCHTERM, searchTerm);
        }
        return sendRequestAndGetResponse(request);
    }

    public String searchByBusinessId(String businessID) {

        businessID = businessID
                .replaceAll("ö", "oe");
        OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
        return sendRequestAndGetResponse(request);
    }

    /**
     * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
     *
     * @param path API endpoint to be queried
     * @return <tt>OAuthRequest</tt>
     */
    private OAuthRequest createOAuthRequest(String path) {
        return new OAuthRequest(Verb.GET, "http://" + "api.yelp.com" + path);
    }

    private String param(final String key, final String value) {
        return "&" + key + "=" + value;
    }

    private String sendRequestAndGetResponse(OAuthRequest request) {

        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    public static final class TwoStepOAuth extends DefaultApi10a {
        public String getAccessTokenEndpoint() {
            return null;
        }

        public String getAuthorizationUrl(Token arg0) {
            return null;
        }

        public String getRequestTokenEndpoint() {
            return null;
        }
    }
}
