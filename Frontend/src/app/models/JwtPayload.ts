export interface JwtPayload {
    sub: string;
    userId: number;
    roles: string[];
    surname: string;
    name: string;
    iat: number;
    exp: number;
  }