import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponent } from './home.component';
import { SharedModule } from '../shared/shared.module';
import { HomeRoutingModule } from './home-routing.module';

@NgModule({
  imports: [
    SharedModule, HomeRoutingModule
  ],
  declarations: [HomeComponent]
})
export class HomeModule { }
