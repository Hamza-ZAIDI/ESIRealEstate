package dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.post_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.R

class PostDetails : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        val pictures = intent.getStringArrayListExtra(ImageSliderFragment.PICTURES)
        val imageSliderFragment = ImageSliderFragment.newInstance(pictures)

        supportFragmentManager.
            beginTransaction().
            replace(R.id.fragment_image_slider,imageSliderFragment).
            commit()

        val descriptionFragment = DescriptionFragment.newInstance(
            intent.getStringExtra(DescriptionFragment.WILAYA)!!,
            intent.getStringExtra(DescriptionFragment.COMMUNE)!!,
            intent.getStringExtra(DescriptionFragment.ADDRESS)!!,
            intent.getStringExtra(DescriptionFragment.CATEGORY)!!,
            intent.getStringExtra(DescriptionFragment.TYPE)!!,
            intent.getStringExtra(DescriptionFragment.PRICE)!!,
            intent.getStringExtra(DescriptionFragment.SURFACE)!!,
            intent.getStringExtra(DescriptionFragment.DESCRIPTION)!!
        )

        supportFragmentManager.
            beginTransaction().
            replace(R.id.fragment_post_description_container,descriptionFragment).
            commit()

    }
}
