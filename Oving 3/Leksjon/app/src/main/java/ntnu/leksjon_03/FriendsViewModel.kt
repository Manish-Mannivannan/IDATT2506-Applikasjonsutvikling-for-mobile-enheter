package ntnu.leksjon_03

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FriendsViewModel : ViewModel() {
    val friends = MutableLiveData<MutableList<Friend>>(mutableListOf())

    fun add(friend: Friend) {
        val list = friends.value ?: mutableListOf()
        list.add(friend)
        friends.value = list
    }

    fun update(index: Int, friend: Friend) {
        val list = friends.value ?: return
        if (index in list.indices) {
            list[index] = friend
            friends.value = list
        }
    }
}
