package com.darekbx.carscrap.repository.remote.scrap.update
import com.google.gson.annotations.SerializedName

data class ApiRequest(
    val after: Any? = null,
    val experiments: List<Experiment>? = listOf(
        Experiment(key = "MCTA-1463", variant = "a"),
        Experiment(key = "CARS-64661", variant = "b")
    ),
    var filters: List<Filter>? = listOf(
        Filter(name = "filter_enum_make", value = "mazda"),
        Filter(name = "filter_enum_model", value = "6"),
        Filter(name = "filter_enum_generation", value = "gen-iii-2012-6"),
        Filter(name = "filter_enum_fuel_type", value = "petrol"),
        Filter(name = "category_id", value = "29")
    ),
    val includeCepik: Boolean? = true,
    val includeFiltersCounters: Boolean? = false,
    val includeNewPromotedAds: Boolean? = false,
    val includePriceEvaluation: Boolean? = true,
    val includePromotedAds: Boolean? = false,
    val includeRatings: Boolean? = false,
    val includeSortOptions: Boolean? = false,
    val includeSuggestedFilters: Boolean? = false,
    val maxAge: Int? = 60,
    var page: Int? = 1,
    val parameters: List<String>? = listOf(
        "make", "offer_type", "show_pir", "fuel_type", "gearbox",
        "country_origin", "mileage", "engine_capacity", "engine_code",
        "engine_power", "model", "version", "year"
    ),
    val promotedInput: PromotedInput? = PromotedInput(),
    val searchTerms: Any? = null
)

data class Experiment(
    val key: String? = null,
    val variant: String? = null
)

data class Filter(
    val name: String? = null,
    val value: String? = null
)

data class PromotedInput(
    val _placeholder: Unit? = null
)

data class Extensions(
    val persistedQuery: PersistedQuery? = null
)

data class PersistedQuery(
    @SerializedName("sha256Hash")
    val sha256Hash: String? = null,
    val version: Int? = null
)