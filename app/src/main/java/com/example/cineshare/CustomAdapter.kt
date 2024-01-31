package com.example.cineshare

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import java.util.Random

class CustomAdapter(private val context: Context, private val items: List<ListItem>, private val username: String) : BaseAdapter() {

    private val channelId = "my_channel_id"

    init {
        createNotificationChannel()
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = convertView ?: inflater.inflate(R.layout.list_item, parent, false)

        val item = getItem(position) as ListItem
        val textView1: TextView = view.findViewById(R.id.textView1)
        val textView2: TextView = view.findViewById(R.id.textView2)
        val button: Button = view.findViewById(R.id.button)


        textView1.text = item.name
        textView2.text = item.directorName

        button.setOnClickListener {

            if(item.avaiable.toInt() > 0) {
                if(isYouTubeLink(item.youtubeLink)) {
                    val intent = Intent(context, WatchFilm::class.java)
                    intent.putExtra("Username", username)
                    intent.putExtra("FilmText", item.name)
                    intent.putExtra("YoutubeLink", item.youtubeLink)
                    val generatedCode = generateRandomCode(6)
                    sendNotification(
                        "Ciesz się oglądaniem!",
                        "Twój kod: $generatedCode. Sprawdź film: ${item.name}"
                    )
                    intent.putExtra("Code", generatedCode)
                    context.startActivity(intent)
                }else{
                    showErrorDialog("Link prowadzący do filmu jest nie poprawny, postaramy się jak najszybciej wprowadzić poprawny","Błędny link do filmu")
                }

            }else{
                showErrorDialog("Przepraszamy aktualnie nie ma wolnych platform do oglądania, spróbuj za kilka minut", "Brak wolnych platform")
            }
        }

        return view
    }

    private fun showErrorDialog(message: String, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = "My Channel"
            val descriptionText = "Opis mojego kanału"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun sendNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.tv)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(Random().nextInt(), builder.build())
        }
    }

    private fun generateRandomCode(length: Int): String {
        val alphanumericChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length)
            .map { alphanumericChars.random() }
            .joinToString("")
    }

    private fun isYouTubeLink(link: String): Boolean {
        val youtubeRegex = ("^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+$").toRegex()
        return link.matches(youtubeRegex)
    }
}