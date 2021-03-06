package dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.post_details.contacts.mail_senders

import androidx.lifecycle.LiveData
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.excptions.NotConnectedException

interface IMailSender {
    @Throws(NotConnectedException :: class)
    fun sendEmails(emails : List<String>, message : String)
    fun getSendingState(): LiveData<Boolean>
}