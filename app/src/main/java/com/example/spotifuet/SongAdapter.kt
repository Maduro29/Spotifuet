// SongAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spotifuet.R
import com.example.spotifuet.Song

class SongAdapter(private val songs: Array<Song>, private val onSongClick: (Song) -> Unit) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_item, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.view.findViewById<TextView>(R.id.song_id).text = song.id.toString()
        holder.view.findViewById<TextView>(R.id.song_title).text = song.title
        holder.view.findViewById<TextView>(R.id.artist_name).text = song.artist
        holder.view.findViewById<TextView>(R.id.song_duration).text = song.duration

        holder.view.setOnClickListener {
            onSongClick(song)
        }
    }

    override fun getItemCount() = songs.size
}
