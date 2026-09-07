package com.petitcaillou.domain.offer;

import java.util.List;

public record SourcePage(List<ScannedOffer> items, String nextCursor)
{
  public static final int MAX_ITEMS = 100;

  public SourcePage
  {
    items = List.copyOf(items);
    if (items.size() > MAX_ITEMS)
    {
      throw new IllegalArgumentException("A source page must contain at most " + MAX_ITEMS + " offers");
    }
  }
}
