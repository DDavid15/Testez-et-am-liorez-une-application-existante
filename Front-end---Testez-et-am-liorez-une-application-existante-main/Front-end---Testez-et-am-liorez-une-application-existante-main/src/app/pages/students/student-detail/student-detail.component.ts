import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Student } from '../../../core/models/Student';
import { StudentService } from '../../../core/service/student.service';

@Component({
  selector: 'app-student-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './student-detail.component.html',
  styleUrl: './student-detail.component.css'
})
export class StudentDetailComponent implements OnInit {

  private readonly route = inject(ActivatedRoute);
  private readonly studentService = inject(StudentService);

  student?: Student;
  loading = false;
  errorMessage = '';

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.errorMessage = 'Identifiant étudiant invalide.';
      return;
    }

    this.loadStudent(id);
  }

  private loadStudent(id: number): void {
    this.loading = true;
    this.errorMessage = '';

    this.studentService.getStudentById(id).subscribe({
      next: (student: Student) => {
        this.student = student;
        this.loading = false;
      },
      error: () => {
        this.errorMessage =
          'Impossible de récupérer les informations de cet étudiant.';
        this.loading = false;
      }
    });
  }
}