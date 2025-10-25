import { HttpInterceptorFn } from "@angular/common/http";
import { inject, NgZone } from "@angular/core";
import { AuthService } from "../../user/services/auth.service";
import { Router } from "@angular/router";
import { EMPTY } from "rxjs";
import { AlertService } from "../services/alert.service";

function parseJwt(token: string | null) {
  if (!token) return null;
  try {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload));
  } catch {
    return null;
  }
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const zone = inject(NgZone);
  const alertService = inject(AlertService);

  const publicEndpoints = [
    '/merenderos/close',   // Endpoint de merenderos cercanos
  ];

  // Verifica si la solicitud es para un endpoint público
  const isPublicRequest = publicEndpoints.some(endpoint =>
    req.url.includes(endpoint)
  );

  // Si es pública, NO agregamos el token
  if (isPublicRequest) {
    return next(req);
  }

  const token = authService.getToken();
  // Para endpoints privados: agregamos el token (comprobamos expiración antes de adjuntarlo
  if (token) {
    const payload = parseJwt(token);
    const exp = payload?.exp;

    if (exp && Date.now() >= exp * 1000) {
      // Token expirado -> cerrar sesión y no enviar token
      authService.logout();
      zone.run(() => {
        router.navigate(['/login-user'], { replaceUrl: true });
      });
      alertService.error("Sesión expirada", "Por favor, inicia sesión nuevamente");
      return EMPTY;
    }

    const clonedReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(clonedReq);
  }

  return next(req);
};