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

export const authService = {
  login: async (credentials: loginCredentials) => {
    const response = await apiService.post("/auth/login", credentials);
    return response.data; 
  },
  signup: async (userData: signupData) => {
    const response = await apiService.post("/auth/signup", userData);
    return response.data;
  }
};