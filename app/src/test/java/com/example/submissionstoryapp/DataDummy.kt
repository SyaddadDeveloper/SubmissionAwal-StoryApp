package com.example.submissionstoryapp

import com.example.submissionstoryapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "photoUrl + $i",
                "name $i",
                "description $i",
                "id $i"
            )
            items.add(story)
        }
        return items
    }
}