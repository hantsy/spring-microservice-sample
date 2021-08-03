import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PostDetailsComponent } from './post-details/post-details.component';
import { AuthGuard } from '../core/auth-guard';
import { NewPostComponent } from './new-post/new-post.component';
import { EditPostComponent } from './edit-post/edit-post.component';
import { PostListComponent } from './post-list/post-list.component';
import { PostDetailsResolve } from './shared/post-details-resolve';

const routes: Routes = [
  { path: '', redirectTo: 'list' },
  { path: 'list', component: PostListComponent },
  { path: 'new', component: NewPostComponent, canActivate: [AuthGuard] },
  {
    path: 'edit/:slug',
    component: EditPostComponent,
    canActivate: [AuthGuard],
    resolve: {
      post: PostDetailsResolve
    }
  },
  { path: 'view/:slug', component: PostDetailsComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PostRoutingModule {}
