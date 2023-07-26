import React, { useEffect, useState } from "react";
import { Slider, Button, Box } from "@mui/material";
import {
  VolumeUp,
  VolumeDown,
  PlayArrow,
  Stop,
  VolumeOff,
} from "@mui/icons-material";
import axios from "axios";

import Status from "./Status";
import "../assets/Player.css";
import CustomGridItem from "./CustomGridItem";

const Player = ({ apiUrl, playing, setPlaying }) => {
  const [volume, setVolume] = useState(50); // Estado do volume
  const [lastVolume, setLastVolume] = useState(null); // Estado para armazenar o último volume
  const [volumeChanged, setVolumeChanged] = useState(false);
  const [statusMessage, setStatusMessage] = useState("");

  // Função para alterar o volume
  const handleVolumeChange = (event, newValue) => {
    setVolume(newValue);
    setVolumeChanged(true);
  };

  useEffect(() => {
    if (volumeChanged) {
      axios
        .post(`${apiUrl}/volume?volume=${volume / 100}`)
        .catch((error) => console.log(error));
      setVolumeChanged(false);
    }
  }, [apiUrl, volume, volumeChanged]);

  // Função para iniciar a reprodução
  const handlePlay = () => {
    axios
      .post(`${apiUrl}/play`)
      .then(function (response) {
        const data = response.data;
        handleStatusChange(data.status, data.statusMessage, parseInt(data.volume * 100));
      })
      .catch((error) => console.log(error));
  };

  // Função para parar a reprodução
  const handleStop = () => {
    axios
      .post(`${apiUrl}/stop`)
      .then(function (response) {
        const data = response.data;
        handleStatusChange(data.status, data.statusMessage, parseInt(data.volume * 100));
      })
      .catch((error) => console.log(error));
  };

  // Função para aumentar o volume
  const handleVolumeUp = () => {
    const newVolume = Math.min(volume + 5, 100); // Aumenta o volume em 5 (ou o máximo possível)
    setLastVolume(newVolume); // Atualiza o último volume com o novo volume
    setVolume(newVolume); // Atualiza o estado do volume
    setVolumeChanged(true);
  };

  // Função para diminuir o volume
  const handleVolumeDown = () => {
    const newVolume = Math.max(volume - 5, 0); // Diminui o volume em 5 (ou o mínimo possível)
    setLastVolume(newVolume); // Atualiza o último volume com o novo volume
    setVolume(newVolume); // Atualiza o estado do volume
    setVolumeChanged(true);
  };

  const handleToggleVolume = () => {
    if (volume === 0) {
      setVolume(lastVolume || 50); // Restaura o último volume ou 50 como padrão
    } else {
      setLastVolume(volume); // Armazena o último volume antes de definir como 0
      setVolume(0); // Define o volume como 0
    }
    setVolumeChanged(true);
  };

  const handleStatusChange = (playing, statusMessage, volume) => {
    if (playing !== null) {
      setPlaying(playing);
      setVolume(volume);
    }
    setStatusMessage(statusMessage);
  };

  return (
    <>
      <CustomGridItem xs={10} md={2}>
        <Status
          apiUrl={apiUrl}
          onStatusChange={handleStatusChange}
          playing={playing}
          statusMessage={statusMessage}
        />
      </CustomGridItem>
      <CustomGridItem xs={2} md={2}>
        <Button variant="contained" onClick={playing ? handleStop : handlePlay}>
          {playing ? <Stop /> : <PlayArrow />}
        </Button>
      </CustomGridItem>
      <CustomGridItem xs={2} md={1}>
        <Button variant="contained" onClick={handleVolumeDown}>
          <VolumeDown />
        </Button>
      </CustomGridItem>
      <CustomGridItem xs={2} md={1}>
        <Button variant="contained" onClick={handleVolumeUp}>
          <VolumeUp />
        </Button>
      </CustomGridItem>
      <CustomGridItem xs={3} md={2}>
        <Button variant="outlined" onClick={handleToggleVolume}>
          <VolumeOff color={volume === 0 ? "secondary" : "disabled"} />
          <VolumeUp color={volume !== 0 ? "primary" : "disabled"} />
        </Button>
      </CustomGridItem>
      <CustomGridItem xs={5} md={4}>
        <Box p={3}>
          <Slider
            color={volume !== 0 ? "primary" : "secondary"}
            value={volume}
            onChange={handleVolumeChange}
            defaultValue={50}
            step={5}
            min={0}
            max={100}
          />
        </Box>
      </CustomGridItem>
    </>
  );
};

export default Player;
