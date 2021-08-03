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
import { Observable } from 'rxjs';

import { PostService } from './post.service';
import { Post } from './post.model';
import { HttpErrorHandler } from '../../http-error-handler.service';
import { MessageService } from '../../message.service';

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

describe('Service: Post', () => {
  let postService: PostService;

  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      // Import the HttpClient mocking services
      imports: [HttpClientTestingModule],
      // Provide the service-under-test and its dependencies
      providers: [PostService, HttpErrorHandler, MessageService]
    });

    // Inject the http, test controller, and service-under-test
    // as they will be referenced by each test.
    httpClient = TestBed.get(HttpClient);
    httpTestingController = TestBed.get(HttpTestingController);
    postService = TestBed.get(PostService);
  });

  afterEach(() => {
    // After every test, assert that there are no more pending requests.
    httpTestingController.verify();
  });

  it('should not be null...', () => {
    expect(postService).toBeTruthy();
  });

  describe('#Get All Posts', () => {
    let expectedPosts: Post[];

    beforeEach(() => {
      expectedPosts = posts;
      postService = TestBed.get(PostService);
    });

    it('should get posts...', () => {
      postService
        .getPosts()
        .subscribe(
          res =>
            expect(res).toEqual(expectedPosts, 'should return exptected posts'),
          fail
        );

      // PostService should have made one request to GET data from expected URL
      const req = httpTestingController.expectOne(postService.apiUrl);
      expect(req.request.method).toEqual('GET');

      // Respond with the mock posts
      req.flush(expectedPosts);
    });

    it('should be OK returning no posts', () => {
      postService
        .getPosts()
        .subscribe(
          res => expect(res.length).toEqual(0, 'should have empty posts array'),
          fail
        );

      const req = httpTestingController.expectOne(postService.apiUrl);
      req.flush([]); // Respond with no posts
    });

    it('should return expected posts (called multiple times)', () => {
      postService.getPosts().subscribe();
      postService.getPosts().subscribe();
      postService
        .getPosts()
        .subscribe(
          data =>
            expect(data).toEqual(expectedPosts, 'should return expected data'),
          fail
        );

      const requests = httpTestingController.match(postService.apiUrl);
      expect(requests.length).toEqual(3, 'calls to getPosts()');

      // Respond to each request with different mock hero results
      requests[0].flush([]);
      requests[1].flush([
        {
          id: '1',
          title: 'Getting started with REST',
          content: 'Content of Getting started with REST',
          createdDate: '9/22/16 4:15 PM'
        }
      ]);
      requests[2].flush(expectedPosts);
    });
  });

  describe('#Get a Post', () => {
    let expectedPost: Post;

    beforeEach(() => {
      expectedPost = posts[0];
      postService = TestBed.get(PostService);
    });

    it('should get post...', () => {
      postService
        .getPost('1')
        .subscribe(
          res =>
            expect(res).toEqual(expectedPost, 'should return exptected post'),
          fail
        );

      // PostService should have made one request to GET data from expected URL
      const req = httpTestingController.expectOne(postService.apiUrl + '/1');
      expect(req.request.method).toEqual('GET');

      // Respond with the mock posts
      req.flush(expectedPost);
    });

    // This service reports the error but finds a way to let the app keep going.
    it('should return 404 if the post is not found', () => {
      postService
        .getPost('notfound')
        .subscribe(
          data => console.log(data),
          error => expect(error.status === 404)
        );

      const req = httpTestingController.expectOne(
        postService.apiUrl + '/notfound'
      );

      // respond with a 404 and the error message in the body
      const msg = 'deliberate 404 error';
      req.flush(msg, { status: 404, statusText: 'Not Found' });
    });
  });

  describe('#Create a Post', () => {
    let expectedPost: Post;

    beforeEach(() => {
      expectedPost = posts[0];
      postService = TestBed.get(PostService);
    });

    it('should save post...', () => {
      postService
        .savePost(expectedPost)
        .subscribe(
          res =>
            expect(res).toEqual(expectedPost, 'should return exptected post'),
          fail
        );

      // PostService should have made one request to GET data from expected URL
      const req = httpTestingController.expectOne(postService.apiUrl);
      expect(req.request.method).toEqual('POST');

      // Respond with the mock posts
      req.flush(expectedPost);
    });
  });

  describe('#Update a Post', () => {
    let expectedPost: Post;

    beforeEach(() => {
      expectedPost = posts[0];
      postService = TestBed.get(PostService);
    });

    it('should update post...', () => {
      postService
        .updatePost('1', expectedPost)
        .subscribe(
          res => expect(res).toEqual({}, 'should return exptected post'),
          fail
        );

      // PostService should have made one request to GET data from expected URL
      const req = httpTestingController.expectOne(postService.apiUrl + '/1');
      expect(req.request.method).toEqual('PUT');

      // Respond with the mock posts
      req.flush({});
    });

    it('should return 404 if the post is not found', () => {
      postService
        .updatePost('notfound', expectedPost)
        .subscribe(
          data => console.log(data),
          error => expect(error.status === 404)
        );

      const req = httpTestingController.expectOne(
        postService.apiUrl + '/notfound'
      );

      // respond with a 404 and the error message in the body
      const msg = 'deliberate 404 error';
      req.flush(msg, { status: 404, statusText: 'Not Found' });
    });
  });

  describe('#Delete a Post', () => {
    let expectedPost: Post;

    beforeEach(() => {
      expectedPost = posts[0];
      postService = TestBed.get(PostService);
    });

    it('should delete post...', () => {
      postService
        .deletePost('1')
        .subscribe(
          res =>
            expect(res).toEqual(
              {},
              'should return nothing if it is deleted successful'
            ),
          fail
        );

      // PostService should have made one request to GET data from expected URL
      const req = httpTestingController.expectOne(postService.apiUrl + '/1');
      expect(req.request.method).toEqual('DELETE');

      // Respond with the mock posts
      req.flush({});
    });

    it('should return 404 if the post is not found', () => {
      postService
        .deletePost('notfound')
        .subscribe(
          data => console.log(data),
          error => expect(error.status === 404)
        );

      const req = httpTestingController.expectOne(
        postService.apiUrl + '/notfound'
      );

      // respond with a 404 and the error message in the body
      const msg = 'deliberate 404 error';
      req.flush(msg, { status: 404, statusText: 'Not Found' });
    });
  });
});
