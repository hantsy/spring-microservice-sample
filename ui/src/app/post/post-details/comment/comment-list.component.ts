import { Component, OnInit, OnChanges, Input } from '@angular/core';

import { Post } from '../../shared/post.model';
import { Comment } from '../../shared/comment.model';


@Component({
  selector: 'app-comment-list',
  templateUrl: './comment-list.component.html',
  styleUrls: ['./comment-list.component.css']
})
export class CommentListComponent implements OnInit, OnChanges {

  @Input() post: Post;
  @Input() comments: Comment[];

  constructor() { }

  ngOnInit() {
    console.log('calling ngOnInit::CommentListComponent...');
  }

  ngOnChanges(changes: any) {
    console.log('calling ngChanges::CommentListComponent...' + JSON.stringify(changes));
    //if (changes['comments'].previousValue != changes['comments'].currentValue) {
     // this.comments = changes['comments'].currentValue;
    //}
  }
}
