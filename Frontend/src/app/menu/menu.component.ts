// menu.component.ts
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../services/user-service/user.service';
import { User } from '../models/user.model';
import { RoleService } from '../services/role-service/role.service';
import { Role } from '../models/role.model';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css'] // <-- corect, trebuie să fie styleUrls (cu 's')
})
export class MenuComponent implements OnInit {
  user: User = User.createDefault();
  showPopup = false;
  allRoles: Role[] = [];
  confirmPassword: string = '';

  constructor(
    private router: Router,
    private userService: UserService,
    private roleService: RoleService
  ) { }

  ngOnInit() {
    this.userService.currentUserData.subscribe(user => {
      if (user) this.user = user;
    });

    this.roleService.getAllRoles().subscribe(roles => {
      this.allRoles = roles;
    });
  }

  hasAnyRole(): boolean {
    return this.userService.userHasAnyRole(this.allRoles.map(role => role.name));
  }

  navigateToLogin() {
    this.userService.logoutUser();
    this.router.navigate(['/login']);
  }

  settings() {
    this.togglePopup();
  }

togglePopup() {
  if (!this.showPopup) {
    this.userService.currentUserData.subscribe(user => {
      if (user) {
        // Creează un nou obiect User cu toate proprietățile necesare
        this.user = new User(
          user.name,
          user.surname,
          user.email,
          [...user.roles],
          user.password,
          user.clientId,
          user.clientSecret,
          user.id
        );
        this.confirmPassword = user.password || '';
      }
    }).unsubscribe();
  }
  this.showPopup = !this.showPopup;
}

  toggleRole(roleName: string): void {
    if (this.user.roles.includes(roleName)) {
      this.user.roles = this.user.roles.filter(r => r !== roleName);
    } else {
      this.user.roles = [...this.user.roles, roleName];
    }
  }

  hasRole(roleName: string): boolean {
    return this.user.roles.includes(roleName);
  }

  saveChanges(): void {
    if (this.user.password && this.user.password !== this.confirmPassword) {
      alert('Passwords do not match!');
      return;
    }

    this.user.roles = [...new Set(this.user.roles)];
    if (!this.user.roles.includes('viewer')) {
      this.user.roles.push('viewer');
    }

    this.userService.updateUser(this.user).subscribe({
      next: () => {
        this.userService.updateUserRoles(this.user.id!, this.user.roles).subscribe({
          next: () => {
            this.userService.setUserData(this.user);
            this.togglePopup();
          },
          error: (error) => console.error('Error updating roles:', error)
        });
      },
      error: (error) => console.error('Error updating user:', error)
    });
  }
}