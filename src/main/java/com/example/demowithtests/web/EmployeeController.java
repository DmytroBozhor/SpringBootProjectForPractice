package com.example.demowithtests.web;

import com.example.demowithtests.domain.Document;
import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.domain.History;
import com.example.demowithtests.dto.*;
import com.example.demowithtests.service.EmployeeService;
import com.example.demowithtests.service.EmployeeServiceEM;
import com.example.demowithtests.service.document.DocumentService;
import com.example.demowithtests.util.mappers.DocumentMapper;
import com.example.demowithtests.util.mappers.EmployeeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.example.demowithtests.util.Endpoints.API_BASE;
import static com.example.demowithtests.util.Endpoints.USER_ENDPOINT;

@RestController
@AllArgsConstructor
@RequestMapping(API_BASE)
@Slf4j
@Tag(name = "Employee", description = "Employee API")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeServiceEM employeeServiceEM;
    private final EmployeeMapper employeeMapper;
    private final DocumentMapper documentMapper;
    private final DocumentService documentService;

    @PostMapping(USER_ENDPOINT)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "This is endpoint to add a new employee.", description = "Create request to add a new employee.", tags = {"Employee"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "CREATED. The new employee is successfully created and added to database."),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND. Specified employee request not found."),
            @ApiResponse(responseCode = "409", description = "Employee already exists")})
    public EmployeeDto saveEmployee(@RequestBody @Valid EmployeeDto requestForSave) {
        log.debug("saveEmployee() - start: requestForSave = {}", requestForSave.name());
        var employee = employeeMapper.toEmployee(requestForSave);
        var dto = employeeMapper.toEmployeeDto(employeeService.create(employee));
        log.debug("saveEmployee() - stop: dto = {}", dto.name());
        return dto;
    }

    @PostMapping("/users/jpa")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto saveEmployeeWithJpa(@RequestBody @Valid EmployeeDto requestForSave) {
        log.debug("saveEmployeeWithJpa() - start: employeeDto = {}", requestForSave);
        Employee employee = employeeServiceEM.createWithJpa(employeeMapper.toEmployee(requestForSave));
        EmployeeDto employeeDto = employeeMapper.toEmployeeDto(employee);
        log.debug("saveEmployeeWithJpa() - stop: employee = {}", employee.getId());
        return employeeDto;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeDto> getAllUsers() {
        return employeeMapper.toListEmployeeDto(employeeService.getAll());
    }

    @GetMapping("/users/pages")
    @ResponseStatus(HttpStatus.OK)
    public Page<EmployeeReadDto> getPage(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        log.debug("getPage() - start: page= {}, size = {}", page, size);
        var paging = PageRequest.of(page, size);
        var content = employeeService.getAllWithPagination(paging)
                .map(employeeMapper::toEmployeeReadDto);
        log.debug("getPage() - end: content = {}", content);
        return content;
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This is endpoint returned a employee by his id.", description = "Create request to read a employee by id", tags = {"Employee"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OK. pam pam param."),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND. Specified employee request not found."),
            @ApiResponse(responseCode = "409", description = "Employee already exists")})
    public EmployeeReadDto getEmployeeById(@PathVariable Integer id) {
        log.debug("getEmployeeById() EmployeeController - start: id = {}", id);
        var employee = employeeService.getById(id);
        log.debug("getById() EmployeeController - to dto start: id = {}", id);
        var dto = employeeMapper.toEmployeeReadDto(employee);
        log.debug("getEmployeeById() EmployeeController - end: name = {}", dto.name);
        return dto;
    }

    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeReadDto refreshEmployee(@PathVariable("id") Integer id, @RequestBody EmployeeDto employee) {
        log.debug("refreshEmployee() EmployeeController - start: id = {}", id);
        Employee entity = employeeMapper.toEmployee(employee);
        EmployeeReadDto dto = employeeMapper.toEmployeeReadDto(employeeService.updateById(id, entity));
        log.debug("refreshEmployee() EmployeeController - end: name = {}", dto.name);
        return dto;
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeEmployeeById(@PathVariable Integer id) {
        employeeService.removeById(id);
    }

    @DeleteMapping("/users")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllUsers() {
        employeeService.removeAll();
    }

    @GetMapping("/users/country")
    @ResponseStatus(HttpStatus.OK)
    public Page<EmployeeReadDto> findByCountry(@RequestParam(required = false) String country,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "3") int size,
                                               @RequestParam(defaultValue = "") List<String> sortList,
                                               @RequestParam(defaultValue = "DESC") Sort.Direction sortOrder) {
        //Pageable paging = PageRequest.of(page, size);
        //Pageable paging = PageRequest.of(page, size, Sort.by("name").ascending());
        return employeeService.findByCountryContaining(country, page, size, sortList, sortOrder.toString()).map(employeeMapper::toEmployeeReadDto);
    }

    @GetMapping("/users/c")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getAllUsersC() {
        return employeeService.getAllEmployeeCountry();
    }

    @GetMapping("/users/s")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getAllUsersSort() {
        return employeeService.getSortCountry();
    }

    @GetMapping("/users/emails")
    @ResponseStatus(HttpStatus.OK)
    public Optional<String> getAllUsersSo() {
        return employeeService.findEmails();
    }

    @GetMapping("/users/countryBy")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeDto> getByCountry(@RequestParam(required = true) String country) {
        return employeeMapper.toListEmployeeDto(employeeService.filterByCountry(country));
    }

    @PatchMapping("/users/ukrainians")
    @ResponseStatus(HttpStatus.OK)
    public Set<String> sendEmailsAllUkrainian() {
        return employeeService.sendEmailsAllUkrainian();
    }

    @GetMapping("/users/names")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeDto> findByNameContaining(@RequestParam String employeeName) {
        log.debug("findByNameContaining() EmployeeController - start: employeeName = {}", employeeName);
        List<Employee> employees = employeeService.findByNameContaining(employeeName);
        log.debug("findByNameContaining() EmployeeController - end: employees = {}", employees.size());
        return employeeMapper.toListEmployeeDto(employees);
    }

    @PatchMapping("/users/names/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void refreshEmployeeName(@PathVariable("id") Integer id, @RequestParam String employeeName) {
        log.debug("refreshEmployeeName() EmployeeController - start: id = {}", id);
        employeeService.updateEmployeeByName(employeeName, id);
        log.debug("refreshEmployeeName() EmployeeController - end: ");
    }

    @PatchMapping("/users/names/body/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Employee refreshEmployeeNameBody(@PathVariable("id") Integer id, @RequestParam String employeeName) {
        log.debug("refreshEmployeeName() EmployeeController - start: id = {}", id);
        Employee employee = employeeService.updateEmployeeByName(employeeName, id);
        log.debug("refreshEmployeeName() EmployeeController - end: id = {}", id);
        return employee;
    }

    @PostMapping("/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public String createAndSave(@RequestBody @Valid EmployeeDto employeeDto) {
        log.debug("createAndSave() EmployeeController - start: employeeDto = {}", employeeDto);
        Employee employee = employeeService.createAndSave(employeeMapper.toEmployee(employeeDto));
        log.debug("createAndSave() EmployeeController - end: employeeDto = {}", employeeDto);
        return "employee with name: " + employee.getName() + " saved!";
    }

    @GetMapping("/users/emails/find")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeEmailDto findByEmail(@RequestParam("email") String email) {
        log.debug("findByEmail() EmployeeController - start: email = {}", email);
        EmployeeEmailDto employeeEmailDto = employeeService.findByEmail(email);
        log.debug("findByEmail() EmployeeController - end: email = {}", email);
        return employeeEmailDto;
    }

    @PutMapping("/users/names/all")
    @ResponseStatus(HttpStatus.OK)
    public Integer updateAllNamesWithPut(@RequestBody String name) {
        log.debug("updateAllNamesWithPut() EmployeeController - start: name = {}", name);
        List<Employee> employees = employeeService.getAll();
        log.debug("updateAllNamesWithPut() EmployeeController - end: name = {}", name);
        return employeeService.updateAllNames(employees, name).size();
    }

    @PatchMapping("/users/names/all")
    @ResponseStatus(HttpStatus.OK)
    public Integer updateAllNamesWithPatch(@RequestBody String name) {
        log.debug("updateAllNamesWithPatch() EmployeeController - start: name = {}", name);
        List<Employee> employees = employeeService.getAll();
        log.debug("updateAllNamesWithPatch() EmployeeController - end: name = {}", name);
        return employeeService.updateAllNames(employees, name).size();
    }

    @PutMapping("/users/edit/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Employee editUserWithPut(@PathVariable("id") Integer id, @RequestBody @Valid EmployeeDto requestForUpdate) {
        log.debug("editUserWithPut() EmployeeController - start: requestForUpdate = {}", requestForUpdate);
        Employee updatedEmployee = employeeMapper.toEmployee(requestForUpdate);
        log.debug("editUserWithPut() EmployeeController - end: requestForUpdate = {}", requestForUpdate);
        return employeeService.updateOrSave(id, updatedEmployee);
    }

    @PatchMapping("/users/edit/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Employee editUserWithPatch(@PathVariable("id") Integer id, @RequestBody EmployeeUpdateDto requestForUpdate) {
        log.debug("editUserWithPatch() EmployeeController - start: requestForUpdate = {}", requestForUpdate);
        Employee updatedEmployee = employeeMapper.toEmployee(requestForUpdate);
        log.debug("editUserWithPatch() EmployeeController - end: requestForUpdate = {}", requestForUpdate);
        return employeeService.updateById(id, updatedEmployee);
    }

    @GetMapping("/users/find-by-name")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeDto> findByNameStartingWith(@RequestParam("startingChars") String startingChars) {
        log.debug("findByNameStartingWith() EmployeeController - start: startingChars = {}", startingChars);
        List<Employee> foundUsers = employeeService.findByNameStartingWith(startingChars);
        log.debug("findByNameStartingWith() EmployeeController - end: startingChars = {}", startingChars);
        return employeeMapper.toListEmployeeDto(foundUsers);
    }

    @GetMapping("/users/find/name/ending")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeDto> findByNameEndingWith(@RequestParam("endingChars") String endingChars) {
        log.debug("findByNameEndingWith() EmployeeController - start: endingChars = {}", endingChars);
        List<Employee> foundUsers = employeeService.findByNameEndingWith(endingChars);
        log.debug("findByNameEndingWith() EmployeeController - end: endingChars = {}", endingChars);
        return employeeMapper.toListEmployeeDto(foundUsers);
    }

    @PatchMapping("/users/edit/{id}/add-document")
    @ResponseStatus(HttpStatus.OK)
    public Employee addDocumentToUser(@PathVariable("id") Integer id, @RequestBody @Valid DocumentDto documentDto) {
        log.debug("addDocumentToUser() EmployeeController - start: documentDto = {}", documentDto);
        Document document = documentMapper.toDocument(documentDto);
        Document savedDocument = documentService.create(document);
        log.debug("addDocumentToUser() EmployeeController - end: documentDto = {}", documentDto);
        return employeeService.setDocument(id, savedDocument);
    }

    @PatchMapping("/users/edit/{id}/remove-document")
    @ResponseStatus(HttpStatus.OK)
    public Employee removeDocumentFromUser(@PathVariable("id") Integer personId) {
        log.debug("removeDocumentFromUser() EmployeeController - start: id = {}", personId);
        Employee employee = employeeService.removeDocument(personId);
        log.debug("removeDocumentFromUser() EmployeeController - end: id = {}", personId);
        return employee;
    }

}
