import React from "react";
import Button from '@material-ui/core/Button';
import {useTranslation} from "react-i18next";
import {Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle} from "@material-ui/core";

export interface PopupAcceptActionProps {
    open: boolean,
    onConfirm: () => void,
    onCancel: () => void
}

export default function PopupAcceptAction({open, onConfirm, onCancel}: PopupAcceptActionProps) {
    const {t} = useTranslation()

    return (
        <Dialog
            open={open}
            onClose={onCancel}
            aria-labelledby="alert-dialog-title"
            aria-describedby="alert-dialog-description"
        >
            <DialogTitle id="alert-dialog-title">{(t('accept.action'))} </DialogTitle>
            <DialogActions>
                <Button onClick={onConfirm} color="primary">
                    {(t('yes'))}
                </Button>
                <Button onClick={onCancel} color="primary" autoFocus>
                    {(t('no'))}
                </Button>
            </DialogActions>
        </Dialog>

    );
}