package com.openclassrooms.etudiant.controller;

import com.openclassrooms.etudiant.dto.CreateStudentDTO;
import com.openclassrooms.etudiant.dto.StudentDTO;
import com.openclassrooms.etudiant.dto.UpdateStudentDTO;
import com.openclassrooms.etudiant.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;


    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(
            @Valid @RequestBody CreateStudentDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(studentService.createStudent(dto));
    }


    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {

        return ResponseEntity.ok(
                studentService.getAllStudents()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                studentService.getStudentById(id)
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentDTO dto) {

        return ResponseEntity.ok(
                studentService.updateStudent(id, dto)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Long id) {

        studentService.deleteStudent(id);

        return ResponseEntity.noContent().build();
    }
}