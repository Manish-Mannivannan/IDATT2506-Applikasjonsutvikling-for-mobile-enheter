package ntnu.leksjon_04

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_detail, container, false)
        val title = requireArguments().getString(ARG_TITLE).orEmpty()
        val desc = requireArguments().getString(ARG_DESC).orEmpty()
        val poster = requireArguments().getInt(ARG_POSTER)

        v.findViewById<TextView>(R.id.tvHeading).text = title
        v.findViewById<TextView>(R.id.tvDescription).text = desc
        v.findViewById<ImageView>(R.id.imgPoster).setImageResource(poster)
        return v
    }

    companion object {
        private const val ARG_TITLE = "t"
        private const val ARG_DESC = "d"
        private const val ARG_POSTER = "p"

        fun newInstance(title: String, desc: String, posterRes: Int) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_DESC, desc)
                    putInt(ARG_POSTER, posterRes)
                }
            }
    }
}
