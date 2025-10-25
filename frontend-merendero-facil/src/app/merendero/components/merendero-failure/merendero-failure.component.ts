import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-merendero-failure',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './merendero-failure.component.html',
  styleUrl: './merendero-failure.component.css'
})
export class MerenderoFailureComponent {

}
