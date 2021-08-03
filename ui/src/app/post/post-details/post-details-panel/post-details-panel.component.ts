import { Component, OnInit, OnChanges, Input, SimpleChange } from '@angular/core';

import { Post } from '../../shared/post.model';

@Component({
  selector: 'app-post-details-panel',
  templateUrl: './post-details-panel.component.html',
  styleUrls: ['./post-details-panel.component.css']
})
export class PostDetailsPanelComponent implements OnInit, OnChanges {

  @Input() post: Post;

  constructor() { }

  ngOnInit() {
  }

  ngOnChanges(changes: { [propertyName: string]: SimpleChange }) {

    // if (changes && changes['post'] && changes['post'].currentValue) {
    //   this.post = Object.assign({}, changes['post'].currentValue);
    // }
    // for (let propName in changes) {
    //   let chng = changes[propName];
    //   let cur = JSON.stringify(chng.currentValue);
    //   let prev = JSON.stringify(chng.previousValue);

    //   console.log(`${propName}: currentValue = ${cur}, previousValue = ${prev}`);
    // }
  }

}
