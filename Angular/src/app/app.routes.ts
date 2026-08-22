import { Routes } from '@angular/router';

import { Login } from './authentication/login/login';
import { Registration } from './authentication/registration/registration';
import { ForgotPassword } from './authentication/forgot-password/forgot-password';
import { ResetPassword } from './authentication/reset-password/reset-password';
import { Home } from './homePage/home';
import { Documents } from './dashboards/admin/documents/documents';
import { authGuard } from './guards/auth-guard';
import { Authentication } from './authentication/authentication';

import { Users } from './dashboards/admin/users/users';
import { Departments } from './dashboards/admin/departments/departments';


export const routes: Routes = [

  // ==========================================
  // HOME
  // ==========================================

  {
    path: '',
    redirectTo: 'Home',
    pathMatch: 'full'
  },

  {
    path: 'Home',
    component: Home
  },


  // ==========================================
  // AUTHENTICATION
  // ==========================================

  {
    path: 'login',
    component: Login
  },

  {
    path: 'register',
    component: Registration
  },

  {
    path: 'forgot-password',
    component: ForgotPassword
  },

  {
    path: 'reset-password',
    component: ResetPassword
  },

  {
    path: 'auth',
    component: Authentication
  },


  // ==========================================
  // ADMIN
  // ==========================================

  {
    path: 'admin',

    canActivate: [authGuard],

    loadComponent: () =>
      import('./dashboards/admin/admin')
        .then(m => m.Admin),

    children: [

      // ----------------------------------------
      // ADMIN DASHBOARD
      // /admin/dashboard
      // ----------------------------------------

      {
        path: 'dashboard',

        loadComponent: () =>
          import('./dashboards/admin/dashboard/dashboard')
            .then(m => m.Dashboard)
      },


      // ----------------------------------------
      // USERS
      // /admin/users
      // ----------------------------------------

      {
        path: 'users',

        component: Users,

        data: {
          mode: 'list'
        }
      },

      {
        path: 'users/add',

        component: Users,

        data: {
          mode: 'add'
        }
      },

      {
        path: 'users/edit/:id',

        component: Users,

        data: {
          mode: 'edit'
        }
      },


      // ----------------------------------------
      // DEPARTMENTS
      // /admin/departments
      // ----------------------------------------

      {
        path: 'departments',

        component: Departments,

        data: {
          mode: 'list'
        }
      },

      {
        path: 'departments/add',

        component: Departments,

        data: {
          mode: 'add'
        }
      },

      {
        path: 'departments/edit/:id',

        component: Departments,

        data: {
          mode: 'edit'
        }
      },

// ----------------------------------------
// DOCUMENTS
// /admin/documents
// ----------------------------------------

{
  path: 'documents',

  component: Documents,

  data: {
    mode: 'list'
  }
},

{
  path: 'documents/add',

  component: Documents,

  data: {
    mode: 'add'
  }
},

{
  path: 'documents/edit/:id',

  component: Documents,

  data: {
    mode: 'edit'
  }
},


      // ----------------------------------------
      // DEFAULT ADMIN ROUTE
      // /admin
      // ----------------------------------------

      {
        path: '',
        redirectTo: 'users',
        pathMatch: 'full'
      }

    ]
  },


  // ==========================================
  // MANAGER
  // ==========================================

  {
    path: 'manager/dashboard',

    canActivate: [authGuard],

    loadComponent: () =>
      import('./dashboards/manager/manager')
        .then(m => m.Manager)
  },


  // ==========================================
  // EMPLOYEE
  // ==========================================

  {
    path: 'employee/dashboard',

    canActivate: [authGuard],

    loadComponent: () =>
      import('./dashboards/employee/employee')
        .then(m => m.Employee)
  },


  // ==========================================
  // UNKNOWN ROUTE
  // ==========================================

  {
    path: '**',
    redirectTo: 'Home'
  }

];