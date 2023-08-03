import React, { useEffect } from "react";
import axios from "axios";
import "../assets/Status.css";

const StatusLabel = ({ apiUrl, onStatusChange, playing, statusMessage }) => {

  const fetchStatus = async () => {
    try {
      const response = await axios.get(`${apiUrl}/status`);
      const data = response.data;
      onStatusChange(data.status, data.statusMessage, parseInt(data.volume*100), data.timer, data.fadeOut);
      console.log()
    } catch (error) {
      console.error('Ocorreu um erro ao buscar o status:', error);
    }
  };

  // Iniciar o intervalo quando o componente é montado
  useEffect(() => {
    fetchStatus();
    const intervalId = setInterval(fetchStatus, 6000);
    
    // Limpar o intervalo quando o componente é desmontado
    return () => clearInterval(intervalId);

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []); // O array de dependências vazio garante que o useEffect seja executado somente após a montagem inicial


  return (
    <div className={`status-label ${playing ? "recording" : ""}`}>
      <span>{statusMessage}</span>
    </div>
  );
};

export default StatusLabel;
