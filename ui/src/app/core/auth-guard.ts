import {
  CanActivate,
  Router,
  ActivatedRouteSnapshot,
  RouterStateSnapshot
} from '@angular/router';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private authService: AuthService) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    console.log('route::' + route.url);
    console.log('state::' + state.url);

    this.authService.isAuthenticated().subscribe(auth => {
      if (!auth) {
        this.router.navigate(['', 'auth', 'signin']);
        return false;
      }
    });
    return true;
  }
}
