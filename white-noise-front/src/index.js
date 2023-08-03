import React from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';
import { createTheme, ThemeProvider } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    mode: 'dark', // Defina o tipo de paleta como "dark" para o tema escuro
  },
  components: {
    MuiTypography: {
      styleOverrides: {
        root: {
          color: "white"
        }
      }
    }
  }
});

const volume = localStorage.getItem("volume");
  if (volume == null) localStorage.setItem("volume", 50);

createRoot(document.getElementById('root'))
  .render(<React.StrictMode>
    <ThemeProvider theme={theme}>
      <App />
    </ThemeProvider>
  </React.StrictMode>);

// Dedico este projeto ao meu amado filho Ian.
// Que este projeto ajude nos seus sonos e que você
// tenha um começo de uma jornada de vida incrível.
// Estarei sempre aqui para apoiar você em todas as suas conquistas. Te amo!
// Dedico tambem à equipe do OpenAI, que tornou possível a criação do ChatGPT.
