import React from "react";
import CheckIcon from "@material-ui/icons/CheckRounded";
import CloseIcon from "@material-ui/icons/CloseRounded";

const ActiveIcon = (
    {active}: {active: boolean}) =>
    active ? <CheckIcon style={{fill: 'var(--green)'}} /> : <CloseIcon style={{fill: 'var(--pink)'}} />

export default ActiveIcon;
