import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { JwtDecoderService } from '../jwt-decoder/jwt-decoder.service';

export const authGuard: CanActivateFn = (route, state) => {
  const jwtService = inject(JwtDecoderService);
  const router = inject(Router);
  if (!jwtService.hasValidToken()) {
    router.navigate(['/login']);
    return false;
  }
  // Check for any required roles in route data
  const payload = jwtService.getPayloadIfValid();
  const requiredRoles = route.data?.['roles'] as string[] | undefined;
  if (requiredRoles?.length && payload && !requiredRoles.some(r => payload.roles.includes(r))) {
    router.navigate(['/home']);
    return false;
  }
  return true;
};
