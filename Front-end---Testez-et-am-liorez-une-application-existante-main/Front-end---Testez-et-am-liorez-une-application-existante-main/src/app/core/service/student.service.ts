import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  CreateStudent,
  Student,
  UpdateStudent
} from '../models/Student';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  private readonly apiUrl = '/api/students';

  constructor(private httpClient: HttpClient) {}

  getAllStudents(): Observable<Student[]> {
    return this.httpClient.get<Student[]>(this.apiUrl);
  }

  getStudentById(id: number): Observable<Student> {
    return this.httpClient.get<Student>(`${this.apiUrl}/${id}`);
  }

  createStudent(student: CreateStudent): Observable<Student> {
    return this.httpClient.post<Student>(this.apiUrl, student);
  }

  updateStudent(
    id: number,
    student: UpdateStudent
  ): Observable<Student> {
    return this.httpClient.put<Student>(
      `${this.apiUrl}/${id}`,
      student
    );
  }

  deleteStudent(id: number): Observable<void> {
    return this.httpClient.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}