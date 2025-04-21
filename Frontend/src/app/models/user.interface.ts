export interface IUser {
    id?: number;              
    name: string;
    surname: string;
    email: string;
    password?: string;        
    roles: string[];
    clientId?: string;        
    clientSecret?: string;    
  }