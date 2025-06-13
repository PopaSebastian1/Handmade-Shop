import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DataService } from '../data.service';
import { Subscription } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../services/user-service/user.service';

interface PersonalInfo {
  name: string;
  phone: string;
}

interface DeliveryAddress {
  street: string;
  number: string;
  bloc: string;
  scara: string;
  etaj: string;
  apartament: string;
  interfon: string;
  city: string;
  sector: string;
  postal: string;
  notes: string;
}

interface PaymentInfo {
  card: string;
  expiry: string;
  cvc: string;
  cardholder: string;
}

interface OrderData {
  personalInfo: PersonalInfo;
  deliveryAddress: DeliveryAddress;
  paymentInfo: PaymentInfo;
  cart: any[];
  orderDate: Date;
  orderNumber: string;
}

@Component({
  selector: 'app-pay',
  templateUrl: './pay.component.html',
  styleUrls: ['./pay.component.css']
})
export class PayComponent implements OnInit, OnDestroy {
  cart: any[] = [];
  cartSubscription: Subscription = new Subscription();
  isProcessing: boolean = false;

  // Form data
  personalInfo: PersonalInfo = {
    name: '',
    phone: ''
  };

  deliveryAddress: DeliveryAddress = {
    street: '',
    number: '',
    bloc: '',
    scara: '',
    etaj: '',
    apartament: '',
    interfon: '',
    city: 'București',
    sector: '',
    postal: '',
    notes: ''
  };

  paymentInfo: PaymentInfo = {
    card: '',
    expiry: '',
    cvc: '',
    cardholder: ''
  };

  constructor(
    private router: Router,
    private snackBar: MatSnackBar,
    private userService: UserService
  ) { }

  ngOnInit() {
    this.setupEventListeners();
    this.setupCardFormatting();
    this.setupValidators();
  }

  ngOnDestroy() {
    this.cartSubscription.unsubscribe();
  }

  private setupEventListeners(): void {
    // Wait for DOM to be ready
    setTimeout(() => {
      const submitBtn = document.getElementById('submitBtn');
      if (submitBtn) {
        submitBtn.addEventListener('click', () => this.onSubmit());
      }
    }, 0);
  }

  private setupCardFormatting(): void {
    setTimeout(() => {
      const cardInput = document.getElementById('card') as HTMLInputElement;
      const expiryInput = document.getElementById('expiry') as HTMLInputElement;
      const cvcInput = document.getElementById('cvc') as HTMLInputElement;

      if (cardInput) {
        cardInput.addEventListener('input', (e) => this.formatCardNumber(e));
      }

      if (expiryInput) {
        expiryInput.addEventListener('input', (e) => this.formatExpiry(e));
      }

      if (cvcInput) {
        cvcInput.addEventListener('input', (e) => this.formatCVC(e));
      }
    }, 0);
  }

  private setupValidators(): void {
    setTimeout(() => {
      // Personal info validators
      this.setupFieldValidator('name', this.validateName.bind(this));
      this.setupFieldValidator('phone', this.validatePhone.bind(this));

      // Address validators
      this.setupFieldValidator('street', this.validateStreet.bind(this));
      this.setupFieldValidator('number', this.validateAddressNumber.bind(this));
      this.setupFieldValidator('bloc', this.validateOptionalBuilding.bind(this));
      this.setupFieldValidator('scara', this.validateOptionalBuilding.bind(this));
      this.setupFieldValidator('etaj', this.validateOptionalFloor.bind(this));
      this.setupFieldValidator('apartament', this.validateOptionalApartment.bind(this));
      this.setupFieldValidator('interfon', this.validateOptionalIntercom.bind(this));
      this.setupFieldValidator('city', this.validateCity.bind(this));
      this.setupFieldValidator('postal', this.validatePostalCode.bind(this));

      // Payment validators
      this.setupFieldValidator('card', this.validateCardNumber.bind(this));
      this.setupFieldValidator('expiry', this.validateExpiry.bind(this));
      this.setupFieldValidator('cvc', this.validateCVC.bind(this));
      this.setupFieldValidator('cardholder', this.validateCardholder.bind(this));
    }, 0);
  }

