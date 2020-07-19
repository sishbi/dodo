package sishbi.dodo.server.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class DodoTask(var name: String = "",
               var description: String = "",
               @Id @GeneratedValue var id: Long? = null) {
  override fun toString() = "name=$name, description=$description, id=$id"
}
