package com.rttc.shopmanager.utilities

import com.rttc.shopmanager.database.Entry
import com.rttc.shopmanager.ui.ModifyFragment
import java.util.*
import kotlin.random.Random

class SelfTesting {
    companion object {
        private fun getRandomName(): String {
            val firstNames = arrayOf(
                "Aayush", "Sanjay", "Rahul", "Aakash", "Yogita",
                "Jayesh", "Arpit", "Ruhi", "Tanmay", "Ajay"
            )
            val lastNames = arrayOf(
                "Jain", "Kumar", "Singh", "Modi", "Sharma",
                "Gupta", "Agarwal", "Banerjee", "Bhatt", "Kapoor"
            )
            return "${firstNames[Random.nextInt(until = firstNames.size)]} ${lastNames[Random.nextInt(until = lastNames.size)]}"
        }

        private fun getRandomContact(): String {
            val contacts = arrayOf(
                "8871787751", "9896225415", "8965565889", "7412478475", "7878000885",
                "8871001251", "7076225415", "8965658899", "8070258475", "7098000402"
            )
            return contacts[Random.nextInt(until = contacts.size)]
        }

        private fun getRandomEmail(): String {
            val email = arrayOf(
                "genericemail1997@gmail.com", "you.tube@yahoo.in", "info.google@google.com", "contact@rttc.com", "binatonewifi@india.com",
                "info@hpservice.com", "nippo4554@ever.net", "shopmanager2020@gmail.com", "single@mingle.com", "twitter@fb.com"
            )
            return email[Random.nextInt(until = email.size)]
        }

        private fun getRandomAddress(): String {
            val address = arrayOf(
                "M35, Sree Pally, Purba Putiary, Kolkata",
                "18/sb, The Emperor Bldg, Fatehgunj Main Road, Fatehgunj Main Road, Vadodara",
                "Shop No 6, Vishwadeep Apt, Vishwadeep Mahavir Nagar, Kandivali (west), Mumbai",
                "18/sb, The Emperor Bldg, Fatehgunj Main Road, Fatehgunj Main Road, Delhi",
                "2, A Wing, Rajdarshan, Dadapatil Wadi, Thane H.o. (east), Mumbai"
            )
            return address[Random.nextInt(until = address.size)]
        }

        private fun getRandomType(): String {
            val types = arrayOf(
                "Modular Kitchen",
                "Chimney",
                "Gas Stove",
                "Repairs and Services",
                "Other"
            )
            return types[Random.nextInt(until = types.size)]
        }

        fun getRandomEntry(): Entry {
            val entry = Entry()
            entry.apply {
                name = getRandomName()
                dateOpened = Calendar.getInstance().time
                primaryContact = getRandomContact()
                if (Random.nextInt(0,10) == 5)
                    secondaryContact = getRandomContact()
                whatsAppContact =
                    if (Random.nextInt(0,5) == 2)
                        ModifyFragment.WHATSAPP_PRIM
                    else
                        ModifyFragment.WHATSAPP_NONE
                if (Random.nextInt(0,5) == 3)
                    email = getRandomEmail()
                address = getRandomAddress()
                //enquiryType = getRandomType()
                if (Random.nextInt(0,5) == 1) {
                    status = ModifyFragment.STATUS_CLOSED
                    dateClosed = dateOpened
                }
                else {
                    status = ModifyFragment.STATUS_OPEN
                }
            }
            return entry
        }
    }
}