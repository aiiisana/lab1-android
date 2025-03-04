package com.example.lab1.music

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.example.lab1.R
import java.io.IOException

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "MusicServiceChannel"
    private val NOTIFICATION_ID = 1

    private val tracks = listOf("music1.mp3", "music2.mp3", "music3.mp3")
    private var currentTrackIndex = 0

    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> startMusic()
            ACTION_PAUSE -> pauseMusic()
            ACTION_STOP -> stopMusic()
            ACTION_NEXT -> nextTrack()
            ACTION_PREVIOUS -> previousTrack()
        }
        return START_STICKY
    }

    private fun startMusic() {
        Log.d("MusicService", "Attempting to start music")

        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            return
        }

        if (mediaPlayer != null) {
            mediaPlayer?.start()
            startForeground(NOTIFICATION_ID, buildNotification(true))
            return
        }

        mediaPlayer = MediaPlayer().apply {
            try {
                val afd = assets.openFd(tracks[currentTrackIndex])
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                start()
                setOnCompletionListener { nextTrack() }
            } catch (e: IOException) {
                Log.e("MusicService", "Error loading music file", e)
            }
        }

        startForeground(NOTIFICATION_ID, buildNotification(true))
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        startForeground(NOTIFICATION_ID, buildNotification(false))
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(true)
        stopSelf()
    }

    private fun nextTrack() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        currentTrackIndex = (currentTrackIndex + 1) % tracks.size
        startMusic()
    }

    private fun previousTrack() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else tracks.size - 1
        startMusic()
    }

    private fun buildNotification(isPlaying: Boolean): Notification {
        val playIntent = Intent(this, MusicService::class.java).apply { action = ACTION_PLAY }
        val pauseIntent = Intent(this, MusicService::class.java).apply { action = ACTION_PAUSE }
        val stopIntent = Intent(this, MusicService::class.java).apply { action = ACTION_STOP }
        val nextIntent = Intent(this, MusicService::class.java).apply { action = ACTION_NEXT }
        val prevIntent = Intent(this, MusicService::class.java).apply { action = ACTION_PREVIOUS }

        val playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)
        val pausePendingIntent = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE)
        val stopPendingIntent = PendingIntent.getService(this, 2, stopIntent, PendingIntent.FLAG_IMMUTABLE)
        val nextPendingIntent = PendingIntent.getService(this, 3, nextIntent, PendingIntent.FLAG_IMMUTABLE)
        val prevPendingIntent = PendingIntent.getService(this, 4, prevIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, "MUSIC_CHANNEL")
            .setContentTitle("Music Player")
            .setContentText("Playing: ${tracks[currentTrackIndex]}")
            .setSmallIcon(R.drawable.ic_music)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setStyle(MediaStyle().setShowActionsInCompactView(0, 1, 2))
            .addAction(R.drawable.ic_previous, "Previous", prevPendingIntent)
            .addAction(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play, "Play/Pause", if (isPlaying) pausePendingIntent else playPendingIntent)
            .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "MUSIC_CHANNEL",
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}