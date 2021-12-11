package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Environment
import android.util.Log
import androidx.annotation.NonNull
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by dhavalkaka on 07/11/2016.
 */
class ImageSaver(private val context: Context) {
    private var directoryName = "Campfire"
    private var fileName = "image.png"
    private var external = false
    fun setFileName(fileName: String): ImageSaver {
        this.fileName = fileName
        return this
    }

    fun setExternal(external: Boolean): ImageSaver {
        this.external = external
        return this
    }

    fun setDirectoryName(directoryName: String): ImageSaver {
        this.directoryName = directoryName
        return this
    }

    fun check_file_exists(fileName: String?): Boolean {
        var exits = false
        val pictureFile = File(getAlbumStorageDir(directoryName), fileName)
        if (pictureFile.exists()) {
            exits = true
        }
        return exits
    }

    fun save(bitmapImage: Bitmap): File {
        var fileOutputStream: FileOutputStream? = null
        val file = createFile()
        try {
            fileOutputStream = FileOutputStream(file)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            //   MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    @NonNull
    fun createFile(): File {
        val directory: File
        directory = if (external) {
            getAlbumStorageDir(directoryName)
        } else {
            context.getDir(directoryName, Context.MODE_PRIVATE)
        }
        Log.e("ImageSaver", "filename:$fileName")
        return File(directory, fileName)
    }

    fun getAlbumStorageDir(albumName: String?): File {
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file.mkdirs()) {
            Log.e("ImageSaver", "Directory not created")
            file.mkdirs()
        }
        return file
    }

    fun load(): Bitmap? {
        var inputStream: FileInputStream? = null
        try {
            inputStream = FileInputStream(createFile())
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun deleteFolder() {
        var pictureFile: File? = null
        pictureFile = getAlbumStorageDir(directoryName)
        if (pictureFile.isDirectory && pictureFile != null) {
            val children = pictureFile.list()
            if (children != null) {
                for (i in children.indices) {
                    File(pictureFile, children[i]).delete()
                }
            }
        }
    }

    companion object {
        var audioFileName = "recording.mp4"
        var tempaudioFileName = "temprecording"
        val isExternalStorageWritable: Boolean
            get() {
                val state = Environment.getExternalStorageState()
                return Environment.MEDIA_MOUNTED == state
            }

        val isExternalStorageReadable: Boolean
            get() {
                val state = Environment.getExternalStorageState()
                return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
            }

        fun drawableToBitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }
    }

}