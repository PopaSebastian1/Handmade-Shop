import { Directive, Input, TemplateRef, ViewContainerRef, OnDestroy } from '@angular/core';
import { UserService } from './services/user-service/user.service';
import { Subscription } from 'rxjs';

@Directive({
  selector: '[appHasRole]'
})
export class HasRoleDirective implements OnDestroy {
  private requiredRoles: string[] = [];
  private hasView = false;
  private userSubscription: Subscription = Subscription.EMPTY;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private userService: UserService
  ) {}

  @Input() set appHasRole(roles: string[] | string) {
    this.requiredRoles = Array.isArray(roles) ? roles : [roles];
    this.updateView();
    
    this.userSubscription.unsubscribe();
    
    this.userSubscription = this.userService.currentUserData.subscribe(() => {
      this.updateView();
    });
  }

  private updateView(): void {
    const hasRole = this.userService.userHasAnyRole(this.requiredRoles);
    
    if (hasRole && !this.hasView) {
      this.viewContainer.createEmbeddedView(this.templateRef);
      this.hasView = true;
    } else if (!hasRole && this.hasView) {
      this.viewContainer.clear();
      this.hasView = false;
    }
  }

  ngOnDestroy(): void {
    this.userSubscription.unsubscribe();
  }
}