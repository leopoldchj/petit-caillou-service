package com.petitcaillou.domain.offer;

import java.time.LocalDate;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.user.Username;

public final class Offer
{
  private final OfferId id;
  private final Username createdBy;
  private final CompanyId companyId;
  private final String title;
  private final String location;
  private final LocalDate publicationDate;
  private final String link;
  private final String description;
  private final boolean verified;
  private final ContentHash contentHash;

  private Offer(OfferId id, Username createdBy, OfferDetails details, boolean verified)
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
    this.link = details.link();
    this.description = details.description();
    this.verified = verified;
    this.contentHash = ContentHash.of(companyId, title, location, publicationDate);
  }

  public static Offer create(Username createdBy, OfferDetails details, boolean verified)
  {
    return new Offer(OfferId.newId(), createdBy, details, verified);
  }

  public static Offer reconstitute(OfferId id, Username createdBy, OfferDetails details, boolean verified)
  {
    return new Offer(id, createdBy, details, verified);
  }

  public boolean isPublic()
  {
    return SystemAccount.USERNAME.equals(createdBy);
  }

  public boolean isVisibleTo(Username user)
  {
    return isPublic() || createdBy.equals(user);
  }

  public Username dedupScope()
  {
    return createdBy;
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

  public String link()
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

  public ContentHash contentHash()
  {
    return contentHash;
  }
}
