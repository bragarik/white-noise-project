import React, { useState } from "react";
import Alert from "@mui/material/Alert";
import { Fade } from "@mui/material";

const AlertServer = ({ open, message }) => {
  return (
    <>
      <Fade in={open}>
        <Alert
          sx={{
            position: "fixed",
            bottom: "1em",
            left: "50%",
            transform: "translateX(-50%)",
            zIndex: 9999,
          }}
          severity="error"
          open={open}
        >
          {message}
        </Alert>
      </Fade>
    </>
  );
};

export default AlertServer;
