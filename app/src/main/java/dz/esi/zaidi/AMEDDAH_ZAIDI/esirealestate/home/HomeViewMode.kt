package dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.data.RealEstatePost
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.data.RealEstatePostsRepository

class HomeViewMode(application: Application) : AndroidViewModel(application) {
    private var realEstatePostsRepository = RealEstatePostsRepository(application)
    var posts : LiveData<List<RealEstatePost>>

    init {
        realEstatePostsRepository.fetchPosts()
        posts = realEstatePostsRepository.posts
    }
}