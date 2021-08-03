import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot } from '@angular/router';
import { PostService } from './post.service';
import { Post } from './post.model';

@Injectable()
export class PostDetailsResolve implements Resolve<Post> {

  constructor(private postService: PostService) {}

  resolve(route: ActivatedRouteSnapshot) {
    return this.postService.getPost(route.paramMap.get('slug'));
  }
}
