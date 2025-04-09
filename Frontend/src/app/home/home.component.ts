import { Component, OnInit} from '@angular/core';
import { DataService } from '../data.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  nume: string ='';

  constructor(private dataService: DataService) {
    this.nume = ''; 
  }

  ngOnInit() {
    this.dataService.currentUser.subscribe(nume => this.nume = nume);
  }
}
