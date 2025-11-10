import {Box, InputAdornment, TextField} from '@material-ui/core'
import {makeStyles} from '@material-ui/styles'
import * as React from 'react'
import {useSelector} from "react-redux";
import {selectDarkMode} from "../redux/slices/userSlice";

const useStyles = makeStyles(theme => ({
    light: {
        borderColor: 'var(--dark)',
        color: 'var(--dark)',
        '& input:focus:focus + fieldset': {
            borderColor: 'var(--dark)'
        },
        '& input': {
            color: 'var(--dark)'
        },
        '& input + fieldset': {
            borderColor: 'var(--dark)'
        },
        '&:hover input + fieldset': {
            borderColor: 'var(--dark)'
        },
        '& .MuiInputLabel-root': {
            color: 'var(--dark-light)'
        }
    },
    dark: {
        borderColor: 'var(--white)',
        color: 'var(--white)',
        '& input:focus:focus + fieldset': {
            borderColor: 'var(--white)'
        },
        '& input + fieldset': {
            borderColor: 'var(--white)'
        },
        '& input': {
            color: 'var(--white)'
        },
        '& input::placeholder': {
            color: 'var(--white)'
        },
        '& .MuiOutlinedInput-root:hover .MuiOutlinedInput-notchedOutline': {
            borderColor: 'var(--white)'
        },
        '& .MuiInputLabel-root': {
            color: 'var(--white-dark)'
        }
    }
}))


export interface DarkedTextFieldProps {
    readonly type?: "text" | "email" | "password" | "number",
    readonly className?: string,
    readonly icon?: JSX.Element,
    readonly placeholder?: string,
    readonly label?: string,
    readonly style?: React.CSSProperties,
    readonly value?: any,
    readonly colorIgnored?: boolean,
    readonly regexError?: boolean

    onChange?(event: React.ChangeEvent<HTMLInputElement>): void
}

const DarkedTextField = (props: DarkedTextFieldProps) => {
        const inputStyle = {
            fontFamily: "'Montserrat', sans-serif",
            fontSize: '1.2rem',
            color: ''
        }

        const labelStyle = {
            style: {
                fontFamily: "'Montserrat', sans-serif",
                fontSize: '1.1rem',
                color: ''
            }
        }
        const regexErrorTextStyle = {
            color: 'red'
        }
        const classes = useStyles()

        const {
            className: styles,
            icon,
            type,
            placeholder,
            label,
            style,
            value,
            onChange,
            colorIgnored,
            regexError
        } = props

        const darkMode = useSelector(selectDarkMode)
        inputStyle.color = regexError ? 'red' : ''
        labelStyle.style.color = regexError ? 'red' : ''

        return (
            <>
                <TextField
                    type={type || "text"}
                    className={classes[(!colorIgnored && darkMode) ? 'dark' : 'light'] + ' ' + (styles || "")}
                    variant="outlined"
                    label={label || ""}
                    InputProps={{
                        startAdornment: (
                            <InputAdornment position="start">
                                {icon || ""}
                            </InputAdornment>
                        ),
                        style: inputStyle,
                        error: regexError
                    }}
                    InputLabelProps={labelStyle}
                    placeholder={placeholder || ""}
                    style={style || undefined}
                    value={value}
                    onChange={onChange}
                />
            </>

        )
    }
;

export default DarkedTextField;