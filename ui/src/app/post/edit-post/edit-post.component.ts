import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { Post } from '../shared/post.model';
import { PostService } from '../shared/post.service';

@Component({
  selector: 'app-edit-post',
  templateUrl: './edit-post.component.html',
  styleUrls: ['./edit-post.component.css']
})
export class EditPostComponent implements OnInit, OnDestroy {
  post: Post = { title: '', content: '' };
  slug: string;

  constructor(private router: Router, private route: ActivatedRoute) { }

  onPostUpdated(event) {
    console.log('post was updated!' + event);
    if (event) {
      this.router.navigate(['', 'post', 'list']);
    }
  }

  ngOnInit() {
    this.post = this.route.snapshot.data['post'];
  }

  ngOnDestroy() {
  }
}
