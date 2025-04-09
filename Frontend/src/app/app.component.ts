import { Component } from '@angular/core';
import { DataService } from './data.service';
import { Router, NavigationStart } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'HandmadeShopping';
  showMenu = true; 

  constructor(public dataService: DataService, private router: Router) {
    router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        if (event.url === '/login' || event.url === '/') {
          this.dataService.setLoggedIn(false);
          this.showMenu = false; 
        } else {
          this.showMenu = true; 
        }
      }
    });
  }
}