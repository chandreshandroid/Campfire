package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.model

data class Address(val longName: String, val shortName: String, val type: ArrayList<String>)
data class Place(
    val id: String,
    val description: String
) {
    override fun toString(): String {
        return ""
    }
}
data class PlaceDetails(
    val id: String,
    val name: String,
    val address: ArrayList<Address>,
    val lat: Double,
    val lng: Double,
    val placeId: String,
    val url: String,
    val utcOffset: Int,
    val vicinity: String

)
//val compoundPlusCode: String,
//val globalPlusCode: String