package com.petitcaillou.domain.text;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class TextNormalizer
{
  private static final Pattern MARKS = Pattern.compile("\\p{M}+");
  private static final Pattern SEPARATORS = Pattern.compile("[^\\p{L}\\p{N}]+");

  private TextNormalizer()
  {
  }

  public static String fold(String raw)
  {
    if (raw == null || raw.isBlank())
    {
      return "";
    }

    String normalized = Normalizer.normalize(raw.toLowerCase(Locale.ROOT), Normalizer.Form.NFKD)
      .replace("œ", "oe")
      .replace("æ", "ae")
      .replace("ß", "ss");

    normalized = MARKS.matcher(normalized).replaceAll("");
    return SEPARATORS.matcher(normalized).replaceAll(" ").trim();
  }
}
