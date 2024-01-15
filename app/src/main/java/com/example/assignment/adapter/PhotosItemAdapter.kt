package com.example.assignment.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment.R
import com.example.assignment.databinding.PhotosListBinding
import com.example.assignment.model.PhotosModelItem
import java.io.File


class PhotosItemAdapter(private var context : Context) :
    PagingDataAdapter<PhotosModelItem, PhotosItemAdapter.PhotosViewHolder>(COMPARATOR) {

    inner class PhotosViewHolder(var photosListBinding: PhotosListBinding) :
        RecyclerView.ViewHolder(photosListBinding.root)

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.photosListBinding.photosItemModel = getItem(position)
        Glide.with(holder.itemView)
            .load(getItem(position)?.downloadUrl.toString())
            .placeholder(R.drawable.ic_launcher_background) // Optional: placeholder image
            .into(holder.photosListBinding.imv)
        holder.photosListBinding.constraintLayout.setOnClickListener {
            Toast.makeText(context,getItem(position)?.author.toString(),Toast.LENGTH_LONG).show()
        }
        holder.photosListBinding.imvDownload.setOnClickListener {
            downloadImage(getItem(position)?.downloadUrl.toString())
        }
    }

    @SuppressLint("Range")
    private fun downloadImage(url: String) {
         var msg: String? = ""
         var lastMsg = ""
        val directory = File(Environment.DIRECTORY_DOWNLOADS)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)
        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("Image-"+url.substring(url.lastIndexOf("/") + 1))
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    directory.toString(),
                    url.substring(url.lastIndexOf("/") + 1)
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread(Runnable {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                msg = statusMessage(url, directory, status)
                if (msg != lastMsg) {
                    (context as Activity).runOnUiThread {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                    lastMsg = msg ?: ""
                }
                cursor.close()
            }
        }).start()
    }

    private fun statusMessage(url: String, directory: File, status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully in $directory" + File.separator + url.substring(
                url.lastIndexOf("/") + 1
            )
            else -> "There's nothing to download"
        }
        return msg
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val notificationListBinding = DataBindingUtil.inflate<PhotosListBinding>(
            inflater,
            R.layout.photos_list, parent, false
        )
        return PhotosViewHolder(notificationListBinding)
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<PhotosModelItem>() {
            override fun areItemsTheSame(oldItem: PhotosModelItem, newItem: PhotosModelItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PhotosModelItem, newItem: PhotosModelItem): Boolean {
                return oldItem == newItem
            }
        }
    }


}