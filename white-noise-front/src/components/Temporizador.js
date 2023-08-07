import React, { useEffect, useState } from "react";
import {
  Typography,
  Button,
  ButtonGroup,
  Grid,
  Box,
  Switch,
  FormControlLabel,
} from "@mui/material";
import TimeSelector from "./TimerSelector";
import axios from "axios";
import "../assets/Temporizador.css";

const Temporizador = ({
  apiUrl,
  playing,
  timeInSeconds,
  setTimeInSeconds,
  timerRunning: running,
  setTimerRunning: setRunning,
  fadeOut,
  handleFadeOut,
}) => {
  const [timeAjusted, setTimeAjusted] = useState(0);

  const formatTime = (seconds) => {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const remainingSeconds = seconds % 60;

    let formattedTime = "";
    if (hours > 0) formattedTime += `${hours}h `;
    if (minutes > 0) formattedTime += `${minutes}m `;
    formattedTime += `${remainingSeconds}s`;

    return formattedTime;
  };

  useEffect(() => {
    let intervalId;
    if (running && timeInSeconds > 0) {
      intervalId = setInterval(() => {
        setTimeInSeconds((prevTime) => prevTime - 1);
      }, 1000);
    } else {
      setRunning(false);
    }
    return () => clearInterval(intervalId);

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [running, timeInSeconds]);

  const handleStopTime = () => {
    handleSetTime(0);
  };

  const handleSetTime = (time, renew) => {
    if (!running) {
      setTimeInSeconds(time);
      setTimeAjusted(time);
    } else {
      setTimeInSeconds(time + (renew ? 0 : timeInSeconds));
      setTimeAjusted(time + (renew ? 0 : timeInSeconds));
    }

    setRunning(true);

    axios
      .post(`${apiUrl}/timer?on=${time > 0}&timer=${time}`)
      .catch((error) => console.log(error));
  };

  return (
    <>
      <Grid container direction="row" spacing={2}>
        <Grid item xs={6} md={6}>
          <Box textAlign="right">
            <Typography
              variant="h4"
              component="div"
              gutterBottom
              className="title"
            >
              {formatTime(timeInSeconds)}
            </Typography>
          </Box>
        </Grid>
        <Grid item xs={6} md={6}>
          <Box textAlign="left">
            <FormControlLabel
              labelPlacement="top"
              control={
                <Switch
                  disabled={playing && !running}
                  checked={fadeOut}
                  onChange={(event, value) => handleFadeOut(value)}
                />
              }
              label="Volume fade-out"
            />
          </Box>
        </Grid>
        <Grid item xs={12} md={6}>
          <Box textAlign="center">
            <FormControlLabel
              color="white"
              labelPlacement="top"
              control={
                <Switch
                  disabled={!playing}
                  checked={running}
                  onChange={() =>
                    running
                      ? handleStopTime()
                      : timeInSeconds <= 0
                      ? handleSetTime(3600)
                      : handleSetTime(timeInSeconds)
                  }
                />
              }
              label={running ? "Timer on" : "Timer off"}
            />
            <ButtonGroup variant="contained" size="large" disabled={!playing}>
              <Button onClick={() => handleSetTime(3600)}>
                {running && "+"} 1h
              </Button>
              <Button onClick={() => handleSetTime(1800)}>
                {running && "+"} 30m
              </Button>
              <Button onClick={() => handleSetTime(900)}>
                {running && "+"} 15m
              </Button>
            </ButtonGroup>
          </Box>
        </Grid>
        <Grid item xs={12} md={6}>
          <Box textAlign="center">
            <TimeSelector
              disabled={!playing}
              value={timeAjusted}
              onChange={(value) => {
                handleSetTime(value, true);
              }}
            ></TimeSelector>
          </Box>
        </Grid>
      </Grid>
    </>
  );
};

export default Temporizador;
