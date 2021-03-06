package dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.subscription_service

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.R
import dz.esi.zaidi.AMEDDAH_ZAIDI.esirealestate.home.PostsAdapter
import kotlinx.android.synthetic.main.wilaya_subscription_fragment.view.*

class WilayaSubscriptionFragment : Fragment(){
    private lateinit var wilayaSubscriptionViewModel: WilayaSubscriptionViewModel
    private lateinit var adapter : WilayaSubscriptionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.wilaya_subscription_fragment,container,false)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        wilayaSubscriptionViewModel = ViewModelProviders.of(this).get(WilayaSubscriptionViewModel::class.java)

        view?.rv_wilayas?.layoutManager = LinearLayoutManager(context)
        view?.rv_wilayas?.setHasFixedSize(true)

        adapter = WilayaSubscriptionAdapter(listOf(*wilayaSubscriptionViewModel.subscribedWilayas.value!!.toTypedArray()))
        adapter.onSubscriptionListener = onSubscribeListener
//        adapter.subscribedWilayasProvider = subscribedWilayasProvider
        val wilayas = resources.getStringArray(R.array.wilayas_array)
        view?.rv_wilayas?.adapter = adapter
        adapter.submitList(wilayas.toList())

        wilayaSubscriptionViewModel.subscribedWilayas.observe(this, Observer {
            adapter.subscribeWilayas = listOf(*it.toTypedArray())
        })
        super.onActivityCreated(savedInstanceState)
    }

    private val onSubscribeListener = object : WilayaSubscriptionAdapter.OnSubscriptionListener{
        override fun onChecked(wilaya : String) {
            wilayaSubscriptionViewModel.subscribeToWilaya(wilaya )
            Log.d("onSubscribtionFragment",wilayaSubscriptionViewModel.subscribedWilayas.toString())
            Toast.makeText(context, context?.getString(R.string.subscribe_toast,wilaya), Toast.LENGTH_SHORT).show()
        }

        override fun onUnchecked(wilaya : String) {
            wilayaSubscriptionViewModel.unSubScribeFromWilaya(wilaya)
            Toast.makeText(context, context?.getString(R.string.unsubscribe_toast,wilaya), Toast.LENGTH_SHORT).show()
        }

    }

    private val subscribedWilayasProvider = object : WilayaSubscriptionAdapter.SubscribedWilayasProvider{
        override fun getCurrentSubscribedWilayas(): List<String> {
            return wilayaSubscriptionViewModel.getCurrentSubscribedWilayas()
        }

    }
}