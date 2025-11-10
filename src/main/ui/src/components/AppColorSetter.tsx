import {useTranslation} from "react-i18next";
import LightThemeIcon from '@material-ui/icons/WbSunnyRounded';
import DarkThemeIcon from '@material-ui/icons/Brightness3Rounded';
import {IconButton} from "@material-ui/core";
import {useSelector} from "react-redux";

import {selectDarkMode} from '../redux/slices/userSlice'
import {changeDarkMode} from '../Services/changeDataService'
import {useSnackbarQueue} from "../pages/snackbar";
import useHandleError from "../errorHandler";
import PopupAcceptAction from "../PopupAcceptAction";
import {useState} from "react";

export default function AppColorSetter() {
    const {t} = useTranslation();
    const showSuccess = useSnackbarQueue('success')
    const handleError = useHandleError()

    const showError = useSnackbarQueue('error')
    const darkMode = useSelector(selectDarkMode)

    const handleClick = () => {
        changeDarkMode().then(res => {
            showSuccess(t('successful action'))
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });
    }

    return (
        <IconButton
            onClick={handleClick}
        >
            {
                darkMode ?
                    <DarkThemeIcon
                        fontSize="large"
                        style={{fill: 'var(--dark)'}}/> :
                    <LightThemeIcon
                        fontSize="large"
                        style={{fill: 'var(--white)'}}/>
            }
        </IconButton>
    )
}