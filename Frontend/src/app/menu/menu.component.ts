import { Component, OnInit } from '@angular/core';
import { DataService } from '../data.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements OnInit {
  nume: string = '';

  constructor(private router: Router, private dataService: DataService) {
    this.nume = ''; 
  }

  ngOnInit() {
    this.dataService.currentUser.subscribe(nume => this.nume = nume);
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
  }
}
