import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/auth.service';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css']
})
export class SigninComponent implements OnInit {

  private username = '';
  private password = '';
  errorMessage = '';

  sub: Subscription = null;

  constructor(private auth: AuthService, private router: Router) { }

  ngOnInit() {
    console.log('calling ngOnInit...');
  }

  submit() {
    console.log('calling submit...');
    this.auth.attempAuth({ username: this.username, password: this.password })
      .subscribe(
      (data) => {
        // console.log(data);
        this.router.navigate(['']);
      },
      (err) => {
        // console.log(err);
        this.errorMessage = 'login failed';
        return;
      }
      );
  }

}
