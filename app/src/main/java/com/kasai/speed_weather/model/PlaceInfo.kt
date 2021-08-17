package com.kasai.speed_weather.model

data class PlaceInfo(
    val candidates: List<Candidate>,
    val status: String
) {
    data class Candidate(
        val business_status: String,
        val formatted_address: String,
        val geometry: Geometry,
        val name: String,
        val place_id: String,
        val plus_code: PlusCode
    ) {
        data class Geometry(
            val location: Location,
            val viewport: Viewport
        ) {
            data class Location(
                val lat: Double,
                val lng: Double
            )

            data class Viewport(
                val northeast: Northeast,
                val southwest: Southwest
            ) {
                data class Northeast(
                    val lat: Double,
                    val lng: Double
                )

                data class Southwest(
                    val lat: Double,
                    val lng: Double
                )
            }
        }

        data class PlusCode(
            val compound_code: String,
            val global_code: String
        )
    }
}