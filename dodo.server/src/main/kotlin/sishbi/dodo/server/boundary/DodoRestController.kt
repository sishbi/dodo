package sishbi.dodo.server.boundary

import mu.KotlinLogging
import org.springframework.http.HttpStatus
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
  fun getTask(@PathVariable id: Long): ResponseEntity<DodoTask?> {
    LOG.debug {"get: $id"}
    val task = service.single(id)
    return if (task.isPresent) {
      val got = task.get()
      LOG.debug {"got: $got"}
      ResponseEntity.ok(got)
    } else {
      LOG.debug {"not-found: $id"}
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
    }
  }

  /** POST task. */
  @PostMapping
  fun createTask(@RequestBody task : DodoTask) : ResponseEntity<String> {
    LOG.debug {"create: $task"}
    val saved = service.save(task)
    LOG.debug {"created: id=${saved.id}"}
    return ResponseEntity.ok("created : ${saved.id}")
  }

  /** PUT task. */
  @PutMapping
  fun updateTask(@RequestBody task: DodoTask) = ResponseEntity.ok(service.save(task))

  /** DELETE task. */
  @DeleteMapping("/{id}")
  fun deleteTask(@PathVariable id: Long) = if (service.delete(id)) {
    ResponseEntity.ok()
  } else {
    ResponseEntity.notFound()
  }
}
