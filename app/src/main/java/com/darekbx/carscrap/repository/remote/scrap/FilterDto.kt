package com.darekbx.carscrap.repository.remote.scrap

/**
 * Filter request
 */
data class FilterRequest(
    val operationName: String = "listingScreen",
    val query: String = "query listingScreen(\$after: ID, \$experiments: [Experiment!], \$filters: [AdvertSearchFilterInput!], \$includePromotedAds: Boolean!, \$includeRatings: Boolean!, \$includeFiltersCounters: Boolean!, \$includeSortOptions: Boolean!, \$includeSuggestedFilters: Boolean!, \$itemsPerPage: Int, \$page: Int, \$parameters: [String!], \$searchTerms: [String!], \$sortBy: String, \$maxAge: Int, \$includeCepik: Boolean!, \$includeNewPromotedAds: Boolean!, \$promotedInput: AdSearchInput!) {\n  advertSearch(\n    criteria: {searchTerms: \$searchTerms, filters: \$filters}\n    sortBy: \$sortBy\n    page: \$page\n    after: \$after\n    itemsPerPage: \$itemsPerPage\n    maxAge: \$maxAge\n    experiments: \$experiments\n  ) {\n    ...advertSearchFields\n    edges {\n      node {\n        ...lazyAdvertFields\n              }\n          }\n    sortOptions @include(if: \$includeSortOptions) {\n      searchKey\n      label\n          }\n      }\n  ...promotedAds @include(if: \$includeNewPromotedAds)\n  ...suggestedFilters @include(if: \$includeSuggestedFilters)\n}\nfragment advertSearchFields on AdvertSearchOutput {\n  url\n  sortedBy\n  locationCriteriaChanged\n  subscriptionKey\n  totalCount\n  filtersCounters @include(if: \$includeFiltersCounters) {\n    name\n    nodes {\n      name\n      value\n          }\n      }\n  appliedLocation {\n    city {\n      name\n      id\n      canonical\n          }\n    subregion {\n      name\n      id\n      canonical\n          }\n    region {\n      name\n      id\n      canonical\n          }\n    latitude\n    longitude\n    mapConfiguration {\n      zoom\n          }\n      }\n  pageInfo {\n    pageSize\n    currentOffset\n      }\n  latestAdId\n  edges {\n    ...listingAdCardFields\n      }\n  topads @include(if: \$includePromotedAds) {\n    ...listingAdCardFields\n      }\n  }\nfragment listingAdCardFields on AdvertEdge {\n node {\n    ...advertFields\n      }\n  }\nfragment advertFields on Advert {\n  id\n  title\n  createdAt\n  shortDescription\n  url\n price {\n    amount {\n      units\n      nanos\n      value\n      currencyCode\n          }\n    badges\n    grossPrice {\n      value\n      currencyCode\n          }\n    netPrice {\n      value\n      currencyCode\n          }\n      }\n  parameters(keys: \$parameters) {\n    key\n    displayValue\n    label\n    value\n      }\n  }\nfragment lazyAdvertFields on Advert {\n  id\n  cepikVerified @include(if: \$includeCepik)\n  sellerRatings(scope: PROFESSIONAL) @include(if: \$includeRatings) {\n    statistics {\n      recommend {\n        value\n        suffix\n              }\n      avgRating {\n        value\n              }\n      total {\n        suffix\n        value\n              }\n      detailedRating {\n        label\n        value\n              }\n          }\n      }\n  }fragment promotedAds on Query {\n  promoted: adSearch {\n    search(input: \$promotedInput) {\n      ... on AdSearchOutput {\n        ads {\n          ...adFields\n                  }\n              }\n      ... on AdSearchError {\n        message\n              }\n          }\n      }\n  }\nfragment adFields on Ad {\n  id\n  url\n  title\n  location {\n    cityName\n    regionName\n      }\n  description\n  badges\n  createdAt\n  updatedAt\n  photos\n  price {\n    currencyCode\n    ... on AdNetGrossPrice {\n      currencyCode\n      netMinorAmount\n      grossMinorAmount\n      isNet\n          }\n      }\n  attributes {\n    key\n    value\n    valueLabel\n    valueSuffix\n      }\n  valueAddedServices {\n    name\n    validity\n    appliedAt\n      }\n  brandProgram {\n    ... on BrandProgram {\n      logo {\n        url\n              }\n      name\n      url\n      id\n          }\n      }\n  seller {\n    ... on ProfessionalSeller {\n      name\n      uuid\n      dealerAdsUrl\n      logo {\n        url\n              }\n      serviceOptions {\n        label\n        code\n        url\n              }\n      benefits(codes: [DEALER_IDENTITY_ELEMENTS])\n      ratings {\n        statistics {\n          recommend {\n            value\n                      }\n          avgRating {\n            value\n                      }\n                  }\n              }\n          }\n    ... on PrivateSeller {\n      name\n      uuid\n          }\n      }\n  }\nfragment suggestedFilters on Query {\n  suggestedFilters(criteria: {searchTerms: \$searchTerms, filters: \$filters}) {\n    key\n    name\n    values {\n      value\n      appliedFilters {\n        name\n        value\n              }\n          }\n      }\n  }",
    val variables: FilterVariables = FilterVariables()
) {
    fun setFilterValues(
        make: String,
        model: String,
        generation: String?,
        damaged: Boolean = false
    ) {
        variables.filters = variables.filters.map {
            when (it.name) {
                "filter_enum_make" -> it.copy(value = make)
                "filter_enum_model" -> it.copy(value = model)
                "filter_enum_generation" -> generation?.let { it1 -> it.copy(value = it1) } ?: it
                "filter_enum_damaged" -> it.copy(value = if (damaged) "1" else "0")
                else -> it
            }
        }
    }
}

