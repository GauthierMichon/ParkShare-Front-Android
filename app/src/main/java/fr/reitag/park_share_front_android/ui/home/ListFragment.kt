package fr.reitag.park_share_front_android.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.reitag.park_share_front_android.R
import fr.reitag.park_share_front_android.models.Ad

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_AD = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var ads: List<Ad> = mutableListOf<Ad>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ads = it.getParcelableArrayList<Ad>(ARG_AD)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerViewAd = view.findViewById(R.id.recyclerViewAd) as RecyclerView
        /*val textViewPortions = view.findViewById(R.id.textViewPortions) as TextView
        val buttonMoins = view.findViewById(R.id.buttonMoins) as Button
        val buttonPlus = view.findViewById(R.id.buttonPlus) as Button

        textViewPortions.text = String.format("%d portions", recipe.portion)*/

        val buttonOrder = view.findViewById(R.id.buttonOrder) as Button
        val iconButtonOrder = view.findViewById(R.id.iconButtonOrder) as ImageButton


        recyclerViewAd.layoutManager = GridLayoutManager(requireContext(), 2)

        val adListAdapter = AdListAdapter(requireContext(), ads)

        recyclerViewAd.adapter = adListAdapter




    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IngredientsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(ads: List<Ad>) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_AD, ArrayList(ads))
                }
            }
    }
}