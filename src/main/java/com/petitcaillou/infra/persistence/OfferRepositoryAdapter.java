package com.petitcaillou.infra.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.offer.ContentHash;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.domain.offer.OfferRepository;
import com.petitcaillou.domain.offer.SystemAccount;
import com.petitcaillou.domain.pagination.Page;
import com.petitcaillou.domain.pagination.PageRequest;
import com.petitcaillou.domain.user.Username;

@Component
public class OfferRepositoryAdapter implements OfferRepository
{
  private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "publicationDate");

  private final SpringDataOfferRepository jpa;

  public OfferRepositoryAdapter(SpringDataOfferRepository jpa)
  {
    this.jpa = jpa;
  }

  @Override
  public Offer save(Offer offer)
  {
    return OfferMapper.toDomain(jpa.save(OfferMapper.toEntity(offer)));
  }

  @Override
  public Optional<Offer> findById(OfferId id)
  {
    return jpa.findById(id.value().toString()).map(OfferMapper::toDomain);
  }

  @Override
  public boolean existsByCompany(CompanyId companyId)
  {
    return jpa.existsByCompanyId(companyId.value().toString());
  }

  @Override
  public Optional<Offer> findByHashAndCreatedBy(ContentHash hash, Username createdBy)
  {
    return jpa.findByContentHashAndCreatedBy(hash.value(), createdBy.value()).map(OfferMapper::toDomain);
  }

  @Override
  public Page<Offer> findVisibleTo(Username user, PageRequest pageRequest)
  {
    List<String> scopes = List.of(SystemAccount.USERNAME.value(), user.value());
    org.springframework.data.domain.Page<OfferEntity> page =
      jpa.findByCreatedByIn(scopes, PageMapper.toPageable(pageRequest, NEWEST_FIRST));
    return PageMapper.toDomain(page, pageRequest, OfferMapper::toDomain);
  }

  @Override
  public boolean existsById(OfferId id)
  {
    return jpa.existsById(id.value().toString());
  }
}
