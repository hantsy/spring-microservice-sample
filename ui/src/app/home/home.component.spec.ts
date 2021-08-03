// Http testing module and mocking controller
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';
import {
  HttpClient,
  HttpClientModule,
  HttpErrorResponse
} from '@angular/common/http';
import { TestBed, async, inject } from '@angular/core/testing';
import {
  BaseRequestOptions,
  Http,
  Response,
  ResponseOptions
} from '@angular/http';
import { Observable, of, empty } from 'rxjs';
import { HomeComponent } from './home.component';

describe('Component: HomeComponent', () => {
  let comp: HomeComponent;

  it('message contains `Spring Boot 2`', () => {
    comp = new HomeComponent();
    expect(comp.message).toContain('Spring Boot 2');
  });
});
