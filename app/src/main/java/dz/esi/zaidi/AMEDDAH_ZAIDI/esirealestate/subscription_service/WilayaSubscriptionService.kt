package dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.subscription_service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.EsiRealEstate
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.R
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.data.RealEstatePost
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.data_sources.AlgerieAnnonceWebsite
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.data_sources.NewPostsNotificationProvider
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.data_sources.RealEstatePostsConsumer
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.post_details.PostDetailsActivity

class WilayaSubscriptionService : JobIntentService(), RealEstatePostsConsumer {

    private val notificationProviders = arrayOf<NewPostsNotificationProvider>(
        AlgerieAnnonceWebsite()
    )

    companion object{
        private const val FETCH_NEW_POSTS_JOB_ID = 100
        private const val TAG = "SubscriptionService"
        private var notificationId = 1


        fun enqueueWork(context : Context, intent: Intent){
            enqueueWork(context,WilayaSubscriptionService::class.java, FETCH_NEW_POSTS_JOB_ID,intent)
        }
    }


    override fun addPosts(newPosts: List<RealEstatePost>) {
        Log.d(TAG,"addPosts")

        for (post in newPosts){
            createNotification(post)
        }
    }

    private fun createNotification(post: RealEstatePost){

        val intent = PostDetailsActivity.createIntent(applicationContext, post)
        val actionIntent = PendingIntent.getActivity(
            applicationContext,
            PostDetailsActivity.SHOW_POST_DETAILS_CODE,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val notification = NotificationCompat.Builder(this, EsiRealEstate.WILAYA_SUBSCRIPTION_CHANNEL)
            .setContentTitle(getString(R.string.new_post_notification_title))
            .setContentText(getString(R.string.new_post_notification_description,post.wilaya))
            .setSmallIcon(R.mipmap.ic_launcher)
            .addAction(R.mipmap.ic_launcher,getString(R.string.view_details),actionIntent)
            .build()

        with(NotificationManagerCompat.from(applicationContext)){
            notify(notificationId,notification)
        }
    }





    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "$TAG created")
    }
    override fun onHandleWork(intent: Intent) {

        Log.d(TAG, "Fetching new posts for wilaya ")

        for (provider in notificationProviders){
            provider.getNewPosts(this,applicationContext)
        }
    }

    override fun onStopCurrentWork(): Boolean {
        Log.d(TAG , "$TAG stopped")
        return super.onStopCurrentWork()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "$TAG destroyed")
    }
}