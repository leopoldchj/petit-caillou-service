package com.petitcaillou.domain.offer;

import java.time.LocalDate;
import java.util.Objects;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.user.Username;
import com.petitcaillou.domain.web.Url;

public final class Offer
{
  private final OfferId id;
  private final Username createdBy;
  private final CompanyId companyId;
  private final String title;
  private final String location;
  private final LocalDate publicationDate;
  private final Url link;
  private final String description;
  private final boolean verified;
  private final ContentHash dedupKey;

  private Offer(OfferId id, Username createdBy, OfferDetails details, boolean verified, ContentHash dedupKey)
  {
    if (createdBy == null)
    {
      throw new IllegalArgumentException("Offer creator is required");
    }
    if (details.companyId() == null)
    {
      throw new IllegalArgumentException("Company is required");
    }
    if (details.title() == null || details.title().isBlank())
    {
      throw new IllegalArgumentException("Title is required");
    }
    if (details.publicationDate() == null)
    {
      throw new IllegalArgumentException("Publication date is required");
    }

    this.id = id;
    this.createdBy = createdBy;
    this.companyId = details.companyId();
    this.title = details.title();
    this.location = details.location();
    this.publicationDate = details.publicationDate();
    this.link = Url.ofNullable(details.link());
    this.description = details.description();
    this.verified = verified;
    this.dedupKey = dedupKey;
  }

  public static Offer create(Username createdBy, OfferDetails details, boolean verified)
  {
    return new Offer(OfferId.newId(), createdBy, details, verified, contentKey(details));
  }

  public static Offer ingested(OfferDetails details, boolean verified, String source, String externalId)
  {
    ContentHash key = externalId == null || externalId.isBlank()
      ? contentKey(details)
      : ContentHash.ofSource(source, externalId);
    return new Offer(OfferId.newId(), SystemAccount.USERNAME, details, verified, key);
  }

  public static Offer reconstitute(OfferId id, Username createdBy, OfferDetails details, boolean verified)
  {
    return new Offer(id, createdBy, details, verified, contentKey(details));
  }

  public static Offer reconstitute(OfferId id, Username createdBy, OfferDetails details, boolean verified,
    ContentHash dedupKey)
  {
    return new Offer(id, createdBy, details, verified, dedupKey);
  }

  private static ContentHash contentKey(OfferDetails details)
  {
    return ContentHash.of(details.companyId(), details.title(), details.location(), details.publicationDate());
  }

  public Offer enrichedWith(OfferDetails incoming)
  {
    OfferDetails merged = new OfferDetails(
      companyId,
      prefer(incoming.title(), title),
      prefer(incoming.location(), location),
      incoming.publicationDate() == null ? publicationDate : incoming.publicationDate(),
      prefer(incoming.link(), link == null ? null : link.value()),
      prefer(incoming.description(), description));
    return new Offer(id, createdBy, merged, verified, dedupKey);
  }

  public boolean hasSameContent(Offer other)
  {
    return companyId.equals(other.companyId)
      && Objects.equals(title, other.title)
      && Objects.equals(location, other.location)
      && Objects.equals(publicationDate, other.publicationDate)
      && Objects.equals(link, other.link)
      && Objects.equals(description, other.description)
      && verified == other.verified;
  }

  private static String prefer(String incoming, String existing)
  {
    return incoming == null || incoming.isBlank() ? existing : incoming;
  }

  public OfferDetails details()
  {
    return new OfferDetails(companyId, title, location, publicationDate, link == null ? null : link.value(), description);
  }

  public boolean isPublic()
  {
    return SystemAccount.USERNAME.equals(createdBy);
  }

  public OfferVisibility visibility()
  {
    return isPublic() ? OfferVisibility.PUBLIC : OfferVisibility.PRIVATE;
  }

  public boolean isVisibleTo(Username user)
  {
    return isPublic() || createdBy.equals(user);
  }

  public OfferId id()
  {
    return id;
  }

  public Username createdBy()
  {
    return createdBy;
  }

  public CompanyId companyId()
  {
    return companyId;
  }

  public String title()
  {
    return title;
  }

  public String location()
  {
    return location;
  }

  public LocalDate publicationDate()
  {
    return publicationDate;
  }

  public Url link()
  {
    return link;
  }

  public String description()
  {
    return description;
  }

  public boolean verified()
  {
    return verified;
  }

  public ContentHash dedupKey()
  {
    return dedupKey;
  }
}
