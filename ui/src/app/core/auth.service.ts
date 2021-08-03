import { Injectable, Inject } from '@angular/core';
import { Router, ActivatedRoute, UrlSegment } from '@angular/router';
import { Observable ,  BehaviorSubject ,  ReplaySubject,  } from 'rxjs';
import { tap , distinctUntilChanged, map } from 'rxjs/operators';

import { User } from './user.model';
import { TokenStorage } from './token-storage';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { Credentials } from './credentials.model';

const apiUrl = environment.baseApiUrl + '/auth';

interface State {
  user: User;
  authenticated: boolean;
}

const defaultState: State = {
  user: null,
  authenticated: false
};

const _store = new BehaviorSubject<State>(defaultState);

class Store {
  private _store = _store;
  changes = this._store.asObservable().pipe(distinctUntilChanged());

  setState(state: State) {
    console.log('update user state:' + JSON.stringify(state));
    this._store.next(state);
  }

  getState(): State {
    return this._store.value;
  }

  updateState(data: State) {
    this._store.next(Object.assign({}, this.getState(), data));
  }

  purge() {
    this._store.next(defaultState);
  }
}

@Injectable()
export class AuthService {

  private store: Store = new Store();

  constructor(
    private http: HttpClient,
    private jwt: TokenStorage,
    private router: Router) {
  }

  attempAuth(credentials: Credentials): Observable<any> {
    const headers = new HttpHeaders()
      .set('Authorization', 'Basic ' + btoa(credentials.username + ':' + credentials.password));

    console.log('attempAuth ::');
    console.log(headers);
    return this.http.get<User>(`${apiUrl}/user`, { headers })
      .pipe(
      tap(data => {
        console.log('do::');
        console.log(data);
        this.store.setState({
          user: data, authenticated: Boolean(data)
        });
      })
      );
  }

  verifyAuth(): void {

    if (this.jwt.get()) {
      this.http.get<User>(`${apiUrl}/user`).subscribe(
        data => {
          this.store.setState({ user: data, authenticated: Boolean(data) });
        },
        err => {
          this.jwt.destroy();
          this.store.purge();
        }
      );
    } else {
      // token is not found in local storage.
      this.jwt.destroy();
      this.store.purge();
    }
  }

  signout() {
    this.http.get<any>(`${apiUrl}/logout`).subscribe(
      (data) => {
        // reset the initial values
        this.jwt.destroy();
        this.store.purge();
      }
    );

  }

  currentUser(): Observable<User> {
    return this.store.changes.pipe(
      map(data => data.user)
    );
  }

  isAuthenticated(): Observable<boolean> {
    return this.store.changes.pipe(
      map(data => data.authenticated)
    );
  }

}
