import { Component } from '@angular/core';
import { DataService } from './data.service';
import { Router, NavigationStart } from '@angular/router';
import { UserService } from './services/user-service/user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'HandmadeShopping';
  showMenu = true; 

  constructor(public userService: UserService, private router: Router) {
    this.userService.restoreSession();
    router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        if (event.url === '/login' || event.url === '/') {
          this.userService.logoutUser();
          this.userService.setLoggedIn(false);
          localStorage.clear();
          this.showMenu = false; 
        } else {
          this.showMenu = true; 
        }
      }
    });
  }
}