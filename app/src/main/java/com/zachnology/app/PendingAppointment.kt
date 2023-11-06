package com.zachnology.app

import java.util.Calendar
import java.util.Date

class PendingAppointment {
    var appointmentId: String? = null
    var category: String? = null
    var description: String? = null
    var contactMethod: String? = null
    var dateRequestedLong: Long? = null
    var dateRequested: Calendar? = null

    constructor(appointmentId:String, category:String, description:String, contactMethod:String, dateRequested:Long) {
        this.appointmentId = appointmentId
        this.category = category
        this.description = description
        this.contactMethod = contactMethod
        this.dateRequestedLong = dateRequested
        this.dateRequested = Calendar.getInstance()
        this.dateRequested!!.timeInMillis = dateRequested

    }

}