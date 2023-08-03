import React from "react";
import { LocalizationProvider, MobileTimePicker } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";

dayjs.extend(utc);

const TimeSelector = ({ value, onChange, disabled }) => {
  const onAccept = (value) => {
    onChange(value.hour() * 3600 + value.minute() * 60 + value.second());
  };

  return (
    <>
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <MobileTimePicker
          value={dayjs.utc(value * 1000)}
          views={["hours", "minutes", "seconds"]}
          ampm={false}
          onAccept={(value) => onAccept(value)}
          disabled={disabled}
        />
      </LocalizationProvider>
    </>
  );
};

export default TimeSelector;
