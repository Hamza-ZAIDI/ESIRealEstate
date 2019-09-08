package dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.data_sources


import android.content.Context
import android.util.Log
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.data.Localisation
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.data.RealEstatePost
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.data.RealEstatePoster
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.subscription_service.WilayaSubscriber
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class AlgerieAnnonceWebsite : RealEstateWebSite, NewPostsNotificationProvider {

    companion object {
        private const val baseUrl = "http://www.annonce-algerie.com/"
        private const val TAG = "AAW"

    }

    private fun getDocument(url: String): Document {
        val connection = Jsoup
            .connect(url)
            .execute()
            .charset("ISO-8859-15")

        val page = Jsoup.parse(connection.body())

        return page
    }

    private fun getPostFromPge(info: Element): RealEstatePost {

        val categoryRange = 1
        val typeRage = 2
        val linkRange = 3
        val priceRage = 4

        val values = info.select("td:nth-child(2n)")
        val category = values[categoryRange].text()
        val type = values[typeRage].text()
        val link = baseUrl + values[linkRange].select("a").attr("href")
        val price = values[priceRage].text()

        val insidePostPage = getDocument(link)

        // Retrieve poster

        var phone = insidePostPage.select("li.cellphone span.da_contact_value").text()
        if (phone == "") {
            phone = insidePostPage.select("li.phone span.da_contact_value").text()
        }
        Log.d(TAG + "/phone", phone)

        //Retrieve localisation

        val labels = insidePostPage.select("td.da_label_field")
        val labelValues = insidePostPage.select("td.da_field_text")
        var i = 0
        var town = ""
        var city = ""
        var address = ""
        var surface = ""
        var text = ""
        for (label in labels) {

            if (label.text() == "Localisation") {
                val loc = labelValues[i].select("a")
                town = loc[2].text()
                city = loc[3].text()
                Log.d(TAG + "/loc", "$town / $city")
            }

            if (label.text() == "Adresse") {
                address = labelValues[i].text()
                Log.d(TAG + "/addr", address)
            }

            if (label.text() == "Surface") {
                surface = labelValues[i].text()
                Log.d(TAG + "/surf", surface)
            }

            if (label.text() == "Texte") {
                text = labelValues[i].text()
                Log.d(TAG + "/text", text)
            }

            i++
        }

        //Retrieve pictures
        val pictures = insidePostPage.select("img.PhotoMin1")
        val picturesURLs = ArrayList<String>()
        for (picture in pictures) {
            picturesURLs.add(baseUrl + picture.attr("src"))
        }

        val localisation = Localisation(town, city)
        localisation.adresse = address

        val poster = RealEstatePoster(null, phone)

        val post = RealEstatePost(text, localisation, poster, picturesURLs, link)
        post.category = category
        post.type = type
        post.price = price
        post.surface = surface

        return post

    }

    override fun getRealEstatePosts(consumer: RealEstatePostsConsumer) {
        doAsync {
            val page = getDocument(baseUrl + "/AnnoncesImmobilier.asp?rech_order_by=11")
            val infos = page.select(".Tableau1")

            for (info in infos) {
                val post = getPostFromPge(info)
                 consumer.addPost(post)
            }

        }

    }

    override fun getNewPosts(consumer: NotificationConsumer, context: Context) {
        val newPosts = ArrayList<RealEstatePost>()
        val wilayaSubscriber = WilayaSubscriber(context)
//        val wilayaNames = wilayaSubscriber.wilayas.value!!
        doAsync{
            val page = getDocument(baseUrl)
            val wilayaLinks = page.select("a[href*=cod_reg]")


            val filteredLinks = wilayaLinks.filter {
                it.text().replace("\\([0-9]+\\)".toRegex(),"").trim() in wilayaSubscriber.subscribedWilayas.map { it.wilayaName }
            }

            for (link in filteredLinks) {
                val wilayaName = link.text().replace("\\([0-9]+\\)".toRegex(),"").trim()
                Log.d(TAG, "getNewPosts : you are subscribed to $wilayaName")

                val value = baseUrl + link.attr("href") + "&rech_order_by=11"
                Log.d(TAG ,"getNewPosts : wilayaLink : $value")
                val wilayaPage = getDocument(value)
                val lastPosted = baseUrl + wilayaPage.select("a[href*=cod_ann]").first().attr("href")
                Log.d(TAG,"getNewPosts : lastPosted $wilayaName : $lastPosted")
                val indexOfWilaya =
                    wilayaSubscriber.subscribedWilayas.indexOfFirst { it.wilayaName == wilayaName }
                Log.d(TAG , "getNewPosts : indexOfWilaya $wilayaName : $indexOfWilaya")
                if (wilayaSubscriber.subscribedWilayas[indexOfWilaya].lastLink == null) {
                    wilayaSubscriber.subscribedWilayas[indexOfWilaya].lastLink = lastPosted
                    Log.d(TAG, "$lastPosted saved to $wilayaName")
                    wilayaSubscriber.saveChanges()
                } else if (wilayaSubscriber.subscribedWilayas[indexOfWilaya].lastLink != lastPosted) {
                    wilayaSubscriber.subscribedWilayas[indexOfWilaya].lastLink = lastPosted
                    wilayaSubscriber.saveChanges()
                    val lastPostInfos = wilayaPage.select(".Tableau1").first()
                    val post = getPostFromPge(lastPostInfos)
                    Log.d(TAG,"getNewPosts : should show a notification for  $wilayaName ${post}")
                    uiThread {
                        consumer.makeNotification(post)
                    }

                }else{
                    Log.d(TAG, "getNewPosts : No new posts available for $wilayaName")
                }
            }
        }
    }
}