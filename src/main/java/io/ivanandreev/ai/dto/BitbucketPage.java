package io.ivanandreev.ai.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Flat view of Bitbucket's paged-response envelope.
 *
 * <p>The OpenAPI-generated {@code Get*200Response} types all carry the same set
 * of fields ({@code values}, {@code size}, {@code limit}, {@code isLastPage},
 * {@code nextPageStart}) but share no common supertype, so the client wraps each
 * of them into this record via {@link #of}.
 */
public record BitbucketPage<T>(
        List<T> values,
        int size,
        int limit,
        boolean isLastPage,
        Integer nextPageStart
) {

    public BitbucketPage {
        values = values != null ? values : List.of();
    }

    /** Pagination cursor for the next page; {@code 0} when this is the last page. */
    public int nextStart() {
        return nextPageStart != null ? nextPageStart : 0;
    }

    /** Wraps the fields of a generated page response into a {@code BitbucketPage}. */
    public static <T> BitbucketPage<T> of(List<T> values, BigDecimal size, BigDecimal limit,
                                          Boolean isLastPage, Integer nextPageStart) {
        return new BitbucketPage<>(
                values,
                size != null ? size.intValue() : (values != null ? values.size() : 0),
                limit != null ? limit.intValue() : 0,
                Boolean.TRUE.equals(isLastPage),
                nextPageStart);
    }
}