  private setupFieldValidator(fieldId: string, validator: (value: string) => string | null): void {
    const field = document.getElementById(fieldId) as HTMLInputElement;
    if (!field) return;

    // Add error message container
    let errorContainer = field.parentElement?.querySelector('.error-message') as HTMLElement;
    if (!errorContainer) {
      errorContainer = document.createElement('div');
      errorContainer.className = 'error-message';
      field.parentElement?.appendChild(errorContainer);
    }

    const validateField = () => {
      const error = validator(field.value);
      this.showFieldError(field, errorContainer, error);
    };

    field.addEventListener('blur', validateField);
    field.addEventListener('input', () => {
      // Clear error on input if field was previously invalid
      if (field.classList.contains('error')) {
        setTimeout(validateField, 300); // Debounce validation
      }
    });
  }

  private showFieldError(field: HTMLInputElement, errorContainer: HTMLElement, error: string | null): void {
    if (error) {
      field.classList.add('error');
      field.classList.remove('valid');
      errorContainer.textContent = error;
      errorContainer.style.display = 'block';
    } else {
      field.classList.remove('error');
      field.classList.add('valid');
      errorContainer.style.display = 'none';
    }
  }

  // Personal Information Validators
  private validateName(value: string): string | null {
    if (!value.trim()) return 'Full name is required';
    if (value.trim().length < 2) return 'Name must be at least 2 characters long';
    if (value.trim().length > 50) return 'Name cannot exceed 50 characters';

    // Allow Romanian characters, letters, spaces, hyphens, and apostrophes
    if (!/^[a-zA-ZăâîșțĂÂÎȘȚ\s\-']+$/.test(value)) {
      return 'Name can only contain letters, spaces, hyphens, and apostrophes';
    }

    // Check for at least first and last name
    const nameParts = value.trim().split(/\s+/);
    if (nameParts.length < 2) return 'Please enter both first and last name';

    return null;
  }

  private validatePhone(value: string): string | null {
    if (!value.trim()) return 'Phone number is required';

    // Remove all non-digit characters for validation
    const cleanPhone = value.replace(/[\s\-\(\)\.]/g, '');

    // Romanian phone number validation
    // Accepts: +40xxxxxxxxx, 40xxxxxxxxx, 07xxxxxxxx, 02xxxxxxx (landline)
    const phoneRegex = /^(\+40|40|0)([1-9]\d{8}|[2-3]\d{7})$/;

    if (!phoneRegex.test(cleanPhone)) {
      return 'Please enter a valid Romanian phone number';
    }

    return null;
  }

  // Address Validators
  private validateStreet(value: string): string | null {
    if (!value.trim()) return 'Street address is required';
    if (value.trim().length < 3) return 'Street address must be at least 3 characters';
    if (value.trim().length > 100) return 'Street address cannot exceed 100 characters';

    // Allow letters, numbers, spaces, dots, hyphens, Romanian characters
    if (!/^[a-zA-ZăâîșțĂÂÎȘȚ0-9\s\.\-]+$/.test(value)) {
      return 'Street address contains invalid characters';
    }

    return null;
  }

  private validateAddressNumber(value: string): string | null {
    if (!value.trim()) return 'Address number is required';
    if (value.trim().length > 10) return 'Address number cannot exceed 10 characters';

    // Allow numbers, letters, and common separators
    if (!/^[a-zA-Z0-9\-\/\.]+$/.test(value.trim())) {
      return 'Address number format is invalid';
    }

    return null;
  }

  private validateOptionalBuilding(value: string): string | null {
    if (!value.trim()) return null; // Optional field
    if (value.trim().length > 10) return 'Building info cannot exceed 10 characters';

    // Allow alphanumeric characters, dots, hyphens
    if (!/^[a-zA-Z0-9\.\-]+$/.test(value.trim())) {
      return 'Building info contains invalid characters';
    }

    return null;
  }

  private validateOptionalFloor(value: string): string | null {
    if (!value.trim()) return null; // Optional field
    if (value.trim().length > 5) return 'Floor info cannot exceed 5 characters';

    // Allow numbers, letters, common floor indicators
    if (!/^[a-zA-Z0-9\-]+$/.test(value.trim())) {
      return 'Floor info contains invalid characters';
    }

    return null;
  }

  private validateOptionalApartment(value: string): string | null {
    if (!value.trim()) return null; // Optional field
    if (value.trim().length > 10) return 'Apartment info cannot exceed 10 characters';

    // Allow alphanumeric characters and common separators
    if (!/^[a-zA-Z0-9\-\/]+$/.test(value.trim())) {
      return 'Apartment info contains invalid characters';
    }

    return null;
  }

  private validateOptionalIntercom(value: string): string | null {
    if (!value.trim()) return null; // Optional field
    if (value.trim().length > 20) return 'Intercom info cannot exceed 20 characters';

    // Allow alphanumeric characters, spaces, and common separators
    if (!/^[a-zA-Z0-9\s\-\.\,]+$/.test(value.trim())) {
      return 'Intercom info contains invalid characters';
    }

    return null;
  }

  private validateCity(value: string): string | null {
    if (!value.trim()) return 'City is required';
    if (value.trim().length < 2) return 'City name must be at least 2 characters';
    if (value.trim().length > 50) return 'City name cannot exceed 50 characters';

    // Allow letters, spaces, hyphens, Romanian characters
    if (!/^[a-zA-ZăâîșțĂÂÎȘȚ\s\-]+$/.test(value)) {
      return 'City name contains invalid characters';
    }

    return null;
  }

  private validatePostalCode(value: string): string | null {
    if (!value.trim()) return null; // Optional field

    // Romanian postal codes are 6 digits
    if (!/^\d{6}$/.test(value.trim())) {
      return 'Postal code must be exactly 6 digits';
    }

    return null;
  }

  // Payment Information Validators
  private validateCardNumber(value: string): string | null {
    if (!value.trim()) return 'Card number is required';

    const cleanCard = value.replace(/\s/g, '');

    // Check if it contains only digits
    if (!/^\d+$/.test(cleanCard)) return 'Card number must contain only digits';

    // Check length (most cards are 16 digits, some are 15 or 13)
    if (cleanCard.length < 13 || cleanCard.length > 19) {
      return 'Card number must be between 13 and 19 digits';
    }

    // Luhn algorithm validation
    if (!this.luhnCheck(cleanCard)) return 'Invalid card number';

    // Card type validation
    const cardType = this.getCardType(cleanCard);
    if (!cardType) return 'Unsupported card type';

    return null;
  }

  private validateExpiry(value: string): string | null {
    if (!value.trim()) return 'Expiry date is required';

    // Check format MM/YY
    if (!/^\d{2}\/\d{2}$/.test(value)) return 'Format should be MM/YY';

    const [month, year] = value.split('/').map(Number);

    // Validate month
    if (month < 1 || month > 12) return 'Invalid month (01-12)';

    // Validate year and check if card is expired
    const currentDate = new Date();
    const currentYear = currentDate.getFullYear() % 100;
    const currentMonth = currentDate.getMonth() + 1;

    if (year < currentYear || (year === currentYear && month < currentMonth)) {
      return 'Card has expired';
    }

    // Check if expiry is too far in the future (more than 10 years)
    if (year > currentYear + 10) return 'Expiry date seems invalid';

    return null;
  }

  private validateCVC(value: string): string | null {
    if (!value.trim()) return 'CVC is required';

    // Check if it contains only digits
    if (!/^\d+$/.test(value)) return 'CVC must contain only digits';

    // Most cards use 3 digits, American Express uses 4
    if (value.length < 3 || value.length > 4) {
      return 'CVC must be 3 or 4 digits';
    }

    return null;
  }

  private validateCardholder(value: string): string | null {
    if (!value.trim()) return 'Cardholder name is required';
    if (value.trim().length < 2) return 'Cardholder name must be at least 2 characters';
    if (value.trim().length > 50) return 'Cardholder name cannot exceed 50 characters';

    // Allow letters, spaces, hyphens, apostrophes, and dots (for initials)
    if (!/^[a-zA-ZăâîșțĂÂÎȘȚ\s\-'\.]+$/.test(value)) {
      return 'Cardholder name contains invalid characters';
    }

    // Check for minimum parts (first and last name)
    const nameParts = value.trim().split(/\s+/);
    if (nameParts.length < 2) return 'Please enter full cardholder name';

    return null;
  }

  // Helper methods for card validation
  private getCardType(cardNumber: string): string | null {
    // Visa
    if (/^4/.test(cardNumber)) return 'visa';
    // Mastercard
    if (/^5[1-5]/.test(cardNumber) || /^2[2-7]/.test(cardNumber)) return 'mastercard';
    // American Express
    if (/^3[47]/.test(cardNumber)) return 'amex';
    // Discover
    if (/^6(?:011|5)/.test(cardNumber)) return 'discover';
    // Diners Club
    if (/^3[0689]/.test(cardNumber)) return 'diners';
    // JCB
    if (/^35/.test(cardNumber)) return 'jcb';

    return null;
  }

  // Luhn algorithm for card validation
  private luhnCheck(cardNumber: string): boolean {
    let sum = 0;
    let alternate = false;

    for (let i = cardNumber.length - 1; i >= 0; i--) {
      let n = parseInt(cardNumber.charAt(i), 10);

      if (alternate) {
        n *= 2;
        if (n > 9) n = (n % 10) + 1;
      }

      sum += n;
      alternate = !alternate;
    }

    return (sum % 10) === 0;
  }

  private formatCardNumber(event: Event): void {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\s/g, '').replace(/[^0-9]/gi, '');
    let formattedValue = value.match(/.{1,4}/g)?.join(' ') || '';

    if (formattedValue.length > 19) {
      formattedValue = formattedValue.substring(0, 19);
    }

    input.value = formattedValue;
    this.paymentInfo.card = formattedValue;
  }

  private formatExpiry(event: Event): void {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, '');

    if (value.length >= 2) {
      value = value.substring(0, 2) + '/' + value.substring(2, 4);
    }

    input.value = value;
    this.paymentInfo.expiry = value;
  }

