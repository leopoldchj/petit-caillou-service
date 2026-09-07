package com.petitcaillou.application;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.petitcaillou.application.dto.OfferRequest;
import com.petitcaillou.application.dto.OfferView;
import com.petitcaillou.application.dto.PageView;
import com.petitcaillou.domain.authentication.AuthenticatedUser;
import com.petitcaillou.domain.company.CompanyId;
import com.petitcaillou.domain.offer.Offer;
import com.petitcaillou.domain.offer.OfferDetails;
import com.petitcaillou.domain.offer.OfferId;
import com.petitcaillou.service.CompanyService;
import com.petitcaillou.service.OfferService;

@RestController
@RequestMapping("/offers")
public class OfferResource
{
  private final OfferService offerService;
  private final CompanyService companyService;

  public OfferResource(OfferService offerService, CompanyService companyService)
  {
    this.offerService = offerService;
    this.companyService = companyService;
  }

  @GetMapping
  public PageView<OfferView> list(@CurrentUser AuthenticatedUser current,
    @RequestParam(name = "page", required = false) Integer page,
    @RequestParam(name = "size", required = false) Integer size)
  {
    return PageView.from(offerService.list(current.username(), PageParams.of(page, size)), this::view);
  }

  @GetMapping("/{id}")
  public OfferView get(@CurrentUser AuthenticatedUser current, @PathVariable String id)
  {
    return view(offerService.byId(current.username(), offerId(id)));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OfferView create(@CurrentUser AuthenticatedUser current, @Valid @RequestBody OfferRequest request)
  {
    OfferDetails details = new OfferDetails(
      CompanyId.of(UUID.fromString(request.companyId())),
      request.title(),
      request.location(),
      request.publicationDate(),
      request.link(),
      request.description());
    return view(offerService.createPrivate(current.username(), details));
  }

  private OfferView view(Offer offer)
  {
    return OfferView.from(offer, companyService.byId(offer.companyId()));
  }

  private OfferId offerId(String id)
  {
    return OfferId.of(UUID.fromString(id));
  }
}
