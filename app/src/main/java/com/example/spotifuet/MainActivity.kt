package com.example.spotifuet

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import SongAdapter
import android.graphics.Color
import android.widget.TextView
import songs
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying = false

    // Declare the playButton variable here
    private lateinit var playButton: Button
    private lateinit var songTitle: TextView
    private lateinit var artistName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayer()

        val btnSongs = findViewById<Button>(R.id.songs)
        val btnPlaylists = findViewById<Button>(R.id.artists)

        // Mặc định nút Songs sẽ sáng từ đầu
        btnSongs.setBackgroundColor(Color.parseColor("#6750A4"))
        btnPlaylists.setBackgroundColor(Color.GRAY)

        btnSongs.setOnClickListener {
            it.setBackgroundColor(Color.parseColor("#6750A4"))
            btnPlaylists.setBackgroundColor(Color.GRAY)
        }

        btnPlaylists.setOnClickListener {
            it.setBackgroundColor(Color.parseColor("#6750A4"))
            btnSongs.setBackgroundColor(Color.GRAY)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.song_list)

        // Set the layout manager for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        songTitle = findViewById(R.id.song_title)
        artistName = findViewById(R.id.artist_name)

        // Pass the songs array to the SongAdapter constructor
        recyclerView.adapter = SongAdapter(songs) { song ->
            playSong(song.id)
            updateNowPlayingBar(song)
        }

        // Initialize the playButton variable here
        playButton = findViewById(R.id.play_button)

        playButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.pause()
                playButton.text = "\u25B6" // Unicode character for "Play"
            } else {
                mediaPlayer.start()
                playButton.text = "\u23F8" // Unicode character for "Pause"
            }
            isPlaying = !isPlaying
        }
    }

    private fun playSong(songId: Int) {
        mediaPlayer.reset()
        val songResource = resources.getIdentifier("song_$songId", "raw", packageName)
        val songUri = Uri.parse("android.resource://$packageName/$songResource")

        // Try to set the data source and catch the IOException if not found
        try {
            mediaPlayer.setDataSource(this, songUri)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            Toast.makeText(this, "File not found: $songUri", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateNowPlayingBar(song: Song) {
        songTitle.text = song.title
        artistName.text = song.artist
        playButton.text = "\u23F8" // Unicode character for "Pause"
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
