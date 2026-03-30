import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE,
  timeout: 300000,
});

export const optimizeCv = async (cvText, jobDescription, modelName = 'qwen2.5:7b') => {
  const response = await api.post('/optimize', { cvText, jobDescription, modelName });
  return response.data;
};

export const parsePdf = async (file, model = 'qwen2.5:7b') => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('model', model);
  const response = await api.post('/parse-pdf', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return response.data;
};

export const healthCheck = async () => {
  const response = await api.get('/health');
  return response.data;
};

export default api;
