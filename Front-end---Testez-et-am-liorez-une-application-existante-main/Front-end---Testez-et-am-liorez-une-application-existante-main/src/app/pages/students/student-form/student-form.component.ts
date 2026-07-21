import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {
  ActivatedRoute,
  Router,
  RouterLink
} from '@angular/router';

import {
  CreateStudent,
  Student,
  UpdateStudent
} from '../../../core/models/Student';
import { StudentService } from '../../../core/service/student.service';

@Component({
  selector: 'app-student-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './student-form.component.html',
  styleUrl: './student-form.component.css'
})
export class StudentFormComponent implements OnInit {

  private readonly formBuilder = inject(FormBuilder);
  private readonly studentService = inject(StudentService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  studentForm: FormGroup = this.formBuilder.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]]
  });

  studentId?: number;
  editMode = false;
  submitted = false;
  loading = false;
  errorMessage = '';

  ngOnInit(): void {
    const idParameter = this.route.snapshot.paramMap.get('id');

    if (idParameter === null) {
      return;
    }

    const id = Number(idParameter);

    if (!Number.isInteger(id) || id <= 0) {
      this.errorMessage = 'Identifiant étudiant invalide.';
      return;
    }

    this.studentId = id;
    this.editMode = true;
    this.loadStudent(id);
  }

  get form() {
    return this.studentForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';

    if (this.studentForm.invalid) {
      return;
    }

    if (this.editMode && this.studentId !== undefined) {
      this.updateStudent();
      return;
    }

    this.createStudent();
  }

  private loadStudent(id: number): void {
    this.loading = true;

    this.studentService.getStudentById(id).subscribe({
      next: (student: Student) => {
        this.studentForm.patchValue({
          firstName: student.firstName,
          lastName: student.lastName,
          email: student.email
        });

        this.loading = false;
      },
      error: () => {
        this.errorMessage =
          'Impossible de récupérer les informations de cet étudiant.';
        this.loading = false;
      }
    });
  }

  private createStudent(): void {
    const student: CreateStudent = {
      firstName: this.studentForm.value.firstName,
      lastName: this.studentForm.value.lastName,
      email: this.studentForm.value.email
    };

    this.loading = true;

    this.studentService.createStudent(student).subscribe({
      next: (createdStudent: Student) => {
        this.router.navigate([
          '/students',
          createdStudent.id
        ]);
      },
      error: () => {
        this.errorMessage =
          'Impossible de créer cet étudiant. Vérifiez notamment que l’adresse email n’est pas déjà utilisée.';
        this.loading = false;
      }
    });
  }

  private updateStudent(): void {
    const student: UpdateStudent = {
      firstName: this.studentForm.value.firstName,
      lastName: this.studentForm.value.lastName,
      email: this.studentForm.value.email
    };

    this.loading = true;

    this.studentService
      .updateStudent(this.studentId!, student)
      .subscribe({
        next: (updatedStudent: Student) => {
          this.router.navigate([
            '/students',
            updatedStudent.id
          ]);
        },
        error: () => {
          this.errorMessage =
            'Impossible de modifier cet étudiant. Vérifiez notamment que l’adresse email n’est pas déjà utilisée.';
          this.loading = false;
        }
      });
  }
}