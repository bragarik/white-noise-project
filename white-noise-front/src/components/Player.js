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
  Piano,
  NoiseAware,
  Pause,
} from "@mui/icons-material";
import axios from "axios";

import Status from "./Status";
import "../assets/Player.css";
import CustomGridItem from "./CustomGridItem";
import Temporizador from "./Temporizador";

const Player = ({ apiUrl, playing, setPlaying, setMessageAlert }) => {
  const [volume, setVolume] = useState(null); // Estado do volume
  const [lastVolume, setLastVolume] = useState(null); // Estado para armazenar o último volume
  const volumeChanged = useRef(false);
  const [volumeComponent, setVolumeComponent] = useState(lastVolume);
  const [statusMessage, setStatusMessage] = useState("");
  const [timeInSeconds, setTimeInSeconds] = useState(0);
  const [timerRunning, setTimerRunning] = useState(false);
  const [fadeOutAjusted, setFadeOutAjusted] = useState(false);
  const [modeNoise, setModeNoise] = useState(true);

  useEffect(() => {
    setVolume(parseInt(localStorage.getItem("volume")));
  }, []);

  useEffect(() => {
    if (volumeChanged.current) {
      axios
        .post(`${apiUrl}/volume?volume=${volume / 100}`)
        .catch((error) => setMessageAlert("Ocorreu um erro ao ajustar o volume: " + error.message))
        .finally(() => (volumeChanged.current = false));
    }
  }, [apiUrl, volume]);

  // Função para iniciar a reprodução
  const handlePlay = () => {
    axios
      .post(`${apiUrl}/play`)
      .then(function (response) {
        const data = response.data;
        handleStatusChange(data);
        setMessageAlert();
      })
      .catch((error) =>  setMessageAlert("Ocorreu um erro ao executar o play: " + error.message));
  };

  // Função para parar a reprodução
  const handleStop = () => {
    axios
      .post(`${apiUrl}/stop`)
      .then(function (response) {
        const data = response.data;
        handleStatusChange(data);
        setMessageAlert();
      })
      .catch((error) =>  setMessageAlert("Ocorreu um erro ao executar o stop: " + error.message));
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
    setVolumeComponent(newVolume);
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

  const handleStatusChange = (data) => {
    setPlaying(data.status);
    if (!volumeChanged.current) {
      setVolume(data.volume * 100);
      setVolumeComponent(data.volume * 100);
    }
    setStatusMessage(data.statusMessage);

    setTimerRunning(data.timer.on);
    if (data.timer.on) {
      setTimeInSeconds(data.timer.remainingSeconds);
    }

    setFadeOutAjusted(data.fadeOut);
    setModeNoise(data.modeNoise);
  };

  const handleFadeOut = (value) => {
    axios
      .post(`${apiUrl}/fadeOut?fadeOut=${value}`)
      .then(() => setMessageAlert())
      .catch((error) => setMessageAlert("Ocorreu um erro ao mudar o fadeOut: " + error.message))
      .finally(() => setFadeOutAjusted(value));
  };

  // Função para alterar o volume
  const handleVolumeChange = (newValue) => {
    setLastVolume(volume);
    setVolume(newValue);
    volumeChanged.current = true;
    localStorage.setItem("volume", newValue);
  };

  const handleModeNoise = (newValue) => {
    setModeNoise(newValue);
    axios
      .post(`${apiUrl}/mode?noise=${newValue}`)
      .then(() => setMessageAlert())
      .catch((error) =>setMessageAlert("Ocorreu um erro ao mudar o noise mode: " + error.message));
  };

  return (
    <>
      <CustomGridItem xs={9} md={2}>
        <Status
          apiUrl={apiUrl}
          onStatusChange={handleStatusChange}
          onStatusError={setMessageAlert}
          playing={playing}
          statusMessage={statusMessage}
        />
      </CustomGridItem>
      <CustomGridItem xs={3} md={1}>
        <Button variant="contained" onClick={playing ? handleStop : handlePlay}>
          {playing ? <Pause /> : <PlayArrow />}
        </Button>
      </CustomGridItem>
      <CustomGridItem xs={3} md={1}>
        <Button
          color={modeNoise ? "primary" : "secondary"}
          onClick={() => handleModeNoise(!modeNoise)}
          size="large"
          endIcon={
            modeNoise ? (
              <NoiseAware fontSize="large" />
            ) : (
              <Piano fontSize="large" />
            )
          }
        >
          {modeNoise ? <>Noise</> : <>Relax</>}
        </Button>
      </CustomGridItem>
      <CustomGridItem xs={9} md={3}>
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
