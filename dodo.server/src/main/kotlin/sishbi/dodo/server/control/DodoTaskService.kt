package sishbi.dodo.server.control

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sishbi.dodo.server.entity.DodoTask
import sishbi.dodo.server.entity.DodoTaskRepository

/**
 * Task Service.
 */
@Service
@Transactional
class DodoTaskService(private val repository: DodoTaskRepository) {
  /** get all tasks. */
  fun all(): Iterable<DodoTask> = repository.findAll()

  /** get a single task. */
  fun single(id: Long) = repository.findById(id)

  /** create/update a task. */
  fun save(task: DodoTask) = repository.save(task)

  /** delete a task if found, return true if deleted. */
  fun delete(id: Long): Boolean {
    val found = repository.existsById(id)
    if (found) {
      repository.deleteById(id)
    }
    return found
  }
}
