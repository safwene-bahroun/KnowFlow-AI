import { Component } from '@angular/core';
import { Login } from "./login/login";
import { Registration } from "./registration/registration";

@Component({
  selector: 'app-authentication',
  imports: [Login, Registration],
  templateUrl: './authentication.html',
  styleUrl: './authentication.css',
})
export class Authentication {
  isLogin = true;

}
