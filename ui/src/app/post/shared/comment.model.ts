import { Username } from './username.model';
export interface Comment {
  id?: string;
  content: string;
  author?: Username;
  createdDate?: string;
}
