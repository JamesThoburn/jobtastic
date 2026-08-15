import axios from "axios";

const apiService = axios.create({
    baseURL: import.meta.env.VITE_API_URL || "/api/v1",
    withCredentials: true
});

export default apiService;