import React from 'react';
import Button from '@material-ui/core/Button';
import MuiDialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import {useTranslation} from "react-i18next";
import {useSelector} from "react-redux";
import {selectDarkMode} from "../redux/slices/userSlice";
import RoundedButton from "./RoundedButton";

export interface FormDialogProps {
    open: boolean,
    title: string,
    children: any,
    onConfirm: () => void,
    onCancel: () => void,
}

export default function Dialog(
    {
        open,
        title,
        children,
        onConfirm,
        onCancel
    }: FormDialogProps) {
    const {t} = useTranslation();
    const darkMode = useSelector(selectDarkMode)

    return (
        <MuiDialog open={open} onClose={onCancel}>
            <DialogTitle style={{fontFamily: "'Montserrat', sans-serif", color: `var(--${darkMode ? 'white-light' : 'dark-dark'}`, backgroundColor: `var(--${darkMode ? 'dark' : '--white-light'})`}}>{title}</DialogTitle>
            <DialogContent style={{backgroundColor: `var(--${darkMode ? 'dark' : '--white-light'})`}}>
                {children}
            </DialogContent>
            <DialogActions style={{backgroundColor: `var(--${darkMode ? 'dark' : '--white-light'})`}}>
                <RoundedButton color="pink" onClick={onCancel}>
                    {t('cancel')}
                </RoundedButton>
                <RoundedButton color="blue" onClick={onConfirm}>
                    {t('confirm')}
                </RoundedButton>
            </DialogActions>
        </MuiDialog>
    );
}