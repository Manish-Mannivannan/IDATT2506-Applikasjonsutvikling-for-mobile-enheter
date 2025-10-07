package ntnu.leksjon_03

data class Friend(
    var name: String,
    var year: Int,
    var month: Int, // 1..12 (we'll store 1-based for simplicity)
    var day: Int
)
