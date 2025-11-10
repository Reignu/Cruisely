import React, {useEffect, useReducer, useState} from "react";
import {useTranslation} from 'react-i18next'
import RoundedButton from '../../../components/RoundedButton'
import axios from "../../../Services/URL";
import styles from '../../../styles/ManageAccount.module.css'
import Grid from "@material-ui/core/Grid";
import {useSnackbarQueue} from "../../snackbar";
import store from "../../../redux/store";
import {setChangeAccessLevelStateAccount} from "../../../redux/slices/changeAccessLevelStateSlice";
import {refreshToken} from "../../../Services/userService";
import useHandleError from "../../../errorHandler";


export default function ChangeAccessLevelState() {
    const [, forceUpdate] = useReducer(x => x + 1, 0); // used to force component refresh on forceUpdate call
    const {t} = useTranslation()
    const {token, changeAccessLevelStateAccount} = store.getState()
    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')


    const handleChangeAccessLevelState = async (accessLevel: string, enabled: boolean) => {
        const json = {
            accountLogin: changeAccessLevelStateAccount.login,
            accessLevel: accessLevel,
            accountVersion: changeAccessLevelStateAccount.version,
            enabled: enabled
        }

        await axios.put("account/change-access-level-state", json, {
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "If-Match": changeAccessLevelStateAccount.etag,
                "Authorization": `Bearer ${token}`
            }
        }).then(res => {
            showSuccess(t('success.accessLevelStateChanged'))
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });

        await axios.get(`account/details/${changeAccessLevelStateAccount.login}`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        }).then(res => {
            store.dispatch(setChangeAccessLevelStateAccount(res.data));
            forceUpdate()
            refreshToken()
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });

    }
    useEffect( ()=>{
        axios.get(`account/details/${changeAccessLevelStateAccount.login}`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        }).then(res => {
            store.dispatch(setChangeAccessLevelStateAccount(res.data));
            forceUpdate()
            refreshToken()
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });
    }, [])
    return (
        <Grid container className={styles.wrapper}>
            <Grid item style={{display: "block"}} className={styles.item}>
                {changeAccessLevelStateAccount.login != "" ? changeAccessLevelStateAccount.accessLevels.map((accessLevel: any) => (
                    <div className={styles.item}>
                        <h4>{t(accessLevel.accessLevelType)}</h4>
                        <RoundedButton color="blue"
                                       onClick={() => handleChangeAccessLevelState(accessLevel.accessLevelType, !accessLevel.enabled)}
                        >{accessLevel.enabled ? t("disable") : t("enable")}
                        </RoundedButton>
                        <br/>
                    </div>
                )) : <RoundedButton color={"blue"} onClick={forceUpdate}>{t("refresh")}</RoundedButton>}
            </Grid>
        </Grid>
    )
}