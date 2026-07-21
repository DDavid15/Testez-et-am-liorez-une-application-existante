import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Student } from '../../../core/models/Student';
import { StudentService } from '../../../core/service/student.service';

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink
  ],
  templateUrl: './student-list.component.html',
  styleUrl: './student-list.component.css'
})
export class StudentListComponent implements OnInit {

  private readonly studentService = inject(StudentService);

  students: Student[] = [];
  loading = false;
  errorMessage = '';

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    this.loading = true;
    this.errorMessage = '';

    this.studentService.getAllStudents().subscribe({
      next: (students: Student[]) => {
        this.students = students;
        this.loading = false;
      },
      error: () => {
        this.errorMessage =
          'Impossible de récupérer la liste des étudiants.';
        this.loading = false;
      }
    });
  }

  deleteStudent(student: Student): void {
    const confirmed = window.confirm(
      `Supprimer l'étudiant ${student.firstName} ${student.lastName} ?`
    );

    if (!confirmed) {
      return;
    }

    this.errorMessage = '';

    this.studentService.deleteStudent(student.id).subscribe({
      next: () => {
        this.students = this.students.filter(
          currentStudent => currentStudent.id !== student.id
        );
      },
      error: () => {
        this.errorMessage =
          `Impossible de supprimer ${student.firstName} ${student.lastName}.`;
      }
    });
  }
}