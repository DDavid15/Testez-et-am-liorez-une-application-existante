package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.dto.CreateStudentDTO;
import com.openclassrooms.etudiant.dto.StudentDTO;
import com.openclassrooms.etudiant.dto.UpdateStudentDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.mapper.StudentMapper;
import com.openclassrooms.etudiant.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    @Test
    void createStudentShouldReturnCreatedStudent() {
        // GIVEN : les données nécessaires à la création d'un étudiant
        CreateStudentDTO createStudentDTO = new CreateStudentDTO();
        createStudentDTO.setFirstName("Emma");
        createStudentDTO.setLastName("Martin");
        createStudentDTO.setEmail("emma.martin@email.fr");

        Student studentToSave = createStudent(
                null,
                "Emma",
                "Martin",
                "emma.martin@email.fr"
        );

        Student savedStudent = createStudent(
                1L,
                "Emma",
                "Martin",
                "emma.martin@email.fr"
        );

        StudentDTO expectedStudentDTO = createStudentDTO(
                1L,
                "Emma",
                "Martin",
                "emma.martin@email.fr"
        );

        when(studentMapper.toEntity(createStudentDTO))
                .thenReturn(studentToSave);

        when(studentRepository.save(studentToSave))
                .thenReturn(savedStudent);

        when(studentMapper.toDto(savedStudent))
                .thenReturn(expectedStudentDTO);

        // WHEN : le service crée l'étudiant
        StudentDTO result = studentService.createStudent(createStudentDTO);

        // THEN : l'étudiant créé est retourné
        assertEquals(expectedStudentDTO, result);

        verify(studentMapper).toEntity(createStudentDTO);
        verify(studentRepository).save(studentToSave);
        verify(studentMapper).toDto(savedStudent);
    }

    @Test
    void getAllStudentsShouldReturnStudentList() {
        // GIVEN : deux étudiants présents dans le repository
        Student firstStudent = createStudent(
                1L,
                "Emma",
                "Martin",
                "emma.martin@email.fr"
        );

        Student secondStudent = createStudent(
                2L,
                "Lucas",
                "Bernard",
                "lucas.bernard@email.fr"
        );

        StudentDTO firstStudentDTO = createStudentDTO(
                1L,
                "Emma",
                "Martin",
                "emma.martin@email.fr"
        );

        StudentDTO secondStudentDTO = createStudentDTO(
                2L,
                "Lucas",
                "Bernard",
                "lucas.bernard@email.fr"
        );

        when(studentRepository.findAll())
                .thenReturn(List.of(firstStudent, secondStudent));

        when(studentMapper.toDto(firstStudent))
                .thenReturn(firstStudentDTO);

        when(studentMapper.toDto(secondStudent))
                .thenReturn(secondStudentDTO);

        // WHEN : le service récupère tous les étudiants
        List<StudentDTO> result = studentService.getAllStudents();

        // THEN : la liste complète est retournée
        assertEquals(
                List.of(firstStudentDTO, secondStudentDTO),
                result
        );

        verify(studentRepository).findAll();
        verify(studentMapper).toDto(firstStudent);
        verify(studentMapper).toDto(secondStudent);
    }

    @Test
    void getStudentByIdShouldReturnStudent() {
        // GIVEN : un étudiant existant
        Long studentId = 1L;

        Student student = createStudent(
                studentId,
                "Emma",
                "Martin",
                "emma.martin@email.fr"
        );

        StudentDTO expectedStudentDTO = createStudentDTO(
                studentId,
                "Emma",
                "Martin",
                "emma.martin@email.fr"
        );

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));

        when(studentMapper.toDto(student))
                .thenReturn(expectedStudentDTO);

        // WHEN : le service recherche l'étudiant par son identifiant
        StudentDTO result = studentService.getStudentById(studentId);

        // THEN : l'étudiant correspondant est retourné
        assertEquals(expectedStudentDTO, result);

        verify(studentRepository).findById(studentId);
        verify(studentMapper).toDto(student);
    }

    @Test
    void updateStudentShouldReturnUpdatedStudent() {
        // GIVEN : un étudiant existant et de nouvelles informations
        Long studentId = 1L;

        Student existingStudent = createStudent(
                studentId,
                "Emma",
                "Martin",
                "emma.martin@email.fr"
        );

        UpdateStudentDTO updateStudentDTO = new UpdateStudentDTO();
        updateStudentDTO.setFirstName("Émilie");
        updateStudentDTO.setLastName("Durand");
        updateStudentDTO.setEmail("emilie.durand@email.fr");

        StudentDTO expectedStudentDTO = createStudentDTO(
                studentId,
                "Émilie",
                "Durand",
                "emilie.durand@email.fr"
        );

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(existingStudent));

        when(studentRepository.save(existingStudent))
                .thenReturn(existingStudent);

        when(studentMapper.toDto(existingStudent))
                .thenReturn(expectedStudentDTO);

        // WHEN : le service modifie l'étudiant
        StudentDTO result = studentService.updateStudent(
                studentId,
                updateStudentDTO
        );

        // THEN : les nouvelles informations sont retournées
        assertEquals(expectedStudentDTO, result);
        assertEquals("Émilie", existingStudent.getFirstName());
        assertEquals("Durand", existingStudent.getLastName());
        assertEquals(
                "emilie.durand@email.fr",
                existingStudent.getEmail()
        );

        verify(studentRepository).findById(studentId);
        verify(studentRepository).save(existingStudent);
        verify(studentMapper).toDto(existingStudent);
    }

    @Test
    void deleteStudentShouldDeleteExistingStudent() {
        // GIVEN : un étudiant existant
        Long studentId = 1L;

        Student student = createStudent(
                studentId,
                "Emma",
                "Martin",
                "emma.martin@email.fr"
        );

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));

        // WHEN : le service supprime l'étudiant
        studentService.deleteStudent(studentId);

        // THEN : le repository reçoit l'étudiant à supprimer
        verify(studentRepository).findById(studentId);
        verify(studentRepository).delete(student);
    }

    private Student createStudent(
            Long id,
            String firstName,
            String lastName,
            String email
    ) {
        Student student = new Student();
        student.setId(id);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        return student;
    }

    private StudentDTO createStudentDTO(
            Long id,
            String firstName,
            String lastName,
            String email
    ) {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(id);
        studentDTO.setFirstName(firstName);
        studentDTO.setLastName(lastName);
        studentDTO.setEmail(email);
        return studentDTO;
    }
}