data class FilterVariables(
    val after: String? = null,
    var filters: List<Filter> = listOf(
        Filter("filter_enum_make", ""),
        Filter("filter_enum_model", ""),
        Filter("filter_enum_generation", ""),
        Filter("filter_enum_damaged", ""),
    ),
    val includeFiltersCounters: Boolean = false,
    val includeNewPromotedAds: Boolean = false,
    val includePromotedAds: Boolean = false,
    val includeSortOptions: Boolean = false,
    val includeSuggestedFilters: Boolean = false,
    val includeRatings: Boolean = false,
    val includeCepik: Boolean = false,
    val maxAge: Int = 60,
    var page: Int = 0,
    val parameters: List<String> = listOf(
        "make",
        "offer_type",
        "fuel_type",
        "gearbox",
        "country_origin",
        "mileage",
        "engine_capacity",
        "engine_code",
        "engine_power",
        "first_registration_year",
        "model",
        "version",
        "year"
    ),
    val promotedInput: Map<String, Any> = emptyMap(),
    val searchTerms: List<String>? = null
)


/**
 * Verification request
 */
data class VerificationRequest(
    val operationName: String = "listingScreen",
    val query: String = "query listingScreen(\$after: ID, \$experiments: [Experiment!], \$filters: [AdvertSearchFilterInput!], \$includePromotedAds: Boolean!, \$includeFiltersCounters: Boolean!, \$includeSortOptions: Boolean!, \$includeSuggestedFilters: Boolean!, \$itemsPerPage: Int, \$page: Int, \$parameters: [String!], \$searchTerms: [String!], \$sortBy: String, \$maxAge: Int, \$includeNewPromotedAds: Boolean!, \$promotedInput: AdSearchInput!) { advertSearch(criteria: { searchTerms: \$searchTerms, filters: \$filters }, sortBy: \$sortBy, page: \$page, after: \$after, itemsPerPage: \$itemsPerPage, maxAge: \$maxAge, experiments: \$experiments) { ...advertSearchFields sortOptions @include(if: \$includeSortOptions) { searchKey label } } ...promotedAds @include(if: \$includeNewPromotedAds) ...suggestedFilters @include(if: \$includeSuggestedFilters) } fragment advertSearchFields on AdvertSearchOutput { url sortedBy locationCriteriaChanged subscriptionKey totalCount filtersCounters @include(if: \$includeFiltersCounters) { name nodes { name value } } appliedLocation { city { name id canonical } subregion { name id canonical } region { name id canonical } latitude longitude mapConfiguration { zoom } } pageInfo { pageSize currentOffset } latestAdId topads @include(if: \$includePromotedAds) { ...listingAdCardFields } } fragment listingAdCardFields on AdvertEdge { node { ...advertFields } } fragment advertFields on Advert { id title createdAt shortDescription url price { amount { units nanos value currencyCode } badges grossPrice { value currencyCode } netPrice { value currencyCode } } parameters(keys: \$parameters) { key displayValue label value } } fragment promotedAds on Query { promoted: adSearch { search(input: \$promotedInput) { ... on AdSearchOutput { ads { ...adFields } } ... on AdSearchError { message } } } } fragment adFields on Ad { id url title location { cityName regionName } description badges createdAt updatedAt photos price { currencyCode ... on AdNetGrossPrice { currencyCode netMinorAmount grossMinorAmount isNet } } attributes { key value valueLabel valueSuffix } valueAddedServices { name validity appliedAt } brandProgram { ... on BrandProgram { logo { url } name url id } } seller { ... on ProfessionalSeller { name uuid dealerAdsUrl logo { url } serviceOptions { label code url } benefits(codes: [DEALER_IDENTITY_ELEMENTS]) ratings { statistics { recommend { value } avgRating { value } } } } ... on PrivateSeller { name uuid } } } fragment suggestedFilters on Query { suggestedFilters(criteria: { searchTerms: \$searchTerms, filters: \$filters }) { key name values { value appliedFilters { name value } } } }\n",
    val variables: VerificationVariables = VerificationVariables()
) {
    fun setFilterValues(make: String, model: String) {
        variables.filters = variables.filters.map {
            when (it.name) {
                "filter_enum_make" -> it.copy(value = make)
                "filter_enum_model" -> it.copy(value = model)
                else -> it
            }
        }
    }
}

