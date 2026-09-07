package com.petitcaillou.domain.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

/**
 * An http(s) web address. It validates and canonicalizes on construction
 * (lowercased scheme and host, default ports and fragments dropped) so two
 * spellings of the same address compare equal, and answers questions about
 * itself rather than exposing a bare string.
 */
public final class Url
{
  private static final int MAX_LENGTH = 2048;
  private static final List<String> SCHEMES = List.of("http", "https");

  private final String value;
  private final String host;
  private final boolean secure;

  private Url(String value, String host, boolean secure)
  {
    this.value = value;
    this.host = host;
    this.secure = secure;
  }

  public static Url ofNullable(String raw)
  {
    return raw == null || raw.isBlank() ? null : of(raw);
  }

  public static Url of(String raw)
  {
    if (raw == null || raw.isBlank())
    {
      throw new IllegalArgumentException("A URL is required");
    }
    if (raw.length() > MAX_LENGTH)
    {
      throw new IllegalArgumentException("A URL must not exceed " + MAX_LENGTH + " characters");
    }

    URI uri;
    try
    {
      uri = new URI(raw.strip());
    }
    catch (URISyntaxException exception)
    {
      throw new IllegalArgumentException("Invalid URL: " + raw);
    }

    String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
    if (!SCHEMES.contains(scheme) || uri.getHost() == null || uri.getRawUserInfo() != null)
    {
      throw new IllegalArgumentException("A URL must be a plain http(s) address: " + raw);
    }

    String host = uri.getHost().toLowerCase(Locale.ROOT);
    boolean secure = scheme.equals("https");
    String authority = host + portSuffix(scheme, uri.getPort());
    String path = uri.getRawPath() == null || uri.getRawPath().isEmpty() ? "/" : uri.getRawPath();
    String query = uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery();

    return new Url(scheme + "://" + authority + path + query, host, secure);
  }

  private static String portSuffix(String scheme, int port)
  {
    boolean isDefault = port == -1
      || scheme.equals("https") && port == 443
      || scheme.equals("http") && port == 80;
    return isDefault ? "" : ":" + port;
  }

  public String value()
  {
    return value;
  }

  public String host()
  {
    return host;
  }

  public boolean isSecure()
  {
    return secure;
  }

  @Override
  public boolean equals(Object other)
  {
    return this == other
      || other instanceof Url url && value.equals(url.value);
  }

  @Override
  public int hashCode()
  {
    return value.hashCode();
  }
}
