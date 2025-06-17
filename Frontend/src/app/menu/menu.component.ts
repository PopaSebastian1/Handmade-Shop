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
  public isFieldDisabled: boolean = true; 

  constructor(
    private router: Router,
    private userService: UserService,
    private roleService: RoleService
  ) { }

  ngOnInit() {
    this.userService.currentUserData.subscribe(user => {
      if (user) this.user = user;
      if(user?.clientId=="" || user?.clientId==null) {
        this.isFieldDisabled = true;
      }
      else{
        this.isFieldDisabled = false;
      }
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
            user.surname,
            user.name,
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

            const currentUrl = this.router.url;
            const routeRoles: { [key: string]: string[] } = {
              '/cart': ['buyer'],
              '/products': ['buyer', 'seller'],
            };
            for (const route in routeRoles) {
              if (currentUrl.startsWith(route)) {
                const required = routeRoles[route];
                if (!required.some(r => this.user.roles.includes(r))) {
                  this.router.navigate(['/home']);
                  break;
                }
              }
            }

            this.togglePopup();
            alert('User updated successfully!');
          },
          error: (error) => {
            console.error('Error updating roles:', error);
            alert('Error updating user roles. Please try again.');
          }
        });
      },
      error: (error) => {
        console.error('Error updating user:', error);

        if (error.status === 409) { // Conflict - email already in use
          alert('This email is already in use by another account. Please use a different email.');
        } else {
          alert('Error updating user data. Please try again.');
        }
      }
    });
  }
}