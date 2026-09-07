package com.petitcaillou.domain.company;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

public final class NormalizedName
{
  private static final List<String> LEGAL_SUFFIXES =
    List.of("sas", "sasu", "sarl", "sa", "eurl", "inc", "llc", "ltd", "ltda", "plc", "gmbh", "ag", "bv", "co", "corp");

  private final String value;

  private NormalizedName(String value)
  {
    this.value = value;
  }

  public static NormalizedName of(String rawName)
  {
    String canonical = canonical(rawName);
    if (canonical.isEmpty())
    {
      throw new IllegalArgumentException("Company name does not normalize to anything meaningful");
    }
    return new NormalizedName(canonical);
  }

  public static String canonical(String rawName)
  {
    String base = text(rawName);
    if (base.isEmpty())
    {
      return base;
    }

    List<String> tokens = new java.util.ArrayList<>(List.of(base.split(" ")));
    while (tokens.size() > 1 && LEGAL_SUFFIXES.contains(tokens.get(tokens.size() - 1)))
    {
      tokens.remove(tokens.size() - 1);
    }
    return String.join(" ", tokens);
  }

  public static String text(String raw)
  {
    if (raw == null)
    {
      return "";
    }
    String withoutAccents = Normalizer.normalize(raw, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
    return withoutAccents.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
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
    if (!(other instanceof NormalizedName name))
    {
      return false;
    }
    return value.equals(name.value);
  }

  @Override
  public int hashCode()
  {
    return value.hashCode();
  }
}
