import {Box, List, ListItem, ListItemIcon, ListItemText} from '@material-ui/core'
import {Link} from 'react-router-dom';
import styles from '../styles/PanelMenu.module.css'
import {useSelector} from "react-redux";
import {selectDarkMode, selectEmail, selectFirstName, selectSecondName} from "../redux/slices/userSlice";
import {Color} from "./interfaces";
import React from "react";
import AppColorSetter from "./AppColorSetter";
import GoBackIcon from '@material-ui/icons/ArrowBackRounded';
import {useTranslation} from "react-i18next";
import LanguageSetter from "./LanguageSetter";


export interface PanelMenuProps {
    color: Color,
    children?: any
}


export default function PanelMenu(props: PanelMenuProps) {
    const darkMode = useSelector(selectDarkMode)
    const firstname = useSelector(selectFirstName)
    const secondName = useSelector(selectSecondName)
    const email = useSelector(selectEmail)

    const {t} = useTranslation()

    return (
        <Box className={styles.wrapper} style={{backgroundColor: `var(--${props.color})`}}>
            <Box className={styles.profile + ' ' + styles[`profile-${!darkMode ? 'light' : 'dark'}`]}>
                <div className={styles.name}>{firstname + ' ' + secondName}</div>
                <div className={styles.email}>{email}</div>
            </Box>
            <List className={styles.menu + ' ' + styles['menu-' + (!darkMode ? 'light' : 'dark')]} component="nav" aria-label="panel menu">
                {props.children}
                <Link to="/">
                    <ListItem button>
                        <ListItemIcon>
                            <GoBackIcon style={{fill: `var(--${!darkMode ? 'white' : 'dark'})` }} />
                        </ListItemIcon>
                        <ListItemText>{t("go back")}</ListItemText>
                    </ListItem>
                </Link>
            </List>
        </Box>
    )
}