import { Component } from '@angular/core';
import { EmailStepComponent } from '../email-step/email-step.component';
import { Router } from '@angular/router';
import { CodeStepComponent } from '../code-step/code-step.component';
import { UserDataStepComponent } from './user-data-step/user-data-step.component';

@Component({
  selector: 'app-create-user',
  standalone: true,
  imports: [
    EmailStepComponent, CodeStepComponent, UserDataStepComponent
  ],
  templateUrl: './create-user.component.html'
})
export class CreateUserComponent {
  currentStep: 1 | 2 | 3 = 1;
  emailToValidate = '';

  constructor(private router: Router) {}
  
  // Eventos emitidos por los hijos cuando completen su paso
  onEmailValidated(email: string) {
    this.emailToValidate = email;
    this.currentStep = 2;
  }

  onCodeValidated() {
    this.currentStep = 3;
  }

  onUserRegistered() {
    this.router.navigate(['/login-user']);
  }
}