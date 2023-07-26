import React from "react";
import { Grid, Box } from "@mui/material";

function CustomGridItem({ children, xs, md }) {
  return (
    <Grid item xs={xs} md={md}>
      <Box textAlign="center">{children}</Box>
    </Grid>
  );
}

export default CustomGridItem;
