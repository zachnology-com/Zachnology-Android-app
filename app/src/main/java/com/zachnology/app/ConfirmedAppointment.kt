package com.zachnology.app

import java.util.Calendar
import java.util.Date

class ConfirmedAppointment {
    var appointmentId: String? = null
    var category: String? = null
    var description: String? = null
    var contactMethod: String? = null
    var dateRequestedLong: Long? = null
    var dateRequested: Calendar? = null
    var appointmentDateLong: Long? = null
    var appointmentDate: Calendar? = null
    var includeTime: Boolean? = null
    var representativeNotes: String? = null

    constructor(
        appointmentId: String,
        category: String,
        description: String,
        contactMethod: String,
        dateRequested: Long,
        appointmentDate: Long,
        includeTime: Boolean,
        representativeNotes: String
    ) {
        this.appointmentId = appointmentId
        this.category = category
        this.description = description
        this.contactMethod = contactMethod
        this.dateRequestedLong = dateRequested
        this.dateRequested = Calendar.getInstance()
        this.dateRequested!!.timeInMillis = dateRequested
        this.appointmentDateLong = appointmentDate
        this.appointmentDate = Calendar.getInstance()
        this.appointmentDate!!.timeInMillis = appointmentDate
        this.includeTime = includeTime
        this.representativeNotes = representativeNotes

    }

}