package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.dto.CreateStudentDTO;
import com.openclassrooms.etudiant.dto.UpdateStudentDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.StudentRepository;
import com.openclassrooms.etudiant.repository.UserRepository;
import com.openclassrooms.etudiant.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Testcontainers
class StudentControllerTest {

    private static final String STUDENTS_URL = "/students";

    private static final String USER_LOGIN = "agent";
    private static final String USER_PASSWORD = "password";

    private static final String FIRST_NAME = "Emma";
    private static final String LAST_NAME = "Martin";
    private static final String EMAIL = "emma.martin@email.fr";

    @Container
    static final MySQLContainer<?> mySQLContainer =
            new MySQLContainer<>("mysql:8.0.36");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @DynamicPropertySource
    static void configureTestProperties(
            DynamicPropertyRegistry registry
    ) {
        registry.add(
                "spring.datasource.url",
                mySQLContainer::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                mySQLContainer::getUsername
        );

        registry.add(
                "spring.datasource.password",
                mySQLContainer::getPassword
        );

        registry.add(
                "spring.jpa.hibernate.ddl-auto",
                () -> "create"
        );
    }

    @AfterEach
    void afterEach() {
        // Nettoie les données créées par chaque test.
        studentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createStudentSuccessful() throws Exception {
        // GIVEN : un utilisateur authentifié et un étudiant à créer
        String authorizationHeader = createAuthorizationHeader();

        CreateStudentDTO createStudentDTO =
                new CreateStudentDTO();

        createStudentDTO.setFirstName(FIRST_NAME);
        createStudentDTO.setLastName(LAST_NAME);
        createStudentDTO.setEmail(EMAIL);

        // WHEN : l'utilisateur appelle l'API de création
        mockMvc.perform(
                        MockMvcRequestBuilders.post(STUDENTS_URL)
                                .header(
                                        "Authorization",
                                        authorizationHeader
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                createStudentDTO
                                        )
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                // THEN : l'étudiant créé est retourné
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(
                        jsonPath("$.firstName")
                                .value(FIRST_NAME)
                )
                .andExpect(
                        jsonPath("$.lastName")
                                .value(LAST_NAME)
                )
                .andExpect(
                        jsonPath("$.email")
                                .value(EMAIL)
                );
    }

    @Test
    void getAllStudentsSuccessful() throws Exception {
        // GIVEN : un utilisateur authentifié et un étudiant existant
        String authorizationHeader = createAuthorizationHeader();

        studentRepository.save(
                createStudent(
                        FIRST_NAME,
                        LAST_NAME,
                        EMAIL
                )
        );

        // WHEN : l'utilisateur demande la liste des étudiants
        mockMvc.perform(
                        MockMvcRequestBuilders.get(STUDENTS_URL)
                                .header(
                                        "Authorization",
                                        authorizationHeader
                                )
                                .accept(MediaType.APPLICATION_JSON)
                )
                // THEN : la liste contient l'étudiant enregistré
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(
                        jsonPath("$[0].firstName")
                                .value(FIRST_NAME)
                )
                .andExpect(
                        jsonPath("$[0].lastName")
                                .value(LAST_NAME)
                )
                .andExpect(
                        jsonPath("$[0].email")
                                .value(EMAIL)
                );
    }

    @Test
    void getStudentByIdSuccessful() throws Exception {
        // GIVEN : un utilisateur authentifié et un étudiant existant
        String authorizationHeader = createAuthorizationHeader();

        Student savedStudent = studentRepository.save(
                createStudent(
                        FIRST_NAME,
                        LAST_NAME,
                        EMAIL
                )
        );

        // WHEN : l'utilisateur recherche l'étudiant par son ID
        mockMvc.perform(
                        MockMvcRequestBuilders.get(
                                        STUDENTS_URL + "/{id}",
                                        savedStudent.getId()
                                )
                                .header(
                                        "Authorization",
                                        authorizationHeader
                                )
                                .accept(MediaType.APPLICATION_JSON)
                )
                // THEN : l'étudiant correspondant est retourné
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(savedStudent.getId())
                )
                .andExpect(
                        jsonPath("$.firstName")
                                .value(FIRST_NAME)
                )
                .andExpect(
                        jsonPath("$.lastName")
                                .value(LAST_NAME)
                )
                .andExpect(
                        jsonPath("$.email")
                                .value(EMAIL)
                );
    }

    @Test
    void updateStudentSuccessful() throws Exception {
        // GIVEN : un utilisateur authentifié et un étudiant existant
        String authorizationHeader = createAuthorizationHeader();

        Student savedStudent = studentRepository.save(
                createStudent(
                        FIRST_NAME,
                        LAST_NAME,
                        EMAIL
                )
        );

        UpdateStudentDTO updateStudentDTO =
                new UpdateStudentDTO();

        updateStudentDTO.setFirstName("Émilie");
        updateStudentDTO.setLastName("Durand");
        updateStudentDTO.setEmail(
                "emilie.durand@email.fr"
        );

        // WHEN : l'utilisateur modifie l'étudiant
        mockMvc.perform(
                        MockMvcRequestBuilders.put(
                                        STUDENTS_URL + "/{id}",
                                        savedStudent.getId()
                                )
                                .header(
                                        "Authorization",
                                        authorizationHeader
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                updateStudentDTO
                                        )
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                // THEN : les nouvelles informations sont retournées
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(savedStudent.getId())
                )
                .andExpect(
                        jsonPath("$.firstName")
                                .value("Émilie")
                )
                .andExpect(
                        jsonPath("$.lastName")
                                .value("Durand")
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("emilie.durand@email.fr")
                );
    }

    @Test
    void deleteStudentSuccessful() throws Exception {
        // GIVEN : un utilisateur authentifié et un étudiant existant
        String authorizationHeader = createAuthorizationHeader();

        Student savedStudent = studentRepository.save(
                createStudent(
                        FIRST_NAME,
                        LAST_NAME,
                        EMAIL
                )
        );

        // WHEN : l'utilisateur supprime l'étudiant
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(
                                        STUDENTS_URL + "/{id}",
                                        savedStudent.getId()
                                )
                                .header(
                                        "Authorization",
                                        authorizationHeader
                                )
                )
                // THEN : l'API confirme la suppression
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    private String createAuthorizationHeader() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setLogin(USER_LOGIN);
        user.setPassword(USER_PASSWORD);

        userService.register(user);

        String jwtToken = userService.login(
                USER_LOGIN,
                USER_PASSWORD
        );

        return "Bearer " + jwtToken;
    }

    private Student createStudent(
            String firstName,
            String lastName,
            String email
    ) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        return student;
    }
}