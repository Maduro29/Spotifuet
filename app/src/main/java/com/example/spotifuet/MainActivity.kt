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
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.view.View
import android.widget.TextView
import songs
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying = false

    private var currentSongIndex = 0

    // Declare the playButton variable here
    private lateinit var playButton: Button
    private lateinit var songTitle: TextView
    private lateinit var artistName: TextView

    private lateinit var nowPlayingBar: View

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (MediaPlayerSingleton.mediaPlayer == null) {
            MediaPlayerSingleton.mediaPlayer = MediaPlayer()
        }

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)

        mediaPlayer = MediaPlayerSingleton.mediaPlayer!!

        val btnSongs = findViewById<Button>(R.id.songs)
        val btnNotification = findViewById<Button>(R.id.notification)

        // Mặc định nút Songs sẽ sáng từ đầu
        btnSongs.setBackgroundColor(Color.parseColor("#6750A4"))
        btnNotification.setBackgroundColor(Color.GRAY)

        btnSongs.setOnClickListener {
            it.setBackgroundColor(Color.parseColor("#6750A4"))
            btnNotification.setBackgroundColor(Color.GRAY)
        }

        btnNotification.setOnClickListener {
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

        // Next song
        val nextButton = findViewById<Button>(R.id.next_button)
        nextButton.setOnClickListener {
            // Tăng chỉ mục lên 1
            currentSongIndex = (currentSongIndex + 1) % songs.size

            // Update next song
            playSong(currentSongIndex)

            // Update nowPlayingBar
            updateNowPlayingBarNextSong(currentSongIndex)
        }

        nowPlayingBar = findViewById<View>(R.id.now_playing_bar)
        nowPlayingBar.setOnClickListener {
            val intent = Intent(this, SongControlActivity::class.java)
            intent.putExtra("songTitle", songTitle.text.toString())
            intent.putExtra("artistName", artistName.text.toString())
            startActivity(intent)
        }

        nowPlayingBar.visibility = View.GONE
    }

    private fun playSong(songId: Int) {
        // Cập nhật currentSongIndex
        currentSongIndex = songId

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

    private fun updateNowPlayingBarNextSong(songId: Int) {
        // Tìm bài hát trong danh sách songs dựa trên song_id
        val song = songs.firstOrNull { it.id == songId }

        // Nếu tìm thấy bài hát, cập nhật nowplayingbar
        if (song != null) {
            songTitle.text = song.title
            artistName.text = song.artist
        }
    }

    private fun updateNowPlayingBar(song: Song) {
        nowPlayingBar.visibility = View.VISIBLE
        songTitle.text = song.title
        artistName.text = song.artist
        playButton.text = "\u23F8" // Unicode character for "Pause"
    }

    override fun onResume() {
        super.onResume()
        isPlaying = sharedPreferences.getBoolean("isPlaying", false)
        playButton.text = if (isPlaying) "\u23F8" else "\u25B6"
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
