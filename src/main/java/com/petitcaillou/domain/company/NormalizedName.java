package com.petitcaillou.domain.company;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.petitcaillou.domain.text.TextNormalizer;

public final class NormalizedName
{
  private static final List<String> LEGAL_SUFFIXES =
    Stream.of("sas", "sasu", "sarl", "sa", "eurl", "inc", "llc", "ltd", "ltda", "plc", "gmbh", "ag", "bv", "co", "corp")
      .flatMap(suffix -> suffix.length() >= 3 ? Stream.of(suffix, spaced(suffix)) : Stream.of(suffix))
      .distinct()
      .sorted(Comparator.comparingInt(String::length).reversed())
      .toList();

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
      throw new IllegalArgumentException("Company name must contain at least one letter or digit");
    }
    return new NormalizedName(canonical);
  }

  public static String canonical(String rawName)
  {
    String result = TextNormalizer.fold(rawName);

    while (!result.isEmpty())
    {
      String shortened = removeTrailingSuffix(result);
      if (shortened.equals(result))
      {
        return result;
      }
      result = shortened;
    }

    return result;
  }

  private static String removeTrailingSuffix(String name)
  {
    for (String suffix : LEGAL_SUFFIXES)
    {
      String ending = " " + suffix;
      if (name.endsWith(ending))
      {
        return name.substring(0, name.length() - ending.length());
      }
    }
    return name;
  }

  private static String spaced(String value)
  {
    return String.join(" ", value.split(""));
  }

  public String value()
  {
    return value;
  }

  @Override
  public boolean equals(Object other)
  {
    return this == other
      || other instanceof NormalizedName name && value.equals(name.value);
  }

  @Override
  public int hashCode()
  {
    return value.hashCode();
  }
}
