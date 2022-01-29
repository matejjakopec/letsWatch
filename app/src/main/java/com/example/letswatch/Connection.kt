package com.example.letswatch

data class Connection(
    var firstUid: String? = null,
    var secondUid: String? = null,
    var firstWatchedIds: MutableList<String>? = null,
    var secondWatchedIds: MutableList<String>? = null,
    var matchesIds: MutableList<String>? = null,
    var deniedIds: MutableList<String>? = null
)