data class VerificationVariables(
    val after: String? = null,
    var filters: List<Filter> = listOf(
        Filter("filter_enum_make", ""),
        Filter("filter_enum_model", "")
    ),
    val includeFiltersCounters: Boolean = false,
    val includeNewPromotedAds: Boolean = false,
    val includePromotedAds: Boolean = false,
    val includeSortOptions: Boolean = false,
    val includeSuggestedFilters: Boolean = false,
    val maxAge: Int = 60,
    val page: Int = 0,
    val parameters: List<String> = listOf("make"),
    val promotedInput: Map<String, Any> = emptyMap(),
    val searchTerms: List<String>? = null
)

data class Filter(
    val name: String,
    val value: String
)

/**
 * Common response
 */
data class CommonResponse(
    val data: Data,
)

data class Data(
    val advertSearch: AdvertSearch?
)

data class AdvertSearch(
    val url: String,
    val totalCount: Int,
    val pageInfo: PageInfo,
    val alternativeLinks: List<AlternativeLinksBlock>,
    val edges: List<AdvertEdge>
)

data class PageInfo(
    val pageSize: Int,
    val currentOffset: Int
)

data class AdvertEdge(
    val node: AdvertNode
)

data class AdvertNode(
    val id: String,
    val title: String,
    val createdAt: String,
    val shortDescription: String,
    val url: String,
    val price: Price,
    val parameters: List<Parameter>
)

data class Price(
    val amount: Amount,
    val grossPrice: Any?,
    val netPrice: Any?
)

data class Amount(
    val units: Int,
    val nanos: Int,
    val value: String,
    val currencyCode: String
)

data class Parameter(
    val key: String,
    val displayValue: String,
    val label: String,
    val value: String
)

data class AlternativeLinksBlock(
    val name: String,
    val title: String,
    val links: List<Link>
)

data class Link(
    val title: String,
    val url: String,
    val counter: Int
)
