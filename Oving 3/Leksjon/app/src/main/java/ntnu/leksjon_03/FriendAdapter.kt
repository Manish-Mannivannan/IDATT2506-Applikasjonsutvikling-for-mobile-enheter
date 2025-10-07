package ntnu.leksjon_03

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendAdapter(
    private var items: List<Friend>,
    private val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<FriendAdapter.Holder>() {

    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvBirthday: TextView = v.findViewById(R.id.tvBirthday)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val f = items[position]
        holder.tvName.text = f.name
        holder.tvBirthday.text = String.format(
            holder.itemView.context.getString(R.string.birthday_fmt),
            f.day, f.month, f.year
        )
        holder.itemView.setOnClickListener { onItemClick(position) }
    }

    override fun getItemCount(): Int = items.size

    fun submit(list: List<Friend>) {
        this.items = list
        notifyDataSetChanged()
    }
}
