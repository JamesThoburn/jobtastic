import apiService from "./apiService"

export const authService = {
  login: async (credentials: any) => {
    const response = await apiService.post("/auth/login", credentials);
    return response.data; 
  },
  signup: async (userData: any) => {
    const response = await apiService.post("/auth/signup", userData);
    return response.data;
  }
};