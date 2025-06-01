import { IRole } from './role.interface';

export class Role implements IRole {
  constructor(
    public name: string,
    public description: string = '',
    public permissions: string[] = [],
    public id?: number
  ) {}

  static createDefault(): Role {
    return new Role('', '', []);
  }

  static fromDTO(dto: IRole): Role {
    return new Role(
      dto.name,
      dto.description || '',
      dto.permissions || [],
      dto.id
    );
  }

  toDTO(): IRole {
    return {
      id: this.id,
      name: this.name,
      description: this.description,
      permissions: this.permissions
    };
  }

  hasPermission(permission: string): boolean {
    return this.permissions.includes(permission);
  }
}