import { IUser } from "./user.interface";

export class User implements IUser {
  constructor(
    public name: string,
    public surname: string,
    public email: string,
    public roles: string[] = [''],
    public password?: string,
    public clientId?: string,
    public clientSecret?: string,
    public id?: number 
  ) {}

  static createDefault(): User {
    return new User('', '', '', ['']);
  }

  get fullName(): string {
    return `${this.name} ${this.surname}`;
  }

  toSafeObject(): Omit<IUser, 'password' | 'clientSecret'> {
    const { password, clientSecret, ...safeUser } = this;
    return safeUser;
  }
}