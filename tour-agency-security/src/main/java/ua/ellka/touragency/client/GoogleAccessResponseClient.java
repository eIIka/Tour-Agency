package ua.ellka.touragency.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;
import ua.ellka.touragency.model.OAuth2Token;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Set;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class GoogleAccessResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
    private final HttpClient httpClient;

    @Value("${spring.security.oauth2.client.provider.google.token-uri:}")
    private String tokenUrl;

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest req) {
        String code = req.getAuthorizationExchange().getAuthorizationResponse().getCode();
        String redirectUri = req.getAuthorizationExchange().getAuthorizationResponse().getRedirectUri();

        try {
            HttpRequest httpReq = HttpRequest.newBuilder()
                    .uri(new URI(tokenUrl))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "code=" + code + "&" +
                            "client_id=" + req.getClientRegistration().getClientId() + "&" +
                            "redirect_uri=" + redirectUri + "&" +
                            "grant_type=authorization_code&" +
                            "client_secret=" + req.getClientRegistration().getClientSecret()))
                    .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .build();

            HttpResponse<String> resp = httpClient.send(httpReq, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();

            OAuth2Token oAuth2Token = objectMapper.readValue(resp.body(), OAuth2Token.class);

            return OAuth2AccessTokenResponse.withToken(oAuth2Token.getAccessToken())
                    .expiresIn(Long.parseLong(oAuth2Token.getExpiresIn()))
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    .refreshToken(oAuth2Token.getRefreshToken())
                    .scopes(Set.of(oAuth2Token.getScope()))
                    .additionalParameters(Map.of(OIDCResponseTypeValue.ID_TOKEN.toString(), oAuth2Token.getIdToken()))
                    .build();

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
