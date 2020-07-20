package sishbi.dodo.server.boundary

import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sishbi.dodo.server.control.DodoTaskService
import sishbi.dodo.server.entity.DodoTask

/** define a static logger. */
private val LOG = KotlinLogging.logger {}

/**
 * REST controller for /tasks endpoint.
 */
@RestController
@RequestMapping("/tasks")
class DodoRestController(val service: DodoTaskService) {
  /** GET all tasks .*/
  @GetMapping
  fun getTasks() = ResponseEntity.ok(service.all())

  /** GET single task. */
  @GetMapping("/{id}")
  fun getTask(@PathVariable id: Long) = service.single(id).let {
      if (it.isPresent) {
        val got = it.get()
        LOG.debug {"got: $got"}
        ResponseEntity.ok(got)
      } else {
        LOG.debug {"get - not-found: id=$id"}
        ResponseEntity.notFound().build()
      }
  }

  /** POST task. */
  @PostMapping
  fun createTask(@RequestBody task : DodoTask) : ResponseEntity<String> {
    LOG.debug {"create: $task"}
    val saved = service.save(task)
    LOG.debug {"created: id=${saved.id}"}
    return ResponseEntity.ok("created: ${saved.id}")
  }

  /** PUT task. */
  @PutMapping
  fun updateTask(@RequestBody task: DodoTask) = task.id?.let {
    val found = service.single(it)
    if (found.isPresent) {
      LOG.debug {"updating: id=${task.id}"}
      service.save(task)
      ResponseEntity.ok("updated")
    } else {
      LOG.debug {"update - not-found: id=${task.id}"}
      ResponseEntity.notFound().build()
    }
  }

  /** DELETE task. */
  @DeleteMapping("/{id}")
  fun deleteTask(@PathVariable id: Long) = if (service.delete(id)) {
    LOG.debug {"deleted: id=$id"}
    ResponseEntity.ok("deleted")
  } else {
    LOG.debug {"delete - not-found: id=$id"}
    ResponseEntity.notFound().build()
  }
}
