import { Box, Card, CardMedia, Grid, Typography } from "@mui/material";
import Titulo from "./components/Titulo";
import Player from "./components/Player";

import "./assets/styles.css";
import { useState } from "react";
import AlertServer from "./components/AlerServert";

const API_URL = `${window.location.protocol}//${window.location.hostname}:8080/WhiteNoiseServer/Actions`; // Substitua pela URL da sua API

function App() {
  const [playing, setPlaying] = useState(false); // Estado de reprodução
  const [openAlert, setOpenAlert] = useState(false); // Estado de reprodução
  const [messageAlert, setMessageAlert] = useState(""); // Estado de reprodução

  const handleMessageAlert = (message) => {
    if (message != null && message.length > 0) {
      setOpenAlert(true);
      setMessageAlert(message);
    } else {
      setOpenAlert(false)
    }
  };

  return (
    <>
      <Grid
        container
        direction="row"
        // spacing={2}
        className="animation"
        alignItems="center"
        justifyContent="center"
      >
        <Titulo
          titulo="White Noise do Ian ❤️"
          subtitulo="Tenha bons sonhos 💭"
        />
        <Player
          apiUrl={API_URL}
          playing={playing}
          setPlaying={setPlaying}
          setMessageAlert={handleMessageAlert}
        />
        <Grid item xs={6}>
          <Box textAlign="center">
            <Card
              style={{
                backgroundColor: "transparent",
                boxShadow: "none",
                backgroundImage: "none",
              }}
            >
              <CardMedia
                component="img"
                height="auto"
                image={
                  process.env.PUBLIC_URL +
                  (playing ? "/go-to.gif" : "/to-go.gif")
                }
                alt="go-to to-go"
                style={{ backgroundColor: "transparent", boxShadow: "none" }}
              />
            </Card>
          </Box>
        </Grid>
        <Grid item xs={6}>
          <Box textAlign="center">
            <Typography variant="caption" color="textSecondary" align="center">
              Versão 1.75
            </Typography>
          </Box>
        </Grid>
      </Grid>
      <AlertServer open={openAlert} message={messageAlert} />
    </>
  );
}

export default App;
