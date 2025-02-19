// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class NoteAndSchedule (
    @Embedded val note: Note,
    @Relation(
        parentColumn = "noteId",
        entityColumn = "ownerId"
    )
    val schedule: Schedule?
)
