import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { AuthService } from './core/auth.service';
import { MatIconRegistry, MatDialog } from '@angular/material';
import { DomSanitizer } from '@angular/platform-browser';
import { DialogComponent } from './dialog/dialog.component';

import { User } from './core/user.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  links = [
    {
      icon: 'home',
      path: '',
      label: 'HOME'
    },

    {
      icon: 'list',
      path: '/post/list',
      label: 'POSTS'
    },
    {
      icon: 'add',
      path: '/post/new',
      label: 'NEW POST'
    }
  ];

  isDarkTheme = false;
  currentUser: Observable<User>;

  constructor(
    private auth: AuthService,
    private iconRegistry: MatIconRegistry,
    private sanitizer: DomSanitizer,
    private dialog: MatDialog
  ) {
    // To avoid XSS attacks, the URL needs to be trusted from inside of your application.
    const avatarsSafeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
      './assets/avatars.svg'
    );
    this.iconRegistry.addSvgIconSetInNamespace('avatars', avatarsSafeUrl);
    this.currentUser = this.auth.currentUser();
  }

  // openAdminDialog() {
  //   this.dialog.open(DialogComponent).afterClosed()
  //     .filter(result => !!result)
  //     .subscribe(user => {
  //       this.users.push(user);
  //       this.selectedUser = user;
  //     });
  // }

  ngOnInit(): void {
    console.log('calling ngOnInit...');
    this.auth.verifyAuth();
  }

  signout() {
    this.auth.signout();
  }
}
