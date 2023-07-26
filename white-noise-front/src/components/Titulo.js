import React from "react";
import { Grid, Typography, useMediaQuery } from "@mui/material";
import { useTheme } from "@emotion/react";

const Titulo = ({ titulo, subtitulo }) => {
  const theme = useTheme();
  const isScreenMedium = useMediaQuery(theme.breakpoints.up("md"));

  return (
    <Grid item xs={12}>
      <Typography variant={isScreenMedium ? "h1" : "h3"} align="center">
        {titulo}
      </Typography>
      <Typography variant={isScreenMedium ? "h2" : "h4"} align="center">
        {subtitulo}
      </Typography>
    </Grid>
  );
};

export default Titulo;
