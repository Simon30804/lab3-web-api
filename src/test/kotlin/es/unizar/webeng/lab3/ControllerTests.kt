package es.unizar.webeng.lab3

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.util.Optional

private val MANAGER_REQUEST_BODY = { name: String ->
    """
    { 
        "role": "Manager", 
        "name": "$name" 
    }
    """
}

private val MANAGER_RESPONSE_BODY = { name: String, id: Int ->
    """
    { 
       "name" : "$name",
       "role" : "Manager",
       "id" : $id
    }
    """
}

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ControllerTests {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var employeeRepository: EmployeeRepository

    @Test
    fun `POST is not safe and not idempotent`() {
        // SETUP - COMPLETE ME!
        // Hint: POST is not idempotent - each call creates a new resource.
        // Think about what the controller does when saving an employee.
        // Consider how to mock the repository to return different results for multiple calls.
        // TODO("Complete the mock setup for POST test")

        // Dado que POST no es idempotente, cada llamada crea un nuevo recurso con un ID distinto
        every {
            employeeRepository.save(any<Employee>())
        } answers {
            Employee("Mary", "Manager", 1)
        } andThenAnswer {
            Employee("Mary", "Manager", 2)
        }

        mvc
            .post("/employees") {
                contentType = MediaType.APPLICATION_JSON
                content = MANAGER_REQUEST_BODY("Mary")
                accept = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isCreated() }
                header { string("Location", "http://localhost/employees/1") }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(MANAGER_RESPONSE_BODY("Mary", 1))
                }
            }

        mvc
            .post("/employees") {
                contentType = MediaType.APPLICATION_JSON
                content = MANAGER_REQUEST_BODY("Mary")
                accept = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isCreated() }
                header { string("Location", "http://localhost/employees/2") }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(MANAGER_RESPONSE_BODY("Mary", 2))
                }
            }

        // VERIFY - COMPLETE ME!
        // Hint: What repository methods should be called for a POST operation?
        // What methods should NOT be called? Think about the difference between safe and unsafe operations.
        // TODO("Complete the verification for POST test")

        // Comprobamos que save ha sido llamado dos veces
        verify(exactly = 2) {
            employeeRepository.save(any<Employee>())
        }
        // Dado que el método POST no debería leer ni borrar datos,
        // nos aseguramos de que no se han llamado a los métodos findById ni deleteById
        verify(exactly = 0) {
            employeeRepository.findById(any())
            employeeRepository.deleteById(any())
            employeeRepository.findAll()
        }
    }

    @Test
    fun `GET is safe and idempotent`() {
        // SETUP
        // Dado que GET es seguro e idempotente, solo lee datos sin modificarlos.
        // Hint: GET is safe and idempotent - it only reads data without side effects.
        // Look at the test expectations to understand what scenarios you need to mock.
        // Consider both successful and unsuccessful retrieval cases.
        // TODO("Complete the mock setup for GET test")

        every {
            employeeRepository.findById(1)
        } answers {
            Optional.of(Employee("Mary", "Manager", 1))
        }
        every {
            employeeRepository.findById(2)
        } answers {
            Optional.empty()
        }

        mvc.get("/employees/1").andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                json(MANAGER_RESPONSE_BODY("Mary", 1))
            }
        }

        mvc.get("/employees/1").andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                json(MANAGER_RESPONSE_BODY("Mary", 1))
            }
        }

        mvc.get("/employees/2").andExpect {
            status { isNotFound() }
        }

        // VERIFY - COMPLETE ME!
        // Hint: Since GET is safe, what repository methods should NOT be called?
        // Count how many times each method was called based on the test requests.
        // TODO("Complete the verification for GET test")

        // Dado que GET es seguro, NO modifica el estado del sistema.
        verify(exactly = 2) {
            employeeRepository.findById(1)
        }
        verify(exactly = 1) {
            employeeRepository.findById(2)
        }
    }

    @Test
    fun `PUT is idempotent but not safe`() {
        // SETUP
        // PUT es idempotente pero no seguro, por lo que modifica el estado del sistema.
        // Hint: PUT is idempotent but not safe - it modifies state but repeated calls have the same effect.
        // Study the controller logic to understand what it does when an employee exists vs. doesn't exist.
        // Consider how to mock the repository to simulate both scenarios.
        // TODO("Complete the mock setup for PUT test")

        every {
            employeeRepository.findById(1)
        } answers {
            Optional.empty()
        } andThenAnswer {
            Optional.of(Employee("Tom", "Manager", 1))
        }

        every {
            employeeRepository.save(any<Employee>())
        } answers {
            Employee("Tom", "Manager", 1)
        }

        mvc
            .put("/employees/1") {
                contentType = MediaType.APPLICATION_JSON
                content = MANAGER_REQUEST_BODY("Tom")
                accept = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isCreated() }
                header { string("Content-Location", "http://localhost/employees/1") }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(MANAGER_RESPONSE_BODY("Tom", 1))
                }
            }

        mvc
            .put("/employees/1") {
                contentType = MediaType.APPLICATION_JSON
                content = MANAGER_REQUEST_BODY("Tom")
                accept = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                header { string("Content-Location", "http://localhost/employees/1") }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(MANAGER_RESPONSE_BODY("Tom", 1))
                }
            }

        // VERIFY - COMPLETE ME!
        // Hint: What repository methods should be called for PUT operations?
        // Think about the controller logic and how many times each method should be invoked.
        //  TODO("Complete the verification for PUT test")

        // PUT no es seguro: modifica el estado del sistema.
        verify(exactly = 2) {
            employeeRepository.save(any<Employee>())
            employeeRepository.findById(1) // PUT verifica si el recurso existe
        }
        // PUT no debe borrar datos, por lo que deleteById no debe ser llamado.
        verify(exactly = 0) {
            employeeRepository.deleteById(any())
            employeeRepository.findAll()
        }
    }

    @Test
    fun `DELETE is idempotent but not safe`() {
        // SETUP
        // Hint: DELETE is idempotent but not safe - it modifies state but repeated calls have the same effect.
        // Look at the controller implementation to see what repository method it calls.
        // Consider how to mock a method that doesn't return a value.
        // TODO("Complete the mock setup for DELETE test")

        // DELETE es idempotente pero no seguro: modifica el estado del sistema eliminando el recurso.
        justRun {
            employeeRepository.deleteById(1)
        }

        mvc.delete("/employees/1").andExpect {
            status { isNoContent() }
        }

        mvc.delete("/employees/1").andExpect {
            status { isNoContent() }
        }

        // VERIFY
        // Hint: What repository methods should be called for DELETE operations?
        // What methods should NOT be called? Think about the nature of DELETE operations.
        // TODO("Complete the verification for DELETE test")

        // DELETE es idempotente, múltiples llamadas devuelven el mismo resultado.
        // DELETE no es seguro: modifica el estado del sistema.
        verify(exactly = 2) {
            employeeRepository.deleteById(1)
        }
        verify(exactly = 0) {
            employeeRepository.findById(any())
            employeeRepository.save(any<Employee>())
            employeeRepository.findAll()
        }
    }
}
