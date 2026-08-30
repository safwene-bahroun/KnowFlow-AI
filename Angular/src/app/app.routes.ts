import { Routes } from '@angular/router';

import { Login } from './authentication/login/login';
import { Registration } from './authentication/registration/registration';
import { ForgotPassword } from './authentication/forgot-password/forgot-password';
import { ResetPassword } from './authentication/reset-password/reset-password';
import { Home } from './homePage/home';

import { Documents } from './dashboards/admin/documents/documents';
import { Users } from './dashboards/admin/users/users';
import { Departments } from './dashboards/admin/departments/departments';
import { SetingProfile } from './dashboards/admin/seting-profile/seting-profile';
import { Notifications } from './dashboards/admin/notifications/notifications';
import { Authentication } from './authentication/authentication';
import { authGuard } from './guards/auth-guard';


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

      {
        path: 'dashboard',

        loadComponent: () =>
          import('./dashboards/admin/dashboard/dashboard')
            .then(m => m.Dashboard)
      },


      // USERS

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


      // DEPARTMENTS

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


      // DOCUMENTS

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


      // SETTINGS

      {
        path: 'settings/profile',
        component: SetingProfile
      },


      // NOTIFICATIONS

      {
        path: 'notifications',
        component: Notifications
      },


      // DEFAULT ADMIN

      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }

    ]
  },


 // ==========================================
// MANAGER
// ==========================================

{
  path: 'manager',

  canActivate: [authGuard],

  loadComponent: () =>
    import('./dashboards/manager/manager')
      .then(m => m.Manager),

  children: [

    // --------------------------------------
    // CHATBOT / MANAGER DASHBOARD
    // URL: /manager/dashboard
    // --------------------------------------

    {
      path: 'dashboard',

      loadComponent: () =>
        import('./dashboards/manager/chat-bot/chat-bot')
          .then(m => m.ChatBot)
    },


    // --------------------------------------
    // CHAT HISTORY
    // URL: /manager/history/conversations
    // --------------------------------------

    {
      path: 'history/conversations',

      loadComponent: () =>
        import('./dashboards/manager/history/history')
          .then(m => m.History)
    },


    // --------------------------------------
    // LOG
    // URL: /manager/log
    // --------------------------------------

    {
      path: 'log',

      loadComponent: () =>
        import('./dashboards/manager/log/log')
          .then(m => m.Log)
    },


    // --------------------------------------
    // NOTIFICATIONS
    // URL: /manager/notifications
    // --------------------------------------

    {
      path: 'notifications',

      loadComponent: () =>
        import('./dashboards/manager/notifications/notifications')
          .then(m => m.Notifications)
    },


    // --------------------------------------
    // PROFILE SETTINGS
    // URL: /manager/settings/profile
    // --------------------------------------

    {
      path: 'settings/profile',

      loadComponent: () =>
        import('./dashboards/manager/seting-profile/seting-profile')
          .then(m => m.SetingProfile)
    },


    // --------------------------------------
    // DEFAULT MANAGER ROUTE
    // /manager -> /manager/dashboard
    // --------------------------------------

    {
      path: '',
      redirectTo: 'dashboard',
      pathMatch: 'full'
    }

  ]
},

  // ==========================================
  // EMPLOYEE
  // ==========================================

  {
    path: 'employee',

    canActivate: [authGuard],

    loadComponent: () =>
      import('./dashboards/employee/employee')
        .then(m => m.Employee),

    children: [

      // --------------------------------------
      // CHATBOT
      // URL:
      // /employee/dashboard
      // --------------------------------------

      {
        path: 'dashboard',

        loadComponent: () =>
          import('./dashboards/employee/chat-bot/chat-bot')
            .then(m => m.ChatBot)
      },


      // --------------------------------------
      // NOTIFICATIONS
      // URL:
      // /employee/notifications
      // --------------------------------------

      {
        path: 'notifications',

        loadComponent: () =>
          import('./dashboards/employee/notifications/notifications')
            .then(m => m.Notifications)
      },


      // --------------------------------------
      // PROFILE SETTINGS
      // URL:
      // /employee/settings/profile
      // --------------------------------------
      {
        path: 'settings/profile',

        loadComponent: () =>
          import('./dashboards/employee/seting-profile/seting-profile')
            .then(m => m.SetingProfile)
      },

 {
        path: 'history/conversations',

        loadComponent: () =>
          import('./dashboards/employee/history/history')
            .then(m => m.History)
      },



      // --------------------------------------
      // DEFAULT EMPLOYEE
      // --------------------------------------

      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }

    ]
  },


  // ==========================================
  // UNKNOWN ROUTE
  // ==========================================

  {
    path: '**',
    redirectTo: 'Home'
  }

];