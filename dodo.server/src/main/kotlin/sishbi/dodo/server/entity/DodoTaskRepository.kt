package sishbi.dodo.server.entity

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Task Repository.
 */
@Repository
interface DodoTaskRepository : CrudRepository<DodoTask, Long>
