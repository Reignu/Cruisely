import React from "react";
import {useDispatch, useSelector} from "react-redux";
import {Link, useHistory, useLocation} from "react-router-dom";
import styles from "../styles/Header.module.css";
import RoundedButton from "./RoundedButton";
import Box from "@material-ui/core/Box";
import {useTranslation} from "react-i18next";
import {
    Button,
    Popper,
    MenuList,
    MenuItem,
    Paper,
    Fade,
    ClickAwayListener
} from '@material-ui/core'
import {
    selectOtherAccessLevel,
    isUserEmpty,
    selectActiveAccessLevel,
    selectFirstName,
    selectSecondName, AccessLevelType, setActiveAccessLevel
} from "../redux/slices/userSlice";
import {logOut} from "../Services/userService";
import {clearHistory, selectHistory, setHistory} from "../redux/slices/historySlice";


export default function Authentication() {
    const {t} = useTranslation()
    const history = useHistory()
    const location = useLocation()
    const h = useSelector(selectHistory)
    const dispatch = useDispatch()

    const isEmpty = useSelector(isUserEmpty)

    const firstName = useSelector(selectFirstName)
    const secondName = useSelector(selectSecondName)
    const activeAccessLevel = useSelector(selectActiveAccessLevel)
    const accessLevelLabels = useSelector(selectOtherAccessLevel)

    const [anchorEl, setAnchorEl] = React.useState<HTMLButtonElement | null>(null);
    const [open, setOpen] = React.useState(false);

    const handleCredClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget);
        if (accessLevelLabels.length) {
            setOpen(prev => !prev);
        }
    };

    const handleClose = () => {
        setOpen(false)
        setAnchorEl(null)
    }

    const handleMenuItemClick = (accessLevel: AccessLevelType) => () => {
        dispatch(setActiveAccessLevel(accessLevel))
        dispatch(clearHistory())
        history.push('/')
    }

    const handleLogOut = () => {
        logOut()
    }

    return (
        <Box>
            {
                isEmpty ? (
                    <>
                        <a
                            style={{
                                cursor: 'pointer',
                                marginRight: 20,
                            }}
                            className={styles.link}
                            onClick={() => {
                                dispatch(setHistory(location.pathname))
                                history.push('/signin')
                            }}
                        >
                            {t('signin')}
                        </a>

                        <RoundedButton
                            color="pink"
                            style={{
                                fontSize: '1rem',
                                padding: '10px 20px',
                                textTransform: 'none'
                            }}
                            onClick={() => {
                                dispatch(setHistory(location.pathname))
                                history.push('/signup/client')
                            }}
                        >
                            {t("signup")}
                        </RoundedButton>
                    </>
                ) : (
                    <>
                        <Button
                            onClick={handleCredClick}
                            className={styles.link}
                            style={{
                                marginRight: 20,
                                color: 'var(--white)',
                                fontFamily: 'Montserrat, sans-serif'
                            }}>{t(activeAccessLevel)}</Button>

                        <Popper open={open} anchorEl={anchorEl} placement="bottom-end" transition style={{zIndex:5001}}>
                            {({ TransitionProps }) => (
                                <Fade {...TransitionProps} timeout={350}>
                                    <Paper style={{padding: 10}}>
                                        <ClickAwayListener onClickAway={handleClose}>
                                            {
                                                accessLevelLabels.length ? (
                                                    <MenuList>
                                                        {
                                                            accessLevelLabels.map(label => (
                                                                    <MenuItem
                                                                        style={{
                                                                            fontFamily: 'Montserrat, sans-serif',
                                                                            fontSize: '1.2rem'
                                                                        }}
                                                                        onClick={handleMenuItemClick(label)}
                                                                    >{t(label)}</MenuItem>
                                                                )
                                                            )
                                                        }
                                                    </MenuList>
                                                ) : <p style={{
                                                    fontFamily: 'Montserrat, sans-serif',
                                                    fontSize: '1.2rem'
                                                }}>{t('error.emptypanellist')}</p>
                                            }
                                        </ClickAwayListener>
                                    </Paper>
                                </Fade>
                            )}
                        </Popper>
                        <Button
                            className={styles.link}
                            style={{
                                marginRight: 20,
                                color: 'var(--white)',
                                fontFamily: 'Montserrat, sans-serif'
                            }}
                            onClick={activeAccessLevel === "CLIENT" ? () => {history.push('/profile')} : undefined}
                        >
                            {firstName + ' ' + secondName}
                        </Button>
                        <RoundedButton
                            color="pink"
                            style={{
                                fontSize: '1rem',
                                padding: '10px 20px',
                                textTransform: 'none'
                            }}
                            onClick={handleLogOut}
                        >{t("logout")}</RoundedButton>
                    </>
                )
            }
        </Box>
    )
}