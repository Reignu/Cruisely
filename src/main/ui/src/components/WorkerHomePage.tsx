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



export default function AdminHomePage() {
    const darkMode = useSelector(selectDarkMode)
    const {t} = useTranslation()

    return (
        <div className={styles.panel_page}>
            <h1>{t("business_workerHomePage")}</h1>
        </div>
    )
}