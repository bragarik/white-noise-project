import React, { useEffect, useRef, useState } from "react";
import {
  Slider,
  Button,
  Box,
  Divider,
  Typography,
  ButtonGroup,
} from "@mui/material";
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
import Temporizador from "./Temporizador";

const Player = ({ apiUrl, playing, setPlaying }) => {
  const [volume, setVolume] = useState(null); // Estado do volume
  const [lastVolume, setLastVolume] = useState(null); // Estado para armazenar o último volume
  const volumeChanged = useRef(false);
  const [volumeComponent, setVolumeComponent] = useState(lastVolume);
  const [statusMessage, setStatusMessage] = useState("");
  const [timeInSeconds, setTimeInSeconds] = useState(0);
  const [timerRunning, setTimerRunning] = useState(false);
  const [fadeOutAjusted, setFadeOutAjusted] = useState(false);

  useEffect(() => {
    setVolume(parseInt(localStorage.getItem("volume")));
  }, []);

  useEffect(() => {
    if (volumeChanged.current) {
      axios
        .post(`${apiUrl}/volume?volume=${volume / 100}`)
        .catch((error) => console.log(error))
        .finally(() => (volumeChanged.current = false));
    }
  }, [apiUrl, volume]);

  // Função para iniciar a reprodução
  const handlePlay = () => {
    axios
      .post(`${apiUrl}/play`)
      .then(function (response) {
        const data = response.data;
        handleStatusChange(
          data.status,
          data.statusMessage,
          parseInt(data.volume * 100),
          data.timer,
          data.fadeOut
        );
      })
      .catch((error) => console.log(error));
  };

  // Função para parar a reprodução
  const handleStop = () => {
    axios
      .post(`${apiUrl}/stop`)
      .then(function (response) {
        const data = response.data;
        handleStatusChange(
          data.status,
          data.statusMessage,
          parseInt(data.volume * 100),
          data.timer,
          data.fadeOut
        );
      })
      .catch((error) => console.log(error));
  };

  // Função para aumentar o volume
  const handleVolumeUp = () => {
    const newVolume = Math.min(volume + 1, 100);
    handleVolumeChange(newVolume);
    setVolumeComponent(newVolume);
  };

  // Função para diminuir o volume
  const handleVolumeDown = () => {
    const newVolume = Math.max(volume - 1, 0);
    handleVolumeChange(newVolume);
    setVolumeComponent(newVolume)
  };

  const handleToggleVolume = () => {
    if (volume === 0) {
      handleVolumeChange(lastVolume || 50); // Restaura o último volume ou 50 como padrão
      setVolumeComponent(lastVolume || 50);
    } else {
      handleVolumeChange(0); 
      setVolumeComponent(0);
    }
  };

  const handleStatusChange = (
    playing,
    statusMessage,
    volume,
    timer,
    fadeOut
  ) => {
    setPlaying(playing);
    if (!volumeChanged.current) {
      setVolume(volume);
      setVolumeComponent(volume);
    }
    setStatusMessage(statusMessage);

    setTimerRunning(timer.on);
    if (timer.on) {
      setTimeInSeconds(timer.remainingSeconds);
    }

    setFadeOutAjusted(fadeOut);
  };

  const handleFadeOut = (value) => {
    axios
      .post(`${apiUrl}/fadeOut?fadeOut=${value}`)
      .catch((error) => console.log(error))
      .finally(() => setFadeOutAjusted(value));
  };

  // Função para alterar o volume
  const handleVolumeChange = (newValue) => {
    setLastVolume(volume);
    setVolume(newValue);
    volumeChanged.current = true;
    localStorage.setItem("volume", newValue);
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
      <CustomGridItem xs={12} md={3}>
        <ButtonGroup variant="contained" size="large">
          <Button variant="contained" onClick={handleVolumeDown}>
            <VolumeDown />
          </Button>
          <Button variant="contained" onClick={handleVolumeUp}>
            <VolumeUp /> 
          </Button>
          <Button variant="outlined" onClick={handleToggleVolume}>
            <VolumeOff color={volume === 0 ? "secondary" : "disabled"} />
            <VolumeUp color={volume !== 0 ? "primary" : "disabled"} />
          </Button>
        </ButtonGroup>
      </CustomGridItem>
      <CustomGridItem xs={12} md={4}>
        <Box p={3}>
          <Slider
            color={volume !== 0 ? "primary" : "secondary"}
            value={volumeComponent}
            onChange={(event, value) => {
              volumeChanged.current = true;
              setVolumeComponent(value);
            }}
            onChangeCommitted={(event, value) => handleVolumeChange(value)}
            step={2}
            min={0}
            max={100}
          />
        </Box>
      </CustomGridItem>
      <CustomGridItem xs={12} md={12}>
        <Divider>
          <Typography variant="h5">Temporizador</Typography>
        </Divider>
      </CustomGridItem>
      <CustomGridItem xs={12} md={12}>
        <Temporizador
          apiUrl={apiUrl}
          playing={playing}
          timeInSeconds={timeInSeconds}
          setTimeInSeconds={setTimeInSeconds}
          timerRunning={timerRunning}
          setTimerRunning={setTimerRunning}
          fadeOut={fadeOutAjusted}
          handleFadeOut={handleFadeOut}
        ></Temporizador>
      </CustomGridItem>
    </>
  );
};

export default Player;
