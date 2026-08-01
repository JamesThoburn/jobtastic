import apiService from "./apiService"

interface loginCredentials {
  email?: string;
  password?: string;
}

interface signupData {
  firstName?: string;
  lastName?: string;
  email?: string;
  password?: string;
}

interface forgotPasswordData {
  email?: string;
}

interface resetPasswordData {
  token?: string;
  newPassword?: string;
}

export const authService = {
  login: async (credentials: loginCredentials) => {
    const response = await apiService.post("/auth/login", credentials);
    return response.data; 
  },
  signup: async (userData: signupData) => {
    const response = await apiService.post("/auth/signup", userData);
    return response.data;
  },
  forgotPassword: async (data: forgotPasswordData) => {
    const response = await apiService.post("/auth/forgot-password", data);
    return response.data;
  },
  resetPassword: async (data: resetPasswordData) => {
    const response = await apiService.post("/auth/reset-password", data);
    return response.data;
  }
};