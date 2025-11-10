import * as React from 'react'
import {FormControl, InputLabel, MenuItem, Select} from '@material-ui/core'
import {makeStyles} from '@material-ui/styles'
import {selectDarkMode} from '../redux/slices/userSlice'
import {useSelector} from "react-redux";


const useStyles = makeStyles(theme => ({
    light: {
        borderColor: 'var(--dark)',
        '& .MuiOutlinedInput-notchedOutline': {
            borderColor: 'var(--dark)'
        },
        color: 'var(--dark)',
        '& .MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline': {
            borderColor: 'var(--dark)'
        },
        '& .MuiInputBase-input': {
            color: 'var(--dark)'
        },
        '& input + fieldset': {
            borderColor: 'var(--dark)'
        },
        '&:hover input + fieldset': {
            borderColor: 'var(--dark)'
        },
        '& .MuiFormLabel-root': {
            color: 'var(--dark)'
        }
    },
    dark: {
        borderColor: 'var(--white)',
        '& .MuiOutlinedInput-notchedOutline': {
            borderColor: 'var(--white)'
        },
        color: 'var(--white)',
        '& .MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline': {
            borderColor: 'var(--white)'
        },
        '& input + fieldset': {
            borderColor: 'var(--white)'
        },
        '& .MuiInputBase-input': {
            color: 'var(--white)'
        },
        '& .MuiOutlinedInput-root:hover .MuiOutlinedInput-notchedOutline': {
            borderColor: 'var(--white)'
        },
        '& .MuiFormLabel-root': {
            color: 'var(--white-dark)'
        },
        '& .MuiInputBase-root > svg': {
            fill: 'var(--white)'
        }
    }
}))

const inputStyle = {
    fontFamily: "'Montserrat', sans-serif",
    fontSize: '1.2rem',
}

const labelStyle = {
    fontFamily: "'Montserrat', sans-serif",
    fontSize: '1.1rem',
}

export interface DarkedSelectProps {
    readonly className?: string,
    readonly label?: string,
    readonly style?: React.CSSProperties,
    readonly options: Array<string | number>,
    readonly colorIgnored?: boolean
    readonly  onSelectedChange?: any
}

export default function DarkedSelect(props: DarkedSelectProps) {

    const darkMode = useSelector(selectDarkMode)

    const classes = useStyles()

    const {
        className: styles,
        label,
        style,
        options,
        colorIgnored = false,
        onSelectedChange
    } = props

    return (
        <FormControl
            className={classes[(!colorIgnored && darkMode) ? 'dark' : 'light'] + ' ' + (styles || "")}
            style={style || undefined}
            variant="outlined"
        >
            <InputLabel
                style={labelStyle}
            >{label}</InputLabel>
            <Select
                inputProps={{style: inputStyle}}
                label={label + ' '}
            >
                {options.map((item: string | number, index: number) => <MenuItem
                    key={item} value={item}
                    onClick={() => {
                        onSelectedChange(item)
                    }
                    }
                >{item}</MenuItem>)}
            </Select>
        </FormControl>


    )
}
