import React from "react";
import Button from '@material-ui/core/Button';
import {useTranslation} from "react-i18next";
import {Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle} from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import styles from "../src/styles/ManageAccount.module.css"
import tbStyles from "../src/styles/mtdTable.module.css";

export interface PopupMetadata {
    open: boolean,
    onCancel: () => void,
    alterType: string,
    alteredBy: string,
    createdBy: string,
    creationDateTime: string,
    lastAlterDateTime: string,
    version: string,
}

export default function PopupMetadata({open, onCancel, alterType, alteredBy, createdBy, creationDateTime, lastAlterDateTime, version}: PopupMetadata) {
    const {t} = useTranslation()
    return (
        <Dialog
            maxWidth={false}
            open={open}
            onClose={onCancel}
            aria-labelledby="alert-dialog-title"
            aria-describedby="alert-dialog-description"
        >
            <DialogTitle id="alert-dialog-title">
                <Grid item style={{display: "block" }} className={styles['change-item']}>
                    <tr>
                        <td className={tbStyles.td}><h6>{t("alterType")}</h6></td>
                        <td className={tbStyles.td}><h6>{t("alteredBy")}</h6></td>
                        <td className={tbStyles.td}><h6>{t("createdBy")}</h6></td>
                        <td className={tbStyles.td}><h6>{t("creationDateTime")}</h6></td>
                        <td className={tbStyles.td}><h6>{t("lastAlterDateTime")}</h6></td>
                        <td className={tbStyles.td}><h6>{t("version")}</h6></td>
                    </tr>
                    <tr>
                        <td className={tbStyles.tdData}><h6>{t(alterType)}</h6></td>
                        <td className={tbStyles.tdData}><h6>{alteredBy}</h6></td>
                        <td className={tbStyles.tdData}><h6>{createdBy}</h6></td>
                        <td className={tbStyles.tdData}><h6>{creationDateTime}</h6></td>
                        <td className={tbStyles.tdData}><h6>{lastAlterDateTime}</h6></td>
                        <td className={tbStyles.tdData}><h6>{version}</h6></td>
                    </tr>
                </Grid>
            </DialogTitle>
            <DialogActions>
                <Button onClick={onCancel} color="primary" autoFocus>
                    {(t('cancel.label'))}
                </Button>
            </DialogActions>
        </Dialog>
    );
}