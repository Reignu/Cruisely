import {Box, createMuiTheme, Grid, TextField} from "@material-ui/core";
import DarkedTextField from "../../../components/DarkedTextField";
import React, {useEffect, useReducer, useState} from "react";
import {useTranslation} from "react-i18next";
import RoundedButton from "../../../components/RoundedButton";
import ImageUploading, {ImageListType} from "react-images-uploading";
import store from "../../../redux/store";
import {useSelector} from "react-redux";
import {selectCompany, selectDarkMode, selectLanguage} from "../../../redux/slices/userSlice";
import axios from "../../../Services/URL";
import useHandleError from "../../../errorHandler";
import {useSnackbarQueue} from "../../snackbar";
import pl from "date-fns/locale/pl"
import eng from "date-fns/locale/en-GB"
import styles from "../../../styles/auth.global.module.css";
import DarkedSelect from "../../../components/DarkedSelect";
import {getAllCruiseGroup, getCruiseGroupForBusinessWorker} from "../../../Services/cruiseGroupService";
import {refreshToken} from "../../../Services/userService";


import {
    MuiPickersUtilsProvider,
    KeyboardDatePicker, KeyboardTimePicker
} from '@material-ui/pickers';
import DateFnsUtils from "@date-io/date-fns";
import {makeStyles} from "@material-ui/styles";
import Popup from "../../../PopupRecaptcha";
import Recaptcha from "react-recaptcha";
import PopupAcceptAction from "../../../PopupAcceptAction";


const useStyles = makeStyles(theme => ({
    light: {
        '& .MuiFormLabel-root, & .MuiInputBase-input': {
            color: 'var(--dark)',
        },
        '& .MuiSvgIcon-root': {
            fill: 'var(--dark)',
        },
        '& .MuiInput-underline::before, & .MuiInput-underline::after, & .MuiInput-underline:hover:not(.Mui-disabled):before': {
            borderColor: 'var(--dark)',
        },
    },
    dark: {
        '& .MuiFormLabel-root, & .MuiInputBase-input': {
            color: 'var(--white)',
        },
        '& .MuiSvgIcon-root': {
            fill: 'var(--white)',
        },
        '& .MuiInput-underline::before, & .MuiInput-underline::after, & .MuiInput-underline:hover:not(.Mui-disabled):before': {
            borderColor: 'var(--white)',
        },
    },
}));

