import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { EmailStepComponent } from '../email-step/email-step.component';
import { CodeStepComponent } from '../code-step/code-step.component';
import { ReseatPasswordStepComponent } from './reseat-password-step/reseat-password-step.component';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [EmailStepComponent, CodeStepComponent, ReseatPasswordStepComponent],
  templateUrl: './forgot-password.component.html'
})
export class ForgotPasswordComponent {
  currentStep: 1 | 2 | 3 = 1;
  emailToValidate = '';

  constructor(private router: Router) { }

  onEmailValidated(email: string) {
    this.emailToValidate = email;
    this.currentStep = 2;
  }

  onCodeValidated() {
    this.currentStep = 3;
    alert("CODIGO VALIDADO HERMANO");
  }

  onPasswordReset() {
    this.router.navigate(['/login-user']);
  }
}