package sg.edu.nus.iss.d13revision.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import sg.edu.nus.iss.d13revision.models.Person;
import sg.edu.nus.iss.d13revision.services.PersonService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    private List<Person> personList;

    @BeforeEach
    void setUp() {
        personList = new ArrayList<>();
        personList.add(new Person("John", "Doe"));
        personList.add(new Person("Jane", "Doe"));
    }

    @Test
    void index() throws Exception {
        mockMvc.perform(get("/person/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void getAllPersons() throws Exception {
        when(personService.getPersons()).thenReturn(personList);

        mockMvc.perform(get("/person/testRetrieve"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    void personList() throws Exception {
        when(personService.getPersons()).thenReturn(personList);

        mockMvc.perform(get("/person/personList"))
                .andExpect(status().isOk())
                .andExpect(view().name("personList"))
                .andExpect(model().attribute("persons", personList));
    }

    @Test
    void showAddPersonPage() throws Exception {
        mockMvc.perform(get("/person/addPerson"))
                .andExpect(status().isOk())
                .andExpect(view().name("addPerson"))
                .andExpect(model().attributeExists("personForm"));
    }

    @Test
    void savePerson_valid() throws Exception {
        mockMvc.perform(post("/person/addPerson")
                .param("firstName", "Peter")
                .param("lastName", "Jones"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/personList"));

        verify(personService, times(1)).addPerson(any(Person.class));
    }

    @Test

    void savePerson_invalid() throws Exception {
        mockMvc.perform(post("/person/addPerson")
                .param("firstName", "")
                .param("lastName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("addPerson"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(personService, never()).addPerson(any(Person.class));
    }

    @Test
    void personToEdit() throws Exception {
        mockMvc.perform(post("/person/personToEdit")
                .flashAttr("per", new Person("Test", "User")))
                .andExpect(status().isOk())
                .andExpect(view().name("editPerson"))
                .andExpect(model().attributeExists("per"));
    }

    @Test
    void personEdit() throws Exception {
        mockMvc.perform(post("/person/personEdit")
                .flashAttr("per", new Person("Updated", "User")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/personList"));

        verify(personService, times(1)).updatePerson(any(Person.class));
    }

    @Test
    void personDelete() throws Exception {
        mockMvc.perform(post("/person/personDelete")
                .flashAttr("per", new Person("To", "Delete")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/personList"));

        verify(personService, times(1)).removePerson(any(Person.class));
    }
}
