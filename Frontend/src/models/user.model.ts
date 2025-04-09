export class User {
    name: string;
    surname: string;
    email: string;
    password: string;
    roles: string[];
  
    constructor(name: string, surname: string, email: string, password: string, roles: string[]) {
      this.name = name;
      this.surname = surname;
      this.email = email;
      this.password = password;
      this.roles = roles;
    }
  }