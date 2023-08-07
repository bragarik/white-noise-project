import { Box, Card, CardMedia, Grid, Typography } from "@mui/material";
import Titulo from "./components/Titulo";
import Player from "./components/Player";

import "./assets/styles.css";
import { useState } from "react";

const API_URL = `${window.location.protocol}//${window.location.hostname}:8080/WhiteNoiseServer/Actions`; // Substitua pela URL da sua API

function App() {
  const [playing, setPlaying] = useState(false); // Estado de reprodução

  return (
    <Grid
      container
      direction="row"
      // spacing={2}
      className="animation"
      alignItems="center"
      justifyContent="center"
    >
      <Titulo titulo="White Noise do Ian ❤️" subtitulo="Tenha bons sonhos 💭" />
      <Player apiUrl={API_URL} playing={playing} setPlaying={setPlaying} />
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
                process.env.PUBLIC_URL + (playing ? "/go-to.gif" : "/to-go.gif")
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
            Versão 1.51
          </Typography>
        </Box>
      </Grid>
    </Grid>
  );
}

export default App;
