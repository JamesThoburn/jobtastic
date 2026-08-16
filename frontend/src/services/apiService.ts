import axios from "axios";

const apiService = axios.create({
    baseURL: "/api/v1",
    withCredentials: true
});

export default apiService;