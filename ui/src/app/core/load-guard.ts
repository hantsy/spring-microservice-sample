import { Injectable } from '@angular/core';
import { CanLoad, Route, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { tap } from 'rxjs/operators';

@Injectable()
export class LoadGuard implements CanLoad {
  constructor(private router: Router, private authService: AuthService) { }

  canLoad(route: Route) {
    console.log('call canLoad guard.');
    this.authService.isAuthenticated().pipe(
      tap(auth => {
      if (!auth) {
        this.router.navigate(['', 'auth', 'signin']);
        return false;
      }
    })
  );
    return true;
  }
}
