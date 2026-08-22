import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: 'admin/users/edit/:id',
    renderMode: RenderMode.Server
  },
  {
    path: 'admin/departments/edit/:id',
    renderMode: RenderMode.Server
  },
  {
    path: 'admin/documents/edit/:id',
    renderMode: RenderMode.Server
  },
  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];
