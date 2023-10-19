package com.example.spotifuet

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SongControlActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying = false

    private lateinit var playButton : Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.song_control)

        playButton = findViewById(R.id.play_button)

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)

        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val songTitle = intent.getStringExtra("songTitle")
        val artistName = intent.getStringExtra("artistName")

        val songTitleTextView = findViewById<TextView>(R.id.song_title)
        val artistNameTextView = findViewById<TextView>(R.id.artist_name)

        songTitleTextView.text = songTitle
        artistNameTextView.text = artistName

        mediaPlayer = MediaPlayerSingleton.mediaPlayer!!
        isPlaying = sharedPreferences.getBoolean("isPlaying", false)

        print(isPlaying)

        playButton.text = if (isPlaying) "\u23F8" else "\u25B6"

        playButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            if (isPlaying) {
                mediaPlayer.pause()
                playButton.text = "\u25B6" // Unicode character for "Play"
                editor.putBoolean("isPlaying", false)
            } else {
                mediaPlayer.start()
                playButton.text = "\u23F8" // Unicode character for "Pause"
                editor.putBoolean("isPlaying", true)
            }
            editor.apply()
            isPlaying = !isPlaying
        }
    }

    override fun onResume() {
        super.onResume()
        isPlaying = sharedPreferences.getBoolean("isPlaying", false)
        playButton.text = if (isPlaying) "\u23F8" else "\u25B6"
    }
}

