import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const token =
    localStorage.getItem('knowflow_token');

  console.log(
    'HTTP REQUEST:',
    req.method,
    req.url
  );

  console.log(
    'JWT TOKEN:',
    token
  );

  if (token) {

    const clonedReq =
      req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });

    console.log(
      'Authorization header added'
    );

    return next(clonedReq);
  }

  return next(req);
};