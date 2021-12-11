package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util

import androidx.exifinterface.media.ExifInterface


class geoDegree internal constructor(exif: ExifInterface, onGetValue : onValueReturn) {
    var isValid = false
        private set
    internal var Latitude: Float? = null
    internal var Longitude: Float? = null

    val latitudeE6: Int
        get() = (Latitude!! * 1000000).toInt()

    val longitudeE6: Int
        get() = (Longitude!! * 1000000).toInt()

    init {
        val attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
        val attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
        val attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
        val attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)

        if (attrLATITUDE != null
            && attrLATITUDE_REF != null
            && attrLONGITUDE != null
            && attrLONGITUDE_REF != null
        ) {
            isValid = true

            if (attrLATITUDE_REF == "N") {
                Latitude = convertToDegree(attrLATITUDE)
            } else {
                Latitude = 0 - convertToDegree(attrLATITUDE)!!
            }

            if (attrLONGITUDE_REF == "E") {
                Longitude = convertToDegree(attrLONGITUDE)
            } else {
                Longitude = 0 - convertToDegree(attrLONGITUDE)!!
            }
        }

        if (Latitude != null && Longitude != null){
            onGetValue.returnValue(Latitude!!, Longitude!!)
        }
    }

    private fun convertToDegree(stringDMS: String): Float? {
        var result: Float? = null
        val DMS = stringDMS.split(",".toRegex(), 3).toTypedArray()

        val stringD = DMS[0].split("/".toRegex(), 2).toTypedArray()
        var D0 = java.lang.Double.parseDouble(stringD[0])
        var D1 = java.lang.Double.parseDouble(stringD[1])
        val FloatD = D0 / D1

        val stringM = DMS[1].split("/".toRegex(), 2).toTypedArray()
        var M0 = java.lang.Double.parseDouble(stringM[0])
        var M1 = java.lang.Double.parseDouble(stringM[1])
        val FloatM = M0 / M1

        val stringS = DMS[2].split("/".toRegex(), 2).toTypedArray()
        var S0 = java.lang.Double.parseDouble(stringS[0])
        var S1 = java.lang.Double.parseDouble(stringS[1])
        val FloatS = S0 / S1

        result = (FloatD + FloatM / 60 + FloatS / 3600).toFloat()

        return result
    }

    override fun toString(): String {
        // TODO Auto-generated method stub
        return ("pring:= "+Latitude.toString()
                + ", "
                + Longitude.toString())
    }

    interface onValueReturn{
        fun returnValue(late : Float, long : Float)
    }
}