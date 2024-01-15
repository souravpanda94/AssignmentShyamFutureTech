package com.example.assignment

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment.adapter.PhotosItemAdapter
import com.example.assignment.api.ApiService
import com.example.assignment.databinding.ActivityMainBinding
import com.example.assignment.viewmodel.PhotosViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.Manifest
import androidx.appcompat.app.AlertDialog

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var activityMainBindig : ActivityMainBinding
    @Inject
    lateinit var apiService: ApiService
    private lateinit var photosViewModel: PhotosViewModel
    private lateinit var adapter : PhotosItemAdapter
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBindig = DataBindingUtil.setContentView(this,R.layout.activity_main)
        askPermissions()
        CoroutineScope(Dispatchers.Main).launch {
            getItems()
        }
        activityMainBindig.swipeRefreshLayout.setOnRefreshListener {
            activityMainBindig.swipeRefreshLayout.isRefreshing = false
            CoroutineScope(Dispatchers.Main).launch {
                getItems()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun askPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("Permission required to save photos from the Web.")
                    .setPositiveButton("Allow") { dialog, id ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            0
                        )
                        finish()
                    }
                    .setNegativeButton("Deny") { dialog, id -> dialog.cancel() }
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0
                )
                // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.

            }
        }
    }

    private suspend fun getItems(){
        photosViewModel = ViewModelProvider(this)[PhotosViewModel::class.java]
        adapter = PhotosItemAdapter(this@MainActivity)
        photosViewModel.getPhotosItems().collect{
            val llm = LinearLayoutManager(this@MainActivity)
            llm.orientation = LinearLayoutManager.VERTICAL
            activityMainBindig.photosRCV.layoutManager = llm
            activityMainBindig.photosRCV.adapter = adapter
            adapter.submitData(lifecycle,it)
        }
    }
}

