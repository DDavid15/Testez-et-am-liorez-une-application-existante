package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.dto.CreateStudentDTO;
import com.openclassrooms.etudiant.dto.StudentDTO;
import com.openclassrooms.etudiant.dto.UpdateStudentDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.mapper.StudentMapper;
import com.openclassrooms.etudiant.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;


    public StudentDTO createStudent(CreateStudentDTO dto) {

        Student student = studentMapper.toEntity(dto);

        Student savedStudent = studentRepository.save(student);

        return studentMapper.toDto(savedStudent);
    }


    public List<StudentDTO> getAllStudents() {

        return studentRepository.findAll()
                .stream()
                .map(studentMapper::toDto)
                .toList();
    }


    public StudentDTO getStudentById(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found with id : " + id)
                );

        return studentMapper.toDto(student);
    }


    public StudentDTO updateStudent(Long id, UpdateStudentDTO dto) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found with id : " + id)
                );


        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());


        Student updatedStudent = studentRepository.save(student);

        return studentMapper.toDto(updatedStudent);
    }


    public void deleteStudent(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found with id : " + id)
                );

        studentRepository.delete(student);
    }
}