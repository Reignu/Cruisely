import Button from '@material-ui/core/Button'
import {makeStyles} from '@material-ui/styles'
import * as React from 'react'
import {Color} from './interfaces'

export interface RoundedButtonProps {
    readonly className?: string,
    readonly color: Color,
    readonly children?: any,
    readonly style?: React.CSSProperties,
    readonly onClick?: React.MouseEventHandler<any>,
    readonly disabled?: boolean
}

export default function RoundedButton(props: RoundedButtonProps) {
    
    const {
        className: styles,
        children,
        color,
        style,
        onClick,
        disabled = false,
    } = props

    const classes = makeStyles(theme => ({
        root: {
            '&.Mui-disabled .MuiButton-label': {
                color: 'var(--white)',
                opacity: .5
            },
            padding: '8px 16px',
            fontFamily: "'Montserrat', sans-serif",
            backgroundColor: 'var(--' + color + ')',
            color: 'var(--white)',
            borderRadius: 34,
            '&:hover': {
                backgroundColor: 'var(--' + color + '-dark)'
            }
        }
    }))()

    return (
        <Button
            disabled={disabled}
            className={(styles || '') + ' ' + classes.root}
            style={style || undefined}
            onClick={onClick || undefined}
        >{children}</Button>
    )
}