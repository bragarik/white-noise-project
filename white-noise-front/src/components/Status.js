import React, { useEffect } from "react";
import axios from "axios";
import "../assets/Status.css";

const StatusLabel = ({ apiUrl, onStatusChange, playing, statusMessage }) => {
  const API_URL = apiUrl;

  useEffect(() => {
    const fetchStatus = async () => {
      try {
        const response = await axios.get(`${API_URL}/status`);
        const data = response.data;
        onStatusChange(data.status, data.statusMessage, parseInt(data.volume*100));
      } catch (error) {
        onStatusChange(null, "Erro!", null);
        console.error("Ocorreu um erro ao buscar o status:", error);
      }
    };
    
    fetchStatus();
    const intervalId = setInterval(fetchStatus, 8000); // Consulta o status a cada 8 segundos

    // Limpa o intervalo quando o componente é desmontado
    return () => clearInterval(intervalId);
  }, [API_URL, onStatusChange]);

  return (
    <div className={`status-label ${playing ? "recording" : ""}`}>
      <span>{statusMessage}</span>
    </div>
  );
};

export default StatusLabel;
