import { IProduct } from './product.interface';

export class Product implements IProduct {
  constructor(
    public name: string,
    public price: number,
    public quantity: number,
    public description: string,
    public rating: number,
    public image: string,
    public id?: number
  ) {}

  static createDefault(): Product {
    return new Product('', 0, 0, '', 0, '');
  }

  toSafeObject(): Omit<IProduct, 'id'> {
    const { id, ...safeProduct } = this;
    return safeProduct;
  }
}
