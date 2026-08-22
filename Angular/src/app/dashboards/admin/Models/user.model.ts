export type Role =
  | 'ADMIN'
  | 'MANAGER'
  | 'EMPLOYEE';

export type Gender =
  | 'MALE'
  | 'FEMALE';

export type EmployeeProfile =
  | 'INTERN'
  | 'JUNIOR'
  | 'SENIOR'
  | 'TECH_LEAD'
  | 'PROJECT_MANAGER'
  | 'DEPARTMENT_MANAGER'
  | 'OTHER';

export interface Department {
  id: number;
  name: string;
  description?: string;
}

export interface User {
  id?: number;
  name: string;
  familyName: string;
  email: string;
  password?: string;
  cin?: string;
  phoneNumber?: string;
  urlImage?: string;
  address?: string;
  age?: number;
  gender?: Gender | string;
  role: Role;
  employeeProfile?: EmployeeProfile | string;
  enabled?: boolean;
  department?: Department;
  createdAt?: string;      // or Date
  modifiedAt?: string;     // or Date
}