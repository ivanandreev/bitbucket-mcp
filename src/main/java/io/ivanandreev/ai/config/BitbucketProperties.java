package io.ivanandreev.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Reads Bitbucket connection settings.
 *
 * <p>Set via environment variables:
 * <ul>
 *   <li>BITBUCKET_URL  – base URL, e.g. https://bitbucket.example.com</li>
 *   <li>BITBUCKET_TOKEN – personal access token</li>
 * </ul>
 */
@ConfigurationProperties(prefix = "bitbucket")
public record BitbucketProperties(String url, String token) {

    public BitbucketProperties {
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("BITBUCKET_URL environment variable is required");
        }
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("BITBUCKET_TOKEN environment variable is required");
        }
    }
}
