import { Username } from './username.model';
export interface Post {
  id?: string;
  slug?: string;
  title: string;
  content: string;
  author?: Username;
  createdDate?: any;
}

