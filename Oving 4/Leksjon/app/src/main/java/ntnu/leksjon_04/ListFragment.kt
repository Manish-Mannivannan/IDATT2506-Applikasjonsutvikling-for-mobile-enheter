package ntnu.leksjon_04

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListFragment : Fragment() {

    interface OnTitleClickListener {
        fun onTitleClicked(index: Int)
    }

    private var listener: OnTitleClickListener? = null
    private lateinit var titles: List<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnTitleClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        titles = requireArguments().getStringArrayList(ARG_TITLES)?.toList().orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_list, container, false)
        val rv = root.findViewById<RecyclerView>(R.id.rvTitles)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = TitleAdapter(titles) { pos -> listener?.onTitleClicked(pos) }
        return root
    }

    class TitleAdapter(
        private val items: List<String>,
        private val onClick: (Int) -> Unit
    ) : RecyclerView.Adapter<TitleAdapter.VH>() {

        class VH(val v: View) : RecyclerView.ViewHolder(v) {
            val tv: TextView = v.findViewById(R.id.tvTitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_title, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.tv.text = items[position]
            holder.itemView.setOnClickListener { onClick(position) }
        }

        override fun getItemCount(): Int = items.size
    }

    companion object {
        private const val ARG_TITLES = "titles"
        fun newInstance(titles: List<String>) = ListFragment().apply {
            arguments = Bundle().apply { putStringArrayList(ARG_TITLES, ArrayList(titles)) }
        }
    }
}