  private formatCVC(event: Event): void {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, '');

    if (value.length > 4) {
      value = value.substring(0, 4);
    }

    input.value = value;
    this.paymentInfo.cvc = value;
  }

  private collectFormData(): OrderData {
    this.personalInfo.name = (document.getElementById('name') as HTMLInputElement)?.value || '';
    this.personalInfo.phone = (document.getElementById('phone') as HTMLInputElement)?.value || '';

    this.deliveryAddress.street = (document.getElementById('street') as HTMLInputElement)?.value || '';
    this.deliveryAddress.number = (document.getElementById('number') as HTMLInputElement)?.value || '';
    this.deliveryAddress.bloc = (document.getElementById('bloc') as HTMLInputElement)?.value || '';
    this.deliveryAddress.scara = (document.getElementById('scara') as HTMLInputElement)?.value || '';
    this.deliveryAddress.etaj = (document.getElementById('etaj') as HTMLInputElement)?.value || '';
    this.deliveryAddress.apartament = (document.getElementById('apartament') as HTMLInputElement)?.value || '';
    this.deliveryAddress.interfon = (document.getElementById('interfon') as HTMLInputElement)?.value || '';
    this.deliveryAddress.city = (document.getElementById('city') as HTMLInputElement)?.value || '';
    this.deliveryAddress.sector = (document.getElementById('sector') as HTMLSelectElement)?.value || '';
    this.deliveryAddress.postal = (document.getElementById('postal') as HTMLInputElement)?.value || '';
    this.deliveryAddress.notes = (document.getElementById('notes') as HTMLTextAreaElement)?.value || '';

    this.paymentInfo.card = (document.getElementById('card') as HTMLInputElement)?.value || '';
    this.paymentInfo.expiry = (document.getElementById('expiry') as HTMLInputElement)?.value || '';
    this.paymentInfo.cvc = (document.getElementById('cvc') as HTMLInputElement)?.value || '';
    this.paymentInfo.cardholder = (document.getElementById('cardholder') as HTMLInputElement)?.value || '';

    return {
      personalInfo: this.personalInfo,
      deliveryAddress: this.deliveryAddress,
      paymentInfo: this.paymentInfo,
      cart: this.cart,
      orderDate: new Date(),
      orderNumber: this.generateOrderNumber()
    };
  }

  private generateOrderNumber(): string {
    const timestamp = Date.now();
    const random = Math.floor(Math.random() * 1000);
    return `ORD-${timestamp}-${random}`;
  }

  private validateForm(orderData: OrderData): string[] {
    const errors: string[] = [];

    const validations = [
      { value: orderData.personalInfo.name, validator: this.validateName.bind(this), field: 'Full Name' },
      { value: orderData.personalInfo.phone, validator: this.validatePhone.bind(this), field: 'Phone' },
      { value: orderData.deliveryAddress.street, validator: this.validateStreet.bind(this), field: 'Street' },
      { value: orderData.deliveryAddress.number, validator: this.validateAddressNumber.bind(this), field: 'Address Number' },
      { value: orderData.deliveryAddress.city, validator: this.validateCity.bind(this), field: 'City' },
      { value: orderData.paymentInfo.card, validator: this.validateCardNumber.bind(this), field: 'Card Number' },
      { value: orderData.paymentInfo.expiry, validator: this.validateExpiry.bind(this), field: 'Expiry Date' },
      { value: orderData.paymentInfo.cvc, validator: this.validateCVC.bind(this), field: 'CVC' },
      { value: orderData.paymentInfo.cardholder, validator: this.validateCardholder.bind(this), field: 'Cardholder Name' }
    ];

    validations.forEach(({ value, validator, field }) => {
      const error = validator(value);
      if (error) {
        errors.push(`${field}: ${error}`);
      }
    });

    const optionalValidations = [
      { value: orderData.deliveryAddress.bloc, validator: this.validateOptionalBuilding.bind(this), field: 'Building' },
      { value: orderData.deliveryAddress.scara, validator: this.validateOptionalBuilding.bind(this), field: 'Entrance' },
      { value: orderData.deliveryAddress.etaj, validator: this.validateOptionalFloor.bind(this), field: 'Floor' },
      { value: orderData.deliveryAddress.apartament, validator: this.validateOptionalApartment.bind(this), field: 'Apartment' },
      { value: orderData.deliveryAddress.interfon, validator: this.validateOptionalIntercom.bind(this), field: 'Intercom' },
      { value: orderData.deliveryAddress.postal, validator: this.validatePostalCode.bind(this), field: 'Postal Code' }
    ];

    optionalValidations.forEach(({ value, validator, field }) => {
      if (value.trim()) {
        const error = validator(value);
        if (error) {
          errors.push(`${field}: ${error}`);
        }
      }
    });

    return errors;
  }

  async onSubmit() {
    const orderData = this.collectFormData();

    try {
      const response = await fetch('https://8hic370xid.execute-api.us-east-1.amazonaws.com/dev/generate-pdf', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(orderData)
      });

      if (!response.ok) throw new Error('API error');

      const data = await response.json();
      const pdfUrl = data.downloadUrl;
      // Apelează processOrder înainte de a deschide PDF-ul
      const currentUser = this.userService.getCurrentUser();
      if (currentUser && currentUser.id) {
        this.userService.processOrder(currentUser.id).subscribe({
          next: () => {
            if (pdfUrl) {
              window.open(pdfUrl, '_blank');
            }
          },
          error: (err) => {
            this.snackBar.open('Eroare la procesarea comenzii.', 'Close', {
              duration: 5000,
              panelClass: ['error-snackbar']
            });
          }
        });
      } else {
        if (pdfUrl) {
          window.open(pdfUrl, '_blank');
        }
      }

      setTimeout(() => {
        this.router.navigate(['/home']);
      }, 2000);

    } catch (err) {
      console.error(err);
      this.snackBar.open('Eroare at PDF generation.', 'Close', {
        duration: 5000,
        panelClass: ['error-snackbar']
      });
    }
  }

  private highlightErrorFields(): void {
    const errorFields = document.querySelectorAll('input.error, select.error, textarea.error');
    if (errorFields.length > 0) {
      (errorFields[0] as HTMLElement).scrollIntoView({ behavior: 'smooth', block: 'center' });
      (errorFields[0] as HTMLElement).focus();
    }
  }

  private async processPayment(orderData: OrderData): Promise<void> {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const isSuccess = Math.random() > 0.1;

        if (isSuccess) {
          resolve();
        } else {
          reject(new Error('Payment declined'));
        }
      }, 2000);
    });
  }

  public isFormValid(): boolean {
    const orderData = this.collectFormData();
    const validationErrors = this.validateForm(orderData);
    return validationErrors.length === 0;
  }
}