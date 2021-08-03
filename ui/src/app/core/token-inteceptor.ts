import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpSentEvent,
  HttpHeaderResponse,
  HttpProgressEvent,
  HttpResponse,
  HttpUserEvent,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenStorage } from './token-storage';
import { Router } from '@angular/router';

const TOKEN_HEADER_KEY = 'X-AUTH-TOKEN';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor(private token: TokenStorage, private router: Router) { }

  intercept(req: HttpRequest<any>, next: HttpHandler):
    Observable<HttpSentEvent | HttpHeaderResponse | HttpProgressEvent | HttpResponse<any> | HttpUserEvent<any>> {


    // set X-Requested-With = XMLHttpRequest
    req.headers.set('X-Requested-With', 'XMLHttpRequest');

    // set X-AUTH-TOKEN
    if (this.token.get()) {
      console.log('set token in header ::' + this.token.get());
      const authReq = req.clone({headers:  req.headers.set(TOKEN_HEADER_KEY, this.token.get())});
      return next.handle(authReq);
    }

    return next.handle(req);
  }

}