export default function AddCruise() {
    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);


    const [, forceUpdate] = useReducer(x => x + 1, 0);
    const {t} = useTranslation();

    const darkMode = useSelector(selectDarkMode);

    const languageType = useSelector(selectLanguage);

    const language = languageType === 'PL' ? pl : eng;
    const isPMAM = language === eng;

    const [cruiseName, setCruiseName] = useState('')
    const [startDate, setStartDate] = React.useState<Date | null>(
        new Date(),
    );
    const [endDate, setEndDate] = React.useState<Date | null>(
        new Date(),
    );
    const [endTime, setEndTime] = React.useState<Date | null>(
        new Date(),
    );
    const [startTime, setStartTime] = React.useState<Date | null>(
        new Date(),
    );
    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')
    const [cruiseGroupList, setCruiseGroupList] = useState([]);
    const [cruiseGroup, setCruiseGroup] = useState("")

    const handleStartDateChange = (startDate: Date | null) => {
        setStartDate(startDate);
    };
    const handleEndDateChange = (endDate: Date | null) => {
        setEndDate(endDate);
    };
    const handleStartTimeChange = (startTime: Date | null) => {
        setStartTime(startTime);
    };
    const handleEndTimeChange = (endTime: Date | null) => {
        setEndTime(endTime);
    };
    const maxNumber = 1;

    const worker_Company = useSelector(selectCompany);

    useEffect(() => {

        getCruiseGroupForBusinessWorker(worker_Company).then(res => {
            setCruiseGroupList(res.data.filter(({active}: { active: boolean }) => active).map((cruiseGroup: { uuid: string, name: string }) => cruiseGroup.name + " - ( " + cruiseGroup.uuid + " )"))
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
        }).then(res => {
            refreshToken()
        });
    }, []);


    const HandleAddCruise = () => {
        if (!cruiseGroup || !startDate || !endDate || !startTime || !endTime) {
            handleError('not.all.fields.filled')
            return
        }

        startDate.setHours(0, 0, 0, 0)
        endDate.setHours(0, 0, 0, 0)

        if (startDate.getTime() < new Date("1900-01-01").getTime()) {
            handleError('min.date.message')
            return
        }
        if (startDate.getTime() > new Date("2100-01-01").getTime()) {
            handleError('max.date.message')
            return
        }
        if (endDate.getTime() < new Date("1900-01-01").getTime()) {
            handleError('min.date.message')
            return
        }
        if (endDate.getTime() > new Date("2100-01-01").getTime()) {
            handleError('max.date.message')
            return
        }


        try {
            var newStartDateWithoutTime = new Date(startTime.getTime())
            newStartDateWithoutTime.setHours(0, 0, 0, 0)
            var newStartDate = new Date(startDate.getTime() + startTime?.getTime() - newStartDateWithoutTime.getTime())


            var newEndDateWithoutTime = new Date(endTime.getTime())
            newEndDateWithoutTime.setHours(0, 0, 0, 0)
            var newEndDate = new Date(endDate.getTime() + endTime.getTime() - newEndDateWithoutTime.getTime())

            var date = new Date()

            if (newStartDate.getTime() > newEndDate.getTime()) {
                handleError('error.field.end.date.before.start.date')
                return
            }
            if (newStartDate.getTime() < date.getTime()) {
                handleError('error.field.start.date')
                return;
            }

            newStartDate.setSeconds(0)
            newEndDate.setSeconds(0)
            newStartDate.setMilliseconds(0)
            newEndDate.setMilliseconds(0)
            const {token} = store.getState();
            const json = JSON.stringify({
                startDate: newStartDate.toISOString(),
                endDate: newEndDate.toISOString(),
                cruiseGroupUUID: cruiseGroup.substr(-38, 36)
            })
            axios.post('cruise/new-cruise', json, {
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            }).then(res => {
                showSuccess(t('successful action'))
                forceUpdate()
            }).catch(error => {
                const message = error.response.data
                handleError(message, error.response.status)
                return
            })
        } catch (e) {
            handleError('error.fields')
            return
        }


    }

    const classes = useStyles()

    return (
        <div>
            <Box>
                <MuiPickersUtilsProvider locale={language} utils={DateFnsUtils}>

                    <KeyboardDatePicker
                        style={{marginRight: 30}}
                        className={darkMode ? classes.dark : classes.light}
                        autoOk={true}
                        disableToolbar
                        maxDateMessage={t("max.date.message")}
                        minDateMessage={t("min.date.message")}
                        invalidDateMessage={t("invalid.date.message")}
                        invalidLabel={t("invalid.label")}
                        cancelLabel={t("cancel.label")}
                        clearLabel={t("clear.label")}
                        okLabel={t("ok.label")}
                        todayLabel={t("today.label")}
                        variant="inline"
                        format="MM/dd/yyyy"
                        margin="normal"
                        label={t("startDate") + ' *'}
                        value={startDate}
                        onChange={handleStartDateChange}
                        KeyboardButtonProps={{
                            'aria-label': 'change date',
                        }}
                    />
                    <KeyboardTimePicker
                        className={darkMode ? classes.dark : classes.light}
                        autoOk={true}
                        maxDateMessage={t("max.date.message")}
                        minDateMessage={t("min.date.message")}
                        invalidDateMessage={t("invalid.date.message")}
                        invalidLabel={t("invalid.label")}
                        cancelLabel={t("cancel.label")}
                        clearLabel={t("clear.label")}
                        okLabel={t("ok.label")}
                        todayLabel={t("today.label")}
                        margin="normal"
                        ampm={isPMAM}
                        label={t("startTime") + ' *'}
                        value={startTime}
                        onChange={handleStartTimeChange}
                        KeyboardButtonProps={{
                            'aria-label': 'change time',
                        }}
                    />
                </MuiPickersUtilsProvider>
            </Box>
            <Box>
                <MuiPickersUtilsProvider locale={language} utils={DateFnsUtils}>
                    <KeyboardDatePicker
                        style={{marginRight: 30}}
                        className={darkMode ? classes.dark : classes.light}
                        disableToolbar
                        autoOk={true}
                        variant="inline"
                        maxDateMessage={t("max.date.message")}
                        minDateMessage={t("min.date.message")}
                        invalidDateMessage={t("invalid.date.message")}
                        invalidLabel={t("invalid.label")}
                        cancelLabel={t("cancel.label")}
                        clearLabel={t("clear.label")}
                        okLabel={t("ok.label")}
                        todayLabel={t("today.label")}
                        format="MM/dd/yyyy"
                        margin="normal"
                        label={t("endDate") + ' *'}
                        value={endDate}
                        onChange={handleEndDateChange}
                        KeyboardButtonProps={{
                            'aria-label': 'change date',
                        }}
                    />
                    <KeyboardTimePicker
                        className={darkMode ? classes.dark : classes.light}
                        autoOk={true}
                        maxDateMessage={t("max.date.message")}
                        minDateMessage={t("min.date.message")}
                        invalidDateMessage={t("invalid.date.message")}
                        invalidLabel={t("invalid.label")}
                        cancelLabel={t("cancel.label")}
                        clearLabel={t("clear.label")}
                        okLabel={t("ok.label")}
                        todayLabel={t("today.label")}
                        margin="normal"
                        label={t("endTime") + ' *'}
                        value={endTime}
                        ampm={isPMAM}
                        onChange={handleEndTimeChange}
                        KeyboardButtonProps={{
                            'aria-label': 'change time'
                        }}
                    />

                </MuiPickersUtilsProvider>
            </Box>
            <Box style={{
                width: '70%',
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between"
            }}>
                <DarkedSelect
                    label={t("cruiseGroup") + ' *'}
                    options={cruiseGroupList}
                    className={styles.input}
                    style={{
                        marginRight: 20
                    }}
                    onSelectedChange={setCruiseGroup}
                />
            </Box>

            <RoundedButton
                onClick={() => {
                    setButtonPopupAcceptAction(true)
                }}
                style={{width: '50%', fontSize: '1.2rem', padding: '10px 0', marginBottom: 20}}
                color="pink"
            >{t("newCruise")} </RoundedButton>


            <PopupAcceptAction
                open={buttonPopupAcceptAction}
                onConfirm={() => {
                    setButtonPopupAcceptAction(false)
                    HandleAddCruise()
                }}
                onCancel={() => {
                    setButtonPopupAcceptAction(false)
                }}
            />

        </div>
    )
}
