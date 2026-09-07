package com.petitcaillou.infra.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.petitcaillou.domain.offer.ContentHash;
import com.petitcaillou.domain.offer.OfferWriteResult;
import com.petitcaillou.domain.offer.ScannedOffer;
import com.petitcaillou.domain.offer.SystemAccount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogWriterAdapterTest
{
  @Captor
  private ArgumentCaptor<List<OfferEntity>> written;

  private static final LocalDate PUBLISHED = LocalDate.of(2026, 8, 30);
  private static final String COMPANY_ID = UUID.randomUUID().toString();
  private static final String DEDUP_KEY = ContentHash.ofSource("board", "42").value();
  private static final ScannedOffer SCANNED = new ScannedOffer(
    "Acme", "https://acme.example", "Backend Engineer", "Paris", PUBLISHED,
    "https://x/1", "role", "board", "42");

  private final SpringDataCompanyRepository companies = mock(SpringDataCompanyRepository.class);
  private final SpringDataOfferRepository offers = mock(SpringDataOfferRepository.class);
  private final CatalogWriterAdapter adapter = new CatalogWriterAdapter(companies, offers,
    new TransactionTemplate(mock(PlatformTransactionManager.class)));

  private CompanyEntity knownCompany()
  {
    return new CompanyEntity(COMPANY_ID, "Acme", "acme", "https://acme.example");
  }

  private OfferEntity storedOffer(String title)
  {
    return new OfferEntity(UUID.randomUUID().toString(), COMPANY_ID, title, "Paris", PUBLISHED,
      "https://x/1", "role", SystemAccount.USERNAME.value(), false, DEDUP_KEY);
  }

  private void companyIsKnown()
  {
    when(companies.findByNormalizedNameIn(anyCollection())).thenReturn(List.of(knownCompany()));
  }

  private void offerIsKnown(String title)
  {
    when(offers.findByCreatedByAndContentHashIn(eq(SystemAccount.USERNAME.value()), anyCollection()))
      .thenReturn(List.of(storedOffer(title)));
  }

  @Test
  void given_unknownCompany_when_ingesting_then_createsIt()
  {
    adapter.ingestAll(List.of(SCANNED));

    verify(companies).saveAll(anyCollection());
  }

  @Test
  void given_newOffer_when_ingesting_then_reportsCreated()
  {
    companyIsKnown();

    assertThat(adapter.ingestAll(List.of(SCANNED)).getFirst().outcome())
      .isEqualTo(OfferWriteResult.Outcome.CREATED);
  }

  @Test
  void given_knownOfferWithSameContent_when_ingesting_then_reportsUnchanged()
  {
    companyIsKnown();
    offerIsKnown("Backend Engineer");

    assertThat(adapter.ingestAll(List.of(SCANNED)).getFirst().outcome())
      .isEqualTo(OfferWriteResult.Outcome.UNCHANGED);
  }

  @Test
  void given_knownOfferWithSameContent_when_ingesting_then_writesNothing()
  {
    companyIsKnown();
    offerIsKnown("Backend Engineer");

    adapter.ingestAll(List.of(SCANNED));

    verify(offers, times(2)).saveAll(List.of());
  }

  @Test
  void given_knownOfferWithChangedContent_when_ingesting_then_reportsUpdated()
  {
    companyIsKnown();
    offerIsKnown("Junior Engineer");

    assertThat(adapter.ingestAll(List.of(SCANNED)).getFirst().outcome())
      .isEqualTo(OfferWriteResult.Outcome.UPDATED);
  }

  @Test
  void given_duplicateWithinTheSameBatch_when_ingesting_then_writesItOnce()
  {
    companyIsKnown();

    adapter.ingestAll(List.of(SCANNED, SCANNED));

    verify(offers, times(2)).saveAll(written.capture());
    assertThat(written.getAllValues().getFirst()).hasSize(1);
  }

  @Test
  void given_invalidCompanyName_when_ingesting_then_reportsFailure()
  {
    ScannedOffer invalid = new ScannedOffer("!!!", null, "Backend Engineer", "Paris", PUBLISHED,
      null, null, "board", "43");

    assertThat(adapter.ingestAll(List.of(invalid)).getFirst().outcome())
      .isEqualTo(OfferWriteResult.Outcome.FAILED);
  }

  @Test
  void given_aBatch_when_ingesting_then_looksUpCompaniesOnce()
  {
    companyIsKnown();

    adapter.ingestAll(List.of(SCANNED, SCANNED, SCANNED));

    verify(companies, times(1)).findByNormalizedNameIn(anyCollection());
  }

  @Test
  void given_aBatch_when_ingesting_then_looksUpExistingOffersOnce()
  {
    companyIsKnown();

    adapter.ingestAll(List.of(SCANNED, SCANNED, SCANNED));

    verify(offers, times(1)).findByCreatedByAndContentHashIn(any(), anyCollection());
  }
}
