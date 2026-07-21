import { Routes } from '@angular/router';

import { authGuard } from './core/guard/auth.guard';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import {
  StudentListComponent
} from './pages/students/student-list/student-list.component';
import {
  StudentDetailComponent
} from './pages/students/student-detail/student-detail.component';
import {
  StudentFormComponent
} from './pages/students/student-form/student-form.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'students',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: StudentListComponent
      },
      {
        path: 'new',
        component: StudentFormComponent
      },
      {
        path: ':id/edit',
        component: StudentFormComponent
      },
      {
        path: ':id',
        component: StudentDetailComponent
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];