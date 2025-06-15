export interface JwtPayload {
    sub: string;
    userId: number;
    roles: string[];
    surname: string;
    name: string;
    clientId: string;
    iat: number;
    exp: number;
  }