package ntnu.leksjon_03

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FriendsActivity : AppCompatActivity() {

    private val vm: FriendsViewModel by viewModels()
    private lateinit var adapter: FriendAdapter
    private lateinit var rv: RecyclerView
    private lateinit var tvEmpty: TextView

    private var editingIndex: Int? = null

    private val editFriendLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode != RESULT_OK || res.data == null) return@registerForActivityResult

        val name = res.data!!.getStringExtra(EditFriendActivity.EXTRA_NAME) ?: return@registerForActivityResult
        val y = res.data!!.getIntExtra(EditFriendActivity.EXTRA_YEAR, 2000)
        val m = res.data!!.getIntExtra(EditFriendActivity.EXTRA_MONTH, 1)
        val d = res.data!!.getIntExtra(EditFriendActivity.EXTRA_DAY, 1)
        val friend = Friend(name, y, m, d)

        val index = editingIndex
        if (index == null) vm.add(friend) else vm.update(index, friend)
        editingIndex = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        rv = findViewById(R.id.rvFriends)
        tvEmpty = findViewById(R.id.tvEmpty)
        val btnAdd: Button = findViewById(R.id.btnAdd)

        adapter = FriendAdapter(emptyList()) { position ->
            editingIndex = position
            val f = vm.friends.value!![position]
            val i = Intent(this, EditFriendActivity::class.java)
                .putExtra(EditFriendActivity.EXTRA_NAME, f.name)
                .putExtra(EditFriendActivity.EXTRA_YEAR, f.year)
                .putExtra(EditFriendActivity.EXTRA_MONTH, f.month)
                .putExtra(EditFriendActivity.EXTRA_DAY, f.day)
            editFriendLauncher.launch(i)
        }

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        btnAdd.setOnClickListener {
            editingIndex = null
            editFriendLauncher.launch(Intent(this, EditFriendActivity::class.java))
        }

        vm.friends.observe(this) { list ->
            adapter.submit(list)
            tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
