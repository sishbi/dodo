package sishbi.dodo.server

import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import sishbi.dodo.server.boundary.DodoRestController
import sishbi.dodo.server.entity.DodoTask

/** define a static logger. */
private val LOG = KotlinLogging.logger {}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests(@LocalServerPort var port: Int) {
	/** inject the RestController. */
	@Autowired
	lateinit var cut : DodoRestController
	/** inject the Test Rest template - to interact with REST endpoint. */
	@Autowired
	lateinit var rest: TestRestTemplate

	@Test
	fun contextLoads() {
		assertNotNull(cut)
	}

	@Test
	fun testRest() {
		val url = "http://localhost:$port/tasks"
		// GET all
		val res1 = rest.getForEntity(url, Array<DodoTask>::class.java)
		assertEquals(HttpStatus.OK, res1.statusCode)
		val tasks1 = res1.body
		assertNotNull(tasks1)
		assertEquals(0, tasks1?.size)
		// POST
		val create = DodoTask("first task", "This is the first task")
		val res2 = rest.postForEntity(url, HttpEntity(create), String::class.java)
		assertEquals(HttpStatus.OK, res2.statusCode)
		val created = res2.body
		assertNotNull(created)
		LOG.debug {"created=$created"}
		val id = 1

		// GET all
		val res3 = rest.getForEntity(url, Array<DodoTask>::class.java)
		assertEquals(HttpStatus.OK, res3.statusCode)
		val tasks2 = res3.body
		assertNotNull(tasks2)
		assertEquals(1, tasks2?.size)
		tasks2?.forEach {
			LOG.debug {"task: $it"}
		}

		// GET single - found
		val res4 = rest.getForEntity("$url/$id", DodoTask::class.java)
		assertEquals(HttpStatus.OK, res4.statusCode)
		val got = res4.body
		LOG.debug { "got: $got" }
		assertEquals("first task", got?.name)

		// GET single - not found
		val res5 = rest.getForEntity("$url/2", DodoTask::class.java)
		assertEquals(HttpStatus.NOT_FOUND, res5.statusCode)
		val notFound = res5.body
		LOG.debug { "got: $notFound" }
		assertEquals(null, notFound?.name)

		rest.delete("$url/$id")
		// GET all
		val res6 = rest.getForEntity(url, Array<DodoTask>::class.java)
		assertEquals(HttpStatus.OK, res6.statusCode)
		val tasks3 = res6.body
		assertNotNull(tasks3)
		assertEquals(0, tasks3?.size)

		// POST
		val recreate = DodoTask("second task", "This is the second task")
		val res7 = rest.postForEntity(url, HttpEntity(recreate), String::class.java)
		assertEquals(HttpStatus.OK, res7.statusCode)
		val recreated = res7.body
		assertNotNull(recreated)
		LOG.debug {"recreated=$recreated"}

		// PUT
		recreate.id = 2
		recreate.description = "Updated second task"
		rest.put(url, HttpEntity(recreate))
		val res8 = rest.getForEntity("$url/2", DodoTask::class.java)
		assertEquals(HttpStatus.OK, res8.statusCode)
		val updated = res8.body
		assertNotNull(updated)
		LOG.debug {"updated=$updated"}

		// GET all
		val res9 = rest.getForEntity(url, Array<DodoTask>::class.java)
		assertEquals(HttpStatus.OK, res9.statusCode)
		val tasks4 = res9.body
		assertNotNull(tasks4)
		assertEquals(1, tasks4?.size)
		tasks4?.forEach {
			LOG.debug {"task: $it"}
		}

		// PUT - not found
		updated?.let {
			it.id = 3
			rest.put(url, HttpEntity(it))
		}

		// DELETE - not found
		rest.delete("$url/3")
	}

}
