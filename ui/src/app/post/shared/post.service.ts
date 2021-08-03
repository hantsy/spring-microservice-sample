import { Injectable, Inject } from '@angular/core';
import { Post } from './post.model';
import { Comment } from './comment.model';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { HttpParams } from '@angular/common/http';

@Injectable()
export class PostService {
  apiUrl = environment.baseApiUrl + '/posts';
  constructor(private http: HttpClient) {}

  getPosts(term?: any): Observable<any> {
    const params: HttpParams = new HttpParams();
    if (term) {
      Object.keys(term).map(key => {
        if (term[key]) {
          params.set(key, term[key]);
        }
      });
    }
    return this.http.get(`${this.apiUrl}`, { params });
  }

  getPost(id: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  savePost(data: Post): Observable<any> {
    console.log('saving post:' + data);
    return this.http.post(`${this.apiUrl}`, data);
  }

  updatePost(id: string, data: Post): Observable<any> {
    console.log('updating post:' + data);
    return this.http.put(`${this.apiUrl}/${id}`, data);
  }

  deletePost(id: string): Observable<any> {
    console.log('delete post by id:' + id);
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  saveComment(id: string, data: Comment): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/comments`, data);
  }

  getCommentsOfPost(id: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}/comments`);
  }
}
