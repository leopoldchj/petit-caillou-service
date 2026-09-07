package com.petitcaillou.domain.offer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.text.TextNormalizer;

public final class ContentHash
{
  private static final String HASH_ALGORITHM = "SHA-256";

  private final String value;

  private ContentHash(String value)
  {
    this.value = value;
  }

  public static ContentHash of(CompanyId companyId, String title, String location, LocalDate publicationDate)
  {
    String seed = String.join(
      "|",
      companyId.value().toString(),
      TextNormalizer.fold(title),
      TextNormalizer.fold(location),
      publicationDate == null ? "" : publicationDate.toString());
    return new ContentHash(sha256(seed));
  }

  public static ContentHash ofValue(String value)
  {
    return new ContentHash(value);
  }

  private static String sha256(String seed)
  {
    try
    {
      MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
      return HexFormat.of().formatHex(digest.digest(seed.getBytes(StandardCharsets.UTF_8)));
    }
    catch (NoSuchAlgorithmException exception)
    {
      throw new IllegalStateException(HASH_ALGORITHM + " is required but unavailable", exception);
    }
  }

  public String value()
  {
    return value;
  }

  @Override
  public boolean equals(Object other)
  {
    if (this == other)
    {
      return true;
    }
    if (!(other instanceof ContentHash hash))
    {
      return false;
    }
    return value.equals(hash.value);
  }

  @Override
  public int hashCode()
  {
    return value.hashCode();
  }
}
