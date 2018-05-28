package com.cypir.healthrelay

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cypir.healthrelay.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_summary.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SummaryFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SummaryFragment : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var vm : MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        vm = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        text_status.text = vm.status
    }
}
