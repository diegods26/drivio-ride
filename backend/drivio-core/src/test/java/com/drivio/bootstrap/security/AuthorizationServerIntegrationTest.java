package com.drivio.bootstrap.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationServerIntegrationTest {

    private static final String CLIENT_ID = "drivio-web";

    private static final String REDIRECT_URI =
            "http://localhost:5173/auth/callback";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(
            username = "diego@gmail.com",
            roles = "PASSENGER"
    )
    void shouldAuthorizeWithPkceAndExchangeCodeForToken()
            throws Exception {

        // 1. Gerar PKCE verifier
        String codeVerifier = generateCodeVerifier();

        String codeChallenge = generateCodeChallenge(
                codeVerifier
        );

        String state = "test-state-123";

        // 2. Authorization Request
        MvcResult authorizationResult =
                mockMvc.perform(
                                get("/oauth2/authorize")
                                        .queryParam("response_type", "code")
                                        .queryParam("client_id", CLIENT_ID)
                                        .queryParam("redirect_uri", REDIRECT_URI)
                                        .queryParam("scope", "api.read")
                                        .queryParam("state", state)
                                        .queryParam("code_challenge", codeChallenge)
                                        .queryParam("code_challenge_method", "S256")
                        )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(
                                redirectedUrlPattern(
                                        REDIRECT_URI + "?code=*&state=" + state
                                )
                        )
                        .andReturn();

        // 3. Extrair authorization code
        String location =
                authorizationResult
                        .getResponse()
                        .getHeader("Location");

        assertThat(location)
                .isNotNull();

        URI redirectUri = URI.create(location);

        String query = redirectUri.getRawQuery();

        String authorizationCode =
                extractQueryParameter(query, "code");

        String returnedState =
                extractQueryParameter(query, "state");

        assertThat(authorizationCode)
                .isNotBlank();

        assertThat(returnedState)
                .isEqualTo(state);

        // 4. Trocar authorization code por access token
        MvcResult tokenResult =
                mockMvc.perform(
                                post("/oauth2/token")
                                        .param(
                                                "grant_type",
                                                "authorization_code"
                                        )
                                        .param(
                                                "client_id",
                                                CLIENT_ID
                                        )
                                        .param(
                                                "redirect_uri",
                                                REDIRECT_URI
                                        )
                                        .param(
                                                "code",
                                                authorizationCode
                                        )
                                        .param(
                                                "code_verifier",
                                                codeVerifier
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn();

        String tokenResponse =
                tokenResult
                        .getResponse()
                        .getContentAsString();

        assertThat(tokenResponse)
                .contains("\"access_token\"");

        assertThat(tokenResponse)
                .contains("\"token_type\"");

        assertThat(tokenResponse)
                .doesNotContain("\"refresh_token\"");
    }

    private static String generateCodeVerifier()
            throws Exception {

        byte[] randomBytes = new byte[32];

        java.security.SecureRandom
                .getInstanceStrong()
                .nextBytes(randomBytes);

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    private static String generateCodeChallenge(
            String codeVerifier
    ) throws Exception {

        MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

        byte[] hash =
                digest.digest(
                        codeVerifier.getBytes(
                                StandardCharsets.US_ASCII
                        )
                );

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(hash);
    }

    private static String extractQueryParameter(
            String query,
            String parameter
    ) {

        for (String pair : query.split("&")) {

            String[] keyValue =
                    pair.split("=", 2);

            if (keyValue.length == 2
                    && keyValue[0].equals(parameter)) {

                return URLDecoder.decode(
                        keyValue[1],
                        StandardCharsets.UTF_8
                );
            }
        }

        return null;
    }
}
