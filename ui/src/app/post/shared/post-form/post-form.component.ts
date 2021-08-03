import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { Post } from '../post.model';
import { PostService } from '../post.service';

@Component({
  selector: 'app-post-form',
  templateUrl: './post-form.component.html',
  styleUrls: ['./post-form.component.css']
})
export class PostFormComponent implements OnInit, OnDestroy {
  @Input() post: Post = { title: '', content: '' };
  @Output() saved: EventEmitter<boolean> = new EventEmitter<boolean>();
  sub: Subscription;

  constructor(private postService: PostService) {}

  submit() {
    const _body = {
      title: this.post.title,
      content: this.post.content
    } as Post;

    if (this.post.id) {
      this.postService.updatePost(this.post.id, _body).subscribe(
        data => {
          this.saved.emit(true);
        },
        error => {
          this.saved.emit(false);
        }
      );
    } else {
      this.postService.savePost(_body).subscribe(
        data => {
          this.saved.emit(true);
        },
        error => {
          this.saved.emit(false);
        }
      );
    }
  }

  ngOnInit() {
    console.log('calling ngOnInit::PostFormComponent...');
  }

  ngOnDestroy() {
    console.log('calling ngOnDestroy::PostFormComponent...');
    if (this.sub) {
      this.sub.unsubscribe();
    }
  }
}
