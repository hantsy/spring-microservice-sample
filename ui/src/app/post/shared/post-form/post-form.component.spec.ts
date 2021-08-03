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
import {
  ComponentFixture,
  TestBed,
  async,
  inject,
  ComponentFixtureAutoDetect,
  tick,
  fakeAsync
} from '@angular/core/testing';
import {
  BaseRequestOptions,
  Http,
  Response,
  ResponseOptions
} from '@angular/http';
import { Observable, of, empty, from, interval, defer } from 'rxjs';
import { PostFormComponent } from './post-form.component';
import { PostService } from '../post.service';
import { Post } from '../post.model';
import { SharedModule } from '../../../shared/shared.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DebugElement, Component } from '@angular/core';
import { By } from '@angular/platform-browser';

const posts = [
  {
    id: '1',
    title: 'Getting started with REST',
    content: 'Content of Getting started with REST',
    createdDate: '9/22/16 4:15 PM'
  },
  {
    id: '2',
    title: 'Getting started with AngularJS 1.x',
    content: 'Content of Getting started with AngularJS 1.x',
    createdDate: '9/22/16 4:15 PM'
  },
  {
    id: '3',
    title: 'Getting started with Angular 2',
    content: 'Content of Getting started with Angular2',
    createdDate: '9/22/16 4:15 PM'
  }
] as Post[];

class MockPostService {
  getPosts(term?: string): Observable<any> {
    return from(posts);
  }
  savePost(post: Post): Observable<any> {
    return empty();
  }
  updatePost(id: string, post: Post): Observable<any> {
    return empty();
  }
}

describe('Component: PostFormComponent', () => {
  let component: PostFormComponent;
  let fixture: ComponentFixture<PostFormComponent>;
  let postService: PostService;

  // Create a fake service object with spies
  const postServiceSpy = jasmine.createSpyObj('PostService', [
    'savePost',
    'updatePost'
  ]);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [BrowserAnimationsModule, SharedModule],
      declarations: [PostFormComponent],
      // provide the component-under-test and dependent service
      providers: [{ provide: PostService, useClass: MockPostService }]
    }).compileComponents();
  }));

  beforeEach(() => {
    postService = TestBed.get(PostService);
    fixture = TestBed.createComponent(PostFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeDefined();
  });

  it('should contain "save" button', () => {
    const buttonElement: HTMLElement = fixture.nativeElement;
    console.log('text content:' + buttonElement.textContent);
    expect(buttonElement.textContent).toContain('save');
  });

  it('should have <button> with "save"', () => {
    const buttonElement: HTMLElement = fixture.nativeElement;
    const p = buttonElement.querySelector('button');
    expect(p.textContent).toContain('save');
  });

  it('should find the <button> with fixture.debugElement.nativeElement)', () => {
    const compDe: DebugElement = fixture.debugElement;
    const compEl: HTMLElement = compDe.nativeElement;
    const p = compEl.querySelector('button');
    expect(p.textContent).toContain('save');
  });

  it('should find the <button> with fixture.debugElement.query(By.css)', () => {
    const compDe: DebugElement = fixture.debugElement;
    const buttonDe = compDe.query(By.css('button'));
    const btn: HTMLElement = buttonDe.nativeElement;
    expect(btn.textContent).toContain('save');
  });
});

describe('Component: PostFormComponent(input & output)', () => {
  let component: PostFormComponent;
  let fixture: ComponentFixture<PostFormComponent>;
  let componentDe: DebugElement;
  let savePostSpy: jasmine.Spy;
  let updatePostSpy: jasmine.Spy;
  // Create a fake service object with spies
  const postServiceSpy = jasmine.createSpyObj('PostService', [
    'savePost',
    'updatePost'
  ]);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BrowserAnimationsModule, SharedModule],
      declarations: [PostFormComponent],
      // provide the component-under-test and dependent service
      providers: [
        //   { provide: ComponentFixtureAutoDetect, useValue: true },
        { provide: PostService, useValue: postServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PostFormComponent);
    component = fixture.componentInstance;
    componentDe = fixture.debugElement;
    fixture.detectChanges();
  });

  afterEach(() => {
    postServiceSpy.savePost.calls.reset();
    postServiceSpy.updatePost.calls.reset();
  });

  it('should raise `saved` event when the form is submitted (triggerEventHandler)', fakeAsync(() => {
    const formData = { title: 'Test title', content: 'Test content' };
    // trigger initial data binding
    component.post = formData;
    let saved = false;
    savePostSpy = postServiceSpy.savePost
      .withArgs(formData)
      .and.returnValue(of({}));
    // Make the spy return a synchronous Observable with the test data
    component.saved.subscribe((data: boolean) => (saved = data));

    // componentDe.triggerEventHandler('submit', null);
    const formElement = componentDe.query(By.css('form#form'));
    formElement.triggerEventHandler('submit', null);
    // component.submit();
    tick();
    fixture.detectChanges();

    expect(saved).toBeTruthy();
    expect(savePostSpy.calls.count()).toBe(1, 'savePost called');
  }));

  it('should raise `saved` event when the form is submitted (triggerEventHandler):failed to save', fakeAsync(() => {
    const formData = { title: 'Test title', content: 'Test content' };
    // trigger initial data binding
    component.post = formData;
    let saved = false;
    savePostSpy = postServiceSpy.savePost
      .withArgs(formData)
      .and.throwError('error');
    // Make the spy return a synchronous Observable with the test data
    component.saved.subscribe((data: boolean) => (saved = data));

    // componentDe.triggerEventHandler('submit', null);
    const formElement = componentDe.query(By.css('form#form'));
    formElement.triggerEventHandler('submit', null);
    // component.submit();
    tick();
    fixture.detectChanges();

    expect(saved).toBeFalsy();
    expect(savePostSpy).toThrowError();
  }));

  it('should raise `saved` event when the form is submitted (triggerEventHandler):update', fakeAsync(() => {
    const formData: Post = {
      id: '1',
      title: 'Test title',
      content: 'Test content'
    };
    // trigger initial data binding
    component.post = formData;
    let saved = false;

    // Make the spy return a synchronous Observable with the test data
    updatePostSpy = postServiceSpy.updatePost
      .withArgs('1', formData)
      .and.returnValue(of({}));

    component.saved.subscribe((data: boolean) => (saved = data));

    // componentDe.triggerEventHandler('submit', null);
    const formElement = componentDe.query(By.css('form#form'));
    formElement.triggerEventHandler('submit', null);
    // component.submit();
    tick();
    fixture.detectChanges();

    expect(saved).toBeTruthy();
    expect(updatePostSpy.calls.count()).toBe(1, 'updatePost called');
  }));

  it('should raise `saved` event when the form is submitted (triggerEventHandler):failed to update', fakeAsync(() => {
    const formData = { id: '1', title: 'Test title', content: 'Test content' };
    // trigger initial data binding
    component.post = formData;
    let saved = false;

    // Make the spy return a synchronous Observable with the test data
    updatePostSpy = postServiceSpy.updatePost
      .withArgs('1', formData)
      .and.throwError('error');

    component.saved.subscribe((data: boolean) => (saved = data));

    // componentDe.triggerEventHandler('submit', null);
    const formElement = componentDe.query(By.css('form#form'));
    formElement.triggerEventHandler('submit', null);
    // component.submit();
    tick();
    fixture.detectChanges();

    expect(saved).toBeFalsy();
    expect(updatePostSpy.calls.count()).toBe(1, 'updatePost called');
    expect(updatePostSpy).toThrowError();
  }));
});
