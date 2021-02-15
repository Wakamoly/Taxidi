package com.lucidsoftworksllc.taxidi.utils

import android.content.Context
import android.graphics.*
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.lucidsoftworksllc.taxidi.R
import kotlin.math.abs
import kotlin.math.atan
import kotlin.random.Random


object MapUtils {

    private const val TAG = "MapUtils"

    fun getTruckBitmap(context: Context): Bitmap {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_light_duty)
        return Bitmap.createScaledBitmap(bitmap, 100, 100, false)
    }

    fun getBusinessBitmap(context: Context): Bitmap {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_warehouse_large)
        return Bitmap.createScaledBitmap(bitmap, 100, 100, false)
    }

    fun getDestinationBitmap(): Bitmap {
        val height = 20
        val width = 20
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

    fun getRotation(start: LatLng, end: LatLng): Float {
        val latDifference: Double = abs(start.latitude - end.latitude)
        val lngDifference: Double = abs(start.longitude - end.longitude)
        var rotation = -1F
        when {
            start.latitude < end.latitude && start.longitude < end.longitude -> {
                rotation = Math.toDegrees(atan(lngDifference / latDifference)).toFloat()
            }
            start.latitude >= end.latitude && start.longitude < end.longitude -> {
                rotation = (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 90).toFloat()
            }
            start.latitude >= end.latitude && start.longitude >= end.longitude -> {
                rotation = (Math.toDegrees(atan(lngDifference / latDifference)) + 180).toFloat()
            }
            start.latitude < end.latitude && start.longitude >= end.longitude -> {
                rotation =
                    (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 270).toFloat()
            }
        }
        Log.d(TAG, "getRotation: $rotation")
        return rotation
    }

    fun getRandomCompanyName() : String {
        var returnString = ""
        val first = (1..6).random()
        val second = (1..6).random()
        val third = (1..6).random()

        when (first) {
            1 -> {
                returnString += "XYZ "
            }
            2 -> {
                returnString += "ZYX "
            }
            3 -> {
                returnString += "123 "
            }
            4 -> {
                returnString += "Burmington "
            }
            5 -> {
                returnString += "Sesame "
            }
            6 -> {
                returnString += "ABC "
            }
        }

        when (second) {
            1 -> {
                returnString += "Albatross "
            }
            2 -> {
                returnString += "Alpha "
            }
            3 -> {
                returnString += "Omega "
            }
            4 -> {
                returnString += "Ultra "
            }
            5 -> {
                returnString += "Limited "
            }
            6 -> {
                returnString += "Fish "
            }
        }

        when (third) {
            1 -> {
                returnString += "Incorporated"
            }
            2 -> {
                returnString += "Inc."
            }
            3 -> {
                returnString += "Limited"
            }
            4 -> {
                returnString += "LLC"
            }
            5 -> {
                returnString += "Arts"
            }
            6 -> {
                returnString += "USA"
            }
        }
        return returnString
    }

    fun getRandomCompanyID() : Long {
        return (100..869012891).random().toLong()
    }

    fun getRandomCompanyImage() : String {
        var returnString = "assets/images/taxidi/company_profile_images/"
        when ((1..8).random()) {
            1 -> {
                returnString += "erskine_murray_warehouse.jpg"
            }
            2 -> {
                returnString += "LSCR-warehouse-space-for-lease.jpg"
            }
            3 -> {
                returnString += "Warehouse-Business.jpeg"
            }
            4 -> {
                returnString += "warehouse-fulfillment-solutions-small-business-1.jpg"
            }
            5 -> {
                returnString += "ic_warehouse_large.png"
            }
            6 -> {
                returnString += "warehouse-photo.jpg"
            }
            7 -> {
                returnString += "warehouse-anywhere-inventory-management-for-small-business-2.jpg"
            }
            8 -> {
                returnString += "Small-business-meeting-in-warehouse_3x2.jpg"
            }
        }
        return returnString
    }

    fun getRandomLoadImage() : String {
        var returnString = "assets/images/taxidi/company_load_images/"
        when ((1..8).random()) {
            1 -> {
                returnString += "Loading-Cargo-Plate.jpg"
            }
            2 -> {
                returnString += "american-freight-furniture-office.jpg"
            }
            3 -> {
                returnString += "Pre-galvanized-square-hollow-section-Q235-Welded.png_350x350.png"
            }
            4 -> {
                returnString += "HTB1GxhwazzuK1Rjy0Fpq6yEpFXaC.jpg"
            }
            5 -> {
                returnString += "HTB1XjptQSzqK1RjSZPcq6zTepXar.jpg"
            }
            6 -> {
                returnString += "scaffold-package-480x266.jpg"
            }
            7 -> {
                returnString += "R47f90dffdfed9cf7d6eb87e96e3768d0.jfif"
            }
            8 -> {
                returnString += "R8a682794fedb9bbcdbb279ec6e864ad7.jfif"
            }
        }
        return returnString
    }

    fun getRandomLoadType() : Int {
        // 1 explosive
        // 2 flammable gas
        // 3 non-flam non-tox gas
        // 4 toxic gas
        // 5 flammable liquid
        // 6 flammable solid
        // 7 spontaneously combustible
        // 8 dangerous when wet
        // 9 oxidizing agent
        // 10 organic peroxide
        // 11 toxic
        // 12 infectious substance
        // 13 radioactive
        // 14 corrosive
        // 15 misc. dangerous goods
        // 16-35 non-hazardous
        return (1..35).random()
    }

    fun getRandomLoadWeight() : Double {
        val newDouble = (1000..5000).random() + Random.nextDouble(0.00, 0.99)
        return "%.2f".format(newDouble).toDouble()
    }

    fun getRandomLoadPay() : Double {
        // Pay per mile
        // TODO: 0.50 is the minimum?
        val pay = Random.nextDouble(1.00, 5.00)
        return "%.2f".format(pay).toDouble()
    }

    fun getRandomTrailerTypeNeeded() : Int {
        // 1 flatbed
        // 2 step deck
        // 3 reefer
        // 4 auto carrier
        // 5 dump trailer
        // 6 tanker
        // 7 LTL Dry van
        // 8 Partial Dry van
        // 9-15 dry van
        return (1..15).random()
    }

    fun getRandomToLatLng(currentLocation: LatLng) : LatLng {
        val randomOperatorForLat = (0..1).random()
        val randomOperatorForLng = (0..1).random()
        var randomDeltaForLat = (10..50).random() / (1..15).random()
        var randomDeltaForLng = (10..50).random() / (1..15).random()
        if (randomOperatorForLat == 1) {
            randomDeltaForLat *= -1
        }
        if (randomOperatorForLng == 1) {
            randomDeltaForLng *= -1
        }
        val randomLatitude = (currentLocation.latitude + randomDeltaForLat).coerceAtMost(360.00)
        val randomLongitude = (currentLocation.longitude + randomDeltaForLng).coerceAtMost(360.00)
        return LatLng(randomLatitude, randomLongitude)
    }

    fun getDistanceFromBothLatLngs(
        start: LatLng,
        end: LatLng
    ) : String {
        val loc1 = Location(LocationManager.GPS_PROVIDER)
        val loc2 = Location(LocationManager.GPS_PROVIDER)

        loc1.latitude = start.latitude
        loc1.longitude = start.longitude

        loc2.latitude = end.latitude
        loc2.longitude = end.longitude

        val distance = "%.2f".format(((loc1.distanceTo(loc2)) * 0.00062137)).toDouble()
        return "$distance miles"
    }


}