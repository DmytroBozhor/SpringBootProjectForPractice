package com.example.demowithtests;

import com.example.demowithtests.domain.Address;
import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.domain.Gender;
import com.example.demowithtests.repository.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Employee Repository Tests")
public class RepositoryTests {

    @Autowired
    private EmployeeRepository employeeRepository;
    private Employee employee;

    @BeforeEach
    public void setUp() {
        employee = Employee.builder()
                .name("Mark")
                .country("England")
                .addresses(new HashSet<>(Set.of(
                        Address
                                .builder()
                                .country("UK")
                                .build())))
                .gender(Gender.M)
                .build();
    }

    @Test
    @Order(1)
    @Rollback(value = false)
    @DisplayName("Save employee test")
    public void saveEmployeeTest() {

        var savedEmployee = employeeRepository.save(employee);

        Assertions.assertThat(savedEmployee.getId()).isEqualTo(1);
        Assertions.assertThat(savedEmployee.getName()).isEqualTo(employee.getName());
        Assertions.assertThat(savedEmployee.getCountry()).isEqualTo(employee.getCountry());
        Assertions.assertThat(savedEmployee.getGender()).isEqualTo(employee.getGender());
    }

    @Test
    @Order(2)
    @DisplayName("Get employee by id test")
    public void getEmployeeTest() {

        var retrievedEmployee = employeeRepository.findById(1).orElseThrow();

        Assertions.assertThat(retrievedEmployee.getId()).isEqualTo(1);
        Assertions.assertThat(retrievedEmployee.getName()).isEqualTo(employee.getName());
        Assertions.assertThat(retrievedEmployee.getCountry()).isEqualTo(employee.getCountry());
        Assertions.assertThat(retrievedEmployee.getGender()).isEqualTo(employee.getGender());
    }

    @Test
    @Order(3)
    @DisplayName("Get employees test")
    public void getListOfEmployeeTest() {

        var employeesList = employeeRepository.findAll();

        Assertions.assertThat(employeesList.size()).isGreaterThan(0);

    }

    @Test
    @Order(4)
    @Rollback(value = false)
    @DisplayName("Update employee test")
    public void updateEmployeeTest() {

        var employeeRetrieved = employeeRepository.findById(1).orElseThrow();

        employeeRetrieved.setName("Martin");
        var employeeUpdated = employeeRepository.save(employeeRetrieved);

        Assertions.assertThat(employeeUpdated.getId()).isEqualTo(employeeRetrieved.getId());
        Assertions.assertThat(employeeUpdated.getName()).isEqualTo(employeeRetrieved.getName());
        Assertions.assertThat(employeeUpdated.getCountry()).isEqualTo(employeeRetrieved.getCountry());
        Assertions.assertThat(employeeUpdated.getGender()).isEqualTo(employeeRetrieved.getGender());
    }

    @Test
    @Order(5)
    @DisplayName("Find employee by gender test")
    public void findByGenderTest() {

        var employees = employeeRepository.findByGender(employee.getGender().toString(), employee.getCountry());

        for (Employee e : employees) {
            assertThat(e.getGender()).isEqualTo(employee.getGender());
            assertThat(e.getCountry()).isEqualTo(employee.getCountry());
        }
    }

    @Test
    @Order(6)
    @DisplayName("Get employee by name starting with test")
    public void findEmployeeByNameStartingWithTest() {

        var employees = employeeRepository.findByNameStartingWith("Mar");

        Assertions.assertThat(employees.size()).isGreaterThan(0);
        for (Employee e : employees) {
            Assertions.assertThat(e.getName()).contains("Mar");
        }
    }

    @Test
    @Order(7)
    @DisplayName("Get employee by name ending with test")
    public void findEmployeeByNameEndingWithTest() {

        var employees = employeeRepository.findByNameEndingWith("tin");

        Assertions.assertThat(employees.size()).isGreaterThan(0);
        for (Employee e : employees) {
            Assertions.assertThat(e.getName()).contains("tin");
        }
    }

    @Test
    @Order(8)
    @Rollback(value = false)
    @DisplayName("Delete employee test")
    public void deleteEmployeeTest() {

        var employee = employeeRepository.findById(1).orElseThrow();

        employeeRepository.delete(employee);

        Employee employeeNull = null;

        var optionalEmployee = Optional.ofNullable(employeeRepository.findByName("Martin"));

        if (optionalEmployee.isPresent()) {
            employeeNull = optionalEmployee.orElseThrow();
        }

        Assertions.assertThat(employeeNull).isNull();
    }

}
