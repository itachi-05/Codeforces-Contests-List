package com.hoMS.hms.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.hoMS.hms.R
import com.hoMS.hms.databinding.FragmentHomeBinding
import com.hoMS.hms.ui.ShimmerScreen
import kotlinx.coroutines.delay

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var sharedPref: SharedPreferences
    private var binding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        sharedPref = requireActivity().getSharedPreferences(
            "sharingLoginDataUsingSP#02",
            AppCompatActivity.MODE_PRIVATE
        )
        val currentUser = sharedPref.getString(
            "userLoggedInNumber",
            "NA"
        )
        if (currentUser != "NA") {
            binding!!.greetUserTxt.text = "Hello!\n$currentUser"
        }
        return binding!!.root
    }
//    private var checkLoading = true
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val viewGroup = FrameLayout(requireContext())
//
//        val composeView = ComposeView(requireContext())
//
//        composeView.setContent {
//            var isLoading by remember { mutableStateOf(true) }
//
//            LaunchedEffect(key1 = true) {
//                delay(2000)
//                isLoading = false
//            }
//
//            if (isLoading && checkLoading) {
//                // Show a loading Composable function
//                LazyColumn(modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxHeight(0.8f)
//                    .padding(16.dp)) {
//                    items(20) {
//                        ShimmerScreen(
//                            isLoading = isLoading,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp)
//                        )
//                    }
//                }
//                checkLoading = false
//            } else {
//                // Inflate the XML layout for the Fragment
//                val fragmentView = inflater.inflate(R.layout.fragment_home, container, false)
//                viewGroup.addView(fragmentView)
//            }
//        }
//
//        viewGroup.addView(composeView)
//
//        return viewGroup
//    }
}
