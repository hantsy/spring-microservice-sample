import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { Observable, Subscription, forkJoin } from 'rxjs';
import { flatMap } from 'rxjs/operators';
import { PostService } from '../shared/post.service';
import { Post } from '../shared/post.model';
import { Comment } from '../shared/comment.model';

@Component({
  selector: 'app-post-details',
  templateUrl: './post-details.component.html',
  styleUrls: ['./post-details.component.css']
})
export class PostDetailsComponent implements OnInit, OnDestroy {

  slug: string;
  post: Post = { title: '', content: '' };
  comments: Comment[] = [];
  sub: Subscription;

  constructor(
    private postService: PostService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    console.log('calling ngOnInit::PostDetailsComponent... ');
    this.sub = this.route.params
      .pipe(
        flatMap(params => {
          this.slug = params['slug'];
          return forkJoin(this.postService.getPost(this.slug), this.postService.getCommentsOfPost(this.slug));
        })
      )
      .subscribe((res: Array<any>) => {
        console.log(res);
        this.post = res[0];
        this.comments = res[1];
        console.log(this.post);
        console.log(this.comments);
      });

    // this.sub = this.route.params
    //   .flatMap(params => this.postService.getPost(params['slug']))
    //   .subscribe(
    //     (data) => this.post = data,
    //     (err) => console.log(err)
    //   );
  }

  ngOnDestroy() {
    if (this.sub) { this.sub.unsubscribe(); }
  }

}
