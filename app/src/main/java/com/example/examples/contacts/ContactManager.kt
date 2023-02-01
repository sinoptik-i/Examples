package com.example.examples.contacts

import android.content.Context
import android.provider.ContactsContract
import android.util.Log


class ContactManager(val context: Context) {

    //   @SuppressLint("Range")
    fun getContacts(): StringBuffer {
        val CONTENT_URI = ContactsContract.Contacts.CONTENT_URI
        val _ID = ContactsContract.Contacts._ID
        val DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        val HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        val PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID
        val NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER


        val output = StringBuffer()
        val contentResolver = context.contentResolver
        val cursor = contentResolver
            .query(CONTENT_URI, null, null, null, null)
        try {
            if (cursor?.count!! > 0) {
                while (cursor.moveToNext()) {
                    val columnIndexID = cursor.getColumnIndex(_ID)
                    val columnIndexDISPLAY_NAME = cursor.getColumnIndex(DISPLAY_NAME)
                    val columnIndexHAS_PHONE_NUMBER = cursor.getColumnIndex(HAS_PHONE_NUMBER)
                    if (columnIndexID >= 0 &&
                        columnIndexDISPLAY_NAME >= 0 &&
                        columnIndexHAS_PHONE_NUMBER >= 0
                    ) {
                        val contact_id = cursor.getString(columnIndexID)
                        val name = cursor.getString(columnIndexDISPLAY_NAME);
                        val hasPhoneNumber = Integer.parseInt(
                            cursor.getString(columnIndexHAS_PHONE_NUMBER)
                        )
                        if (hasPhoneNumber > 0) {
                            output.append("\n Имя: " + name);

                            val phoneCursor = contentResolver.query(
                                PhoneCONTENT_URI, null,
                                "$Phone_CONTACT_ID = ?", arrayOf(contact_id), null
                            )
                            if (phoneCursor != null) {
                                while (phoneCursor.moveToNext()) {
                                    val  columnIndexNUMBER=phoneCursor.getColumnIndex(NUMBER)
                                    val phoneNumber =
                                        phoneCursor.getString(columnIndexNUMBER);
                                    output.append("\n Телефон: " + phoneNumber);
                                }
                                phoneCursor.close()
                            }
                        }
                        output.append("\n");
                    }
                    }
                }
            }
         catch (exception: Exception) {
             Log.e("ContactManager",exception.message.toString())
        }
        cursor?.close()
        return output
    }

}