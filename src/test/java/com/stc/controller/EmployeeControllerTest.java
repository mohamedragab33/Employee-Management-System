package com.stc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stc.dto.EmployeeReq;
import com.stc.dto.EmployeeRes;
import com.stc.service.EmailValidationService;
import com.stc.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
 class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private EmailValidationService emailValidationService;

    private EmployeeReq employeeReq;
    private EmployeeRes employeeRes;

    @BeforeEach
    void setUp() {
        EmployeeReq.EmployeeReqBuilder employeeReqBuilder = EmployeeReq.builder();
        employeeReqBuilder.firstName("Mo")
                .lastName("Ragab")
                .email("Mo.Ragab@example.com")
                .department("IT")
                .salary(5000);
        employeeReq = employeeReqBuilder.build();
    
        EmployeeRes.EmployeeResBuilder employeeResBuilder = EmployeeRes.builder();
        employeeResBuilder.firstName("Mo")
                .lastName("Ragab")
                .email("mohamed.ragab.dv@gmail.com")
                .department("IT")
                .salary(5000);
        employeeRes = employeeResBuilder.build();
    }

    @Test
    void createEmployee_shouldReturnCreatedEmployee() throws Exception {
        Mockito.when(employeeService.createEmployee(any(EmployeeReq.class))).thenReturn(employeeRes);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Mo"))
                .andExpect(jsonPath("$.lastName").value("Ragab"))
                .andExpect(jsonPath("$.email").value("mohamed.ragab.dv@gmail.com"))
                .andExpect(jsonPath("$.department").value("IT"))
                .andExpect(jsonPath("$.salary").value(5000));
    }

    @Test
    void getEmployeeById_shouldReturnEmployee() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(employeeService.getEmployeeById(id)).thenReturn(employeeRes);

        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Mo"))
                .andExpect(jsonPath("$.lastName").value("Ragab"))
                .andExpect(jsonPath("$.email").value("mohamed.ragab.dv@gmail.com"))
                .andExpect(jsonPath("$.department").value("IT"))
                .andExpect(jsonPath("$.salary").value(5000));
    }

    @Test
    void updateEmployee_shouldReturnUpdatedEmployee() throws Exception {
        UUID id = UUID.randomUUID();
        EmployeeReq updatedReq = new EmployeeReq("Jane", "Doe", "jane.doe@example.com", "HR", 6000);
        EmployeeRes updatedRes = new EmployeeRes(id,"Jane", "Doe", "jane.doe@example.com", "HR", 6000);

        Mockito.when(employeeService.updateEmployee(eq(id), any(EmployeeReq.class))).thenReturn(updatedRes);

        mockMvc.perform(put("/api/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"))
                .andExpect(jsonPath("$.department").value("HR"))
                .andExpect(jsonPath("$.salary").value(6000));
    }

    @Test
    void deleteEmployee_shouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(employeeService).deleteEmployee(id);

        mockMvc.perform(delete("/api/employees/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAllEmployees_shouldReturnListOfEmployees() throws Exception {
        Mockito.when(employeeService.listAllEmployees()).thenReturn(List.of(employeeRes));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Mo"))
                .andExpect(jsonPath("$[0].lastName").value("Ragab"))
                .andExpect(jsonPath("$[0].email").value("mohamed.ragab.dv@gmail.com"))
                .andExpect(jsonPath("$[0].salary").value(5000));
    }

    @Test
    void validateEmail_shouldReturnBoolean() throws Exception {
        String email = "test@example.com";
        Mockito.when(emailValidationService.validateEmail(email)).thenReturn(true);

        mockMvc.perform(post("/api/employees/validate-email")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(email))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
