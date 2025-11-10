import React, {useEffect, useReducer, useState} from 'react';
import {getAllCompanies} from "../../../Services/companiesService";
import {makeStyles} from "@material-ui/core/styles";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import useHandleError from "../../../errorHandler";
import RoundedButton from "../../../components/RoundedButton";
import {useSelector} from "react-redux";
import {selectCompany, selectDarkMode, selectLanguage} from "../../../redux/slices/userSlice";
import {refreshToken} from "../../../Services/userService";
import {useTranslation} from "react-i18next";
import Autocomplete from "../../../components/Autocomplete";
import {Button, TextField} from "@material-ui/core";
import {useSnackbarQueue} from "../../../pages/snackbar";
import {selectToken} from "../../../redux/slices/tokenSlice";
import {Link} from "react-router-dom";
import TableContainer from "@material-ui/core/TableContainer";
import Paper from "@material-ui/core/Paper";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableBody from "@material-ui/core/TableBody";
import {ImageListType} from "react-images-uploading";
import {
    getCruiseGroupForBusinessWorker, getCruiseGroupMetadata,
    getCruisesForCruiseGroup,
    publishCruise
} from "../../../Services/cruiseGroupService";
import IconButton from "@material-ui/core/IconButton";
import KeyboardArrowUpIcon from "@material-ui/icons/KeyboardArrowUp";
import KeyboardArrowDownIcon from "@material-ui/icons/KeyboardArrowDown";
import Collapse from "@material-ui/core/Collapse";
import Box from "@material-ui/core/Box";
import {dCruiseGroup} from "../../../components/ListCruiseGroup";
import axios from "../../../Services/URL";
import store from "../../../redux/store";
import PopupAcceptAction from "../../../PopupAcceptAction";
import Dialog from "../../../components/Dialog";
import {KeyboardDatePicker, KeyboardTimePicker, MuiPickersUtilsProvider} from "@material-ui/pickers";
import DateFnsUtils from "@date-io/date-fns";
import DarkedSelect from "../../../components/DarkedSelect";
import styles from "../../../styles/auth.global.module.css";
import pl from "date-fns/locale/pl";
import eng from "date-fns/locale/en-GB";
import ActiveIcon from "../../../components/ActiveIcon";
import {getCruiseMetadata} from "../../../Services/cruisesService";
import PopupMetadata from "../../../PopupMetadata";

const useRowStyles = makeStyles({
    root: {
        '& > *': {
            borderBottom: 'unset',
        },
    },
});


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


export function createCruiseGroup(
    price: any,
    start_time: any,
    end_time: any,
    numberOfSeats: any,
    name: any,
    cruisePictures: ImageListType,
    etag: string,
    version: bigint,
    uuid: string,
    description: any,
    active: boolean,
    company: any,
    cruises: any,
    published: boolean
) {
    return {
        price: price,
        start_time: start_time,
        end_time: end_time,
        numberOfSeats: numberOfSeats,
        name: name,
        cruisePictures: cruisePictures,
        uuid: uuid,
        etag: etag,
        version: version,
        description: description,
        active: active,
        company: company,
        cruises: cruises,
        published: published
    };
}

const useButtonStyles = makeStyles({
    root: {
        fontFamily: '"Montserrat", sans-serif',
        color: 'var(--white)',
        backgroundColor: "var(--blue)",
        padding: '8px 16px',
        margin: '0 16px',
        '&:hover': {
            backgroundColor: "var(--blue-dark)",
        }
    }
})


interface DeactivateCruiseData {
    uuid: string;
    etag: string;
    version: bigint,
    token: string
}

interface DeactivateCruise {
    uuid: string,
    etag: string,
    version: bigint
}
interface MetadataCruiseGroupe {
    uuid: string,
}
interface MetadataCruise {
    uuid: string,
}
const deactivateCruiseGroup = async ({uuid, etag, version, token}: DeactivateCruiseData) => {
    const json = JSON.stringify({
            uuid: uuid,
            version: version,
        }
    )
    return await axios.put('cruiseGroup/deactivate-cruise-group', json, {
        headers: {
            'Content-Type': 'application/json',
            'If-Match': etag,
            'Authorization': `Bearer ${token}`
        }
    })
}


export interface CruiseData {
    group: ReturnType<typeof createCruiseGroup>,
    style: React.CSSProperties,
    onChange: () => Promise<any>,
}

function Row(props: CruiseData) {
    const {group} = props;
    const {style} = props;
    const {onChange} = props;
    const {t} = useTranslation();
    const classes = useRowStyles();
    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')
    const token = useSelector(selectToken)
    const [open, setOpen] = useState(false);
    const [openDialog, setOpenDialog] = useState(false);
    const [cruises, setCruises] = useState([]);
    const buttonClass = useButtonStyles();

    const languageType = useSelector(selectLanguage);
    const darkMode = useSelector(selectDarkMode);
    const language = languageType === 'PL' ? pl : eng;
    const isPMAM = language === eng;

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
    const [metadataPopupAcceptAction, setMetadataPopupAcceptAction] = useState(false);
    const [cruiseMetadataPopupAcceptAction, setCruiseMetadataPopupAcceptAction] = useState(false);

    const [alterType, setAlterType] = useState('')
    const [alteredBy, setAlteredBy] = useState('')
    const [createdBy, setCreatedBy] = useState('')
    const [creationDateTime, setCreationDateTime] = useState('')
    const [lastAlterDateTime, setLastAlterDateTime] = useState('')
    const [versionM, setVersionM] = useState('')

    const handleMetadata = async ({uuid}: MetadataCruiseGroupe) =>{
        getCruiseGroupMetadata(uuid).then(res => {
            setAlterType(res.data.alterType);
            setAlteredBy(res.data.alteredBy);
            setCreatedBy(res.data.createdBy);
            if(res.data.creationDateTime !=null)
                setCreationDateTime(res.data.creationDateTime.dayOfMonth +" "+ t(res.data.creationDateTime.month) +" "+ res.data.creationDateTime.year +" "+ res.data.creationDateTime.hour +":"+ res.data.creationDateTime.minute.toString().padStart(2, '0'))
            if(res.data.lastAlterDateTime !=null)
                setLastAlterDateTime(res.data.lastAlterDateTime.dayOfMonth +" "+ t(res.data.lastAlterDateTime.month) +" "+ res.data.lastAlterDateTime.year +" "+ res.data.lastAlterDateTime.hour +":"+ res.data.lastAlterDateTime.minute.toString().padStart(2, '0'));
            setVersionM(res.data.version);
            refreshToken();
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        }).then(res => {
            setMetadataPopupAcceptAction(true);
        })
    }
    const handleCruiseMetadata = async ({uuid}: MetadataCruise) =>{
        getCruiseMetadata(uuid).then(res => {
            setAlterType(res.data.alterType);
            setAlteredBy(res.data.alteredBy);
            setCreatedBy(res.data.createdBy);
            if(res.data.creationDateTime !=null)
                setCreationDateTime(res.data.creationDateTime.dayOfMonth +" "+ t(res.data.creationDateTime.month) +" "+ res.data.creationDateTime.year +" "+ res.data.creationDateTime.hour +":"+ res.data.creationDateTime.minute.toString().padStart(2, '0'))
            if(res.data.lastAlterDateTime !=null)
                setLastAlterDateTime(res.data.lastAlterDateTime.dayOfMonth +" "+ t(res.data.lastAlterDateTime.month) +" "+ res.data.lastAlterDateTime.year +" "+ res.data.lastAlterDateTime.hour +":"+ res.data.lastAlterDateTime.minute.toString().padStart(2, '0'));
            setVersionM(res.data.version);
            refreshToken();
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        }).then(res => {
            setCruiseMetadataPopupAcceptAction(true);
        })
    }

    const [version, setVersion] = useState(0)

    const [uuid, setUUID] = useState('')
    const [etag, setEtag] = useState('')

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
    const HandleEditCruise = () => {
        setOpenDialog(false)
        if (!startDate || !endDate || !startTime || !endTime) {
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
                uuid: uuid,
                startDate: newStartDate.toISOString(),
                endDate: newEndDate.toISOString(),
                version: version
            })
            axios.put('cruise/edit-cruise', json, {
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json",
                    "Authorization": `Bearer ${token}`,
                    "If-Match": etag
                }
            }).then(res => {
                showSuccess(t('successful action'))
                forceUpdate()
                reload()
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


    const [deactivateCruise, setDeactivateCruise] = useState<DeactivateCruise>({
        uuid: "",
        etag: "",
        version: BigInt(0)
    });
    const [, forceUpdate] = useReducer(x => x + 1, 0);

    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);
    const [buttonPopupAcceptActionCruiseGroup, setButtonPopupAcceptActionCruiseGroup] = useState(false);
    const deactivateCruiseG = () => {
         deactivateCruiseGroup({
            uuid: group.uuid,
            etag: group.etag,
            version: group.version,
            token: token
        }).then(res => {
             setButtonPopupAcceptActionCruiseGroup(false)

             onChange().then(() => {
                showSuccess(t('successful action'))
            })
        }).catch(error => {
             setButtonPopupAcceptActionCruiseGroup(false)
             const message = error.response.data
            handleError(t(message), error.response.status)
        })
    }

    const handleConfirm = async ({uuid, etag, version}: DeactivateCruise) => {
        const {token} = store.getState();
        const json = JSON.stringify({
            uuid: uuid,
            version: version
        })

        await axios.put("cruise/deactivate-cruise", json, {
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json",
                    'Authorization': `Bearer ${token}`,
                    "If-Match": etag
                }
            }
        ).then(() => {
            setButtonPopupAcceptAction(false)
            showSuccess(t('successful action'))
            forceUpdate()
            refreshToken()
            reload()
        }).catch(error => {
            setButtonPopupAcceptAction(false)
            const message = error.response.data
            handleError(message, error.response.status)
        });
    }

    const reload = async () => {
        await getCruisesForCruiseGroup(group.uuid)
            .then(res => {
                setCruises(res.data)
                refreshToken();
            })
            .catch(error => {
                const message = error.response.data
                const status = error.response.status
                handleError(message, status)
            })
    }

    const handleSetOpen = async () => {
        setOpen(state => !state);
        await getCruisesForCruiseGroup(group.uuid)
            .then(res => {
                setCruises(res.data)
                refreshToken();
            })
            .catch(error => {
                const message = error.response.data
                const status = error.response.status
                handleError(message, status)
            })
    }


    const getParsedDate = (date: any) => {
        return `${date.dayOfMonth}-${date.monthValue.toString().padStart(2, '0')}-${date.year}`
    }
    const handleReservations = (props: any) => {
        const uuid = props
        sessionStorage.setItem("cruiseUUID", uuid)
    }
    const HandleChangeData = (props: any) => {
        sessionStorage.setItem("ChangeCruiseGroupData", JSON.stringify(props))
    }

    const classesDialog = useStyles()

    function publishCruiseHandler(cruise: any) {
        publishCruise(cruise.uuid, cruise.version, cruise.etag).then(res => {
            reload();
            refreshToken();
            showSuccess(t('successful action'));
        })
            .catch(error => {
                const message = error.response.data
                const status = error.response.status
                handleError(message, status)
            })
    }

    return (
        <React.Fragment>
            <TableRow className={classes.root}>
                <TableCell>
                    <IconButton aria-label="expand row" size="small" onClick={handleSetOpen}>
                        {open ? <KeyboardArrowUpIcon/> : <KeyboardArrowDownIcon/>}
                    </IconButton>
                </TableCell>
                <TableCell component="th" scope="row" style={style}>{group.name}</TableCell>
                <TableCell style={style}>{group.company.name}</TableCell>
                <TableCell style={style}>{group.uuid}</TableCell>
                <TableCell style={style}>{group.numberOfSeats}</TableCell>
                <TableCell style={style}>{group.price + " pln"}</TableCell>
                <TableCell style={style}>{group.description}</TableCell>
                <TableCell style={style}>
                    <RoundedButton
                        color="pink" onClick={() => {
                       setButtonPopupAcceptActionCruiseGroup(true)
                    }}
                        disabled={!group.active}
                    >
                        {t("deactivate")}
                    </RoundedButton>
                </TableCell>
                <TableCell style={style}>
                    {group.cruises.length > 0 ?
                        <RoundedButton
                            color="pink"
                            disabled={group.cruises.length}
                        >
                            {t("changeData")}
                        </RoundedButton> :
                        <Link to="/changeCruiseGroupData">
                            <RoundedButton
                                color="pink" onClick={() => {
                                HandleChangeData(group)
                            }}
                                disabled={group.cruises.length}
                            >
                                {t("changeData")}
                            </RoundedButton>
                        </Link>
                    }
                </TableCell>
                <TableCell align="center">
                    <RoundedButton color={"green"}
                                   className={buttonClass.root}
                                   onClick={() => {
                                       handleMetadata({
                                           uuid: group.uuid,
                                       })
                                       setMetadataPopupAcceptAction(true)
                                   }
                                   }>{t("metadata")}</RoundedButton>
                </TableCell>
                <PopupMetadata
                    open={metadataPopupAcceptAction}
                    onCancel={() => {setMetadataPopupAcceptAction(false)}}
                    alterType={alterType}
                    alteredBy={alteredBy}
                    createdBy={createdBy}
                    creationDateTime={creationDateTime}
                    lastAlterDateTime={lastAlterDateTime}
                    version={versionM}
                />
            </TableRow>
            <TableRow>
                <TableCell style={{paddingBottom: 0, paddingTop: 0}} colSpan={6}>
                    <Collapse in={open} timeout="auto" unmountOnExit>
                        <Box margin={1}>
                            <Table size="small" aria-label="clients">
                                <TableHead>
                                    <TableRow>
                                        <TableCell align="center" style={{
                                            backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                            color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                        }}>{t("start_date")}</TableCell>
                                        <TableCell align="center" style={{
                                            backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                            color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                        }}>{t("end_date")}</TableCell>
                                        <TableCell align="center" style={{
                                            backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                            color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                        }}>{t("active")}</TableCell>
                                        <TableCell align="center" style={{
                                            backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                            color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                        }}>{t("isPublished")}</TableCell>
                                        <TableCell align="center" style={{
                                            backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                            color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                        }}>{t("reservations")}</TableCell>
                                        <TableCell align="center" style={{
                                            backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                            color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                        }}>{t("attractions")}</TableCell>
                                        <TableCell align="center" style={{
                                            backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                            color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                        }}>{t("metadata")}</TableCell>
                                        <TableCell align="center" style={{
                                            backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                            color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                        }}>{t("publish")}</TableCell>
                                        <TableCell align="center" style={{
                                            backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                            color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                        }}>{t("changeData")}</TableCell>
                                        <TableCell align="center" style={{
                                            backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                            color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                        }}>{t("deactivate")}</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {cruises.map((cruise: any, index) => (
                                        <TableRow>
                                            <TableCell align="center"
                                                       style={style}>{getParsedDate(cruise.startDate)}</TableCell>
                                            <TableCell align="center"
                                                       style={style}>{getParsedDate(cruise.endDate)}</TableCell>
                                            <TableCell align="center"
                                                       style={style}><ActiveIcon active={cruise.active} /></TableCell>
                                            <TableCell align="center"
                                                       style={style}><ActiveIcon active={cruise.published} /></TableCell>
                                            <TableCell align="center">
                                                <Link to="/reservations">
                                                    <RoundedButton color={"blue"}
                                                        className={buttonClass.root} onClick={() => {
                                                        handleReservations(cruise.uuid)
                                                    }}
                                                    >{t("reservations")}</RoundedButton>
                                                </Link>
                                            </TableCell>
                                            <TableCell align="center">
                                                <Link to={`attractions/${cruise.uuid}/${cruise.published}`}>
                                                    <RoundedButton color={"blue"}
                                                        className={buttonClass.root}>
                                                        {t("attractions")}</RoundedButton>
                                                </Link>
                                            </TableCell>
                                                    <TableCell align="center">
                                                        <RoundedButton color={"green"}
                                                                       className={buttonClass.root}
                                                                       onClick={() => {
                                                                           handleCruiseMetadata({
                                                                               uuid: cruise.uuid
                                                                           })
                                                                           setCruiseMetadataPopupAcceptAction(true)
                                                                       }
                                                                       }>{t("metadata")}</RoundedButton>
                                                    </TableCell>
                                            <TableCell align="center">
                                                <RoundedButton color={"pink"}
                                                               className={buttonClass.root}
                                                               disabled={(cruise.published)}
                                                               onClick={() => publishCruiseHandler(cruise)}
                                                >
                                                    {(cruise.published) ? t("published") : t("publish")}
                                                </RoundedButton>

                                            </TableCell>

                                            <TableCell align="center">
                                                <RoundedButton color={"pink"} disabled={(cruise.published)}
                                                               className={buttonClass.root} onClick={() => {
                                                    setVersion(cruise.version)
                                                    setUUID(cruise.uuid)
                                                    setEtag(cruise.etag)
                                                    setOpenDialog(true)
                                                }}>{t("changeData")
                                                }</RoundedButton>
                                            </TableCell>
                                            <TableCell align="center">
                                                <RoundedButton color={"pink"}
                                                               disabled={!(cruise.published && cruise.active)}
                                                               className={buttonClass.root} onClick={() => {
                                                    setDeactivateCruise({
                                                        uuid: cruise.uuid,
                                                        etag: cruise.etag,
                                                        version: cruise.version,

                                                    })
                                                    setButtonPopupAcceptAction(true)
                                                }
                                                }>{t("deactivate")}</RoundedButton>
                                            </TableCell>

                                        </TableRow>
                                    ))}
                                </TableBody>
                                <PopupMetadata
                                    open={cruiseMetadataPopupAcceptAction}
                                    onCancel={() => {setCruiseMetadataPopupAcceptAction(false)}}
                                    alterType={alterType}
                                    alteredBy={alteredBy}
                                    createdBy={createdBy}
                                    creationDateTime={creationDateTime}
                                    lastAlterDateTime={lastAlterDateTime}
                                    version={versionM}
                                />
                                <PopupAcceptAction
                                    open={buttonPopupAcceptAction}
                                    onConfirm={() => {
                                        handleConfirm(deactivateCruise)
                                    }}
                                    onCancel={() => {
                                        setButtonPopupAcceptAction(false)
                                    }}/>
                            </Table>
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
            <Dialog
                open={openDialog}
                title={t('edit')}
                onConfirm={HandleEditCruise}
                onCancel={() => {
                    setOpenDialog(false)
                }}>
                <div>
                    <Box>
                        <MuiPickersUtilsProvider locale={language} utils={DateFnsUtils}>
                            <KeyboardDatePicker
                                style={{marginRight: 30}}
                                className={darkMode ? classesDialog.dark : classesDialog.light}
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
                                className={darkMode ? classesDialog.dark : classesDialog.light}
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
                                autoOk={true}
                                style={{marginRight: 30}}
                                className={darkMode ? classesDialog.dark : classesDialog.light}
                                disableToolbar
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
                                autoOk={true}
                                className={darkMode ? classesDialog.dark : classesDialog.light}
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
                </div>
            </Dialog>

            <PopupAcceptAction
                open={buttonPopupAcceptActionCruiseGroup}
                onConfirm={() => {
                    deactivateCruiseG()
                }}
                onCancel={() => {
                    setButtonPopupAcceptActionCruiseGroup(false)
                }}/>
        </React.Fragment>

    );
}

const ListCruiseGroups = () => {
    const [cruiseGroupL, setCruiseGroupL] = useState([]);
    const [searchInput, setSearchInput] = useState("");

    const handleError = useHandleError()

    const darkMode = useSelector(selectDarkMode)

    const worker_Company = useSelector(selectCompany);

    const getCruiseGroupFromWorker = () => {
        return getCruiseGroupForBusinessWorker(worker_Company).then(res => {
            setCruiseGroupL(res.data)
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
        }).then(res => {
            refreshToken()
        })
    }
    useEffect(() => {
        getCruiseGroupFromWorker()
    }, []);


    function search(rows: any[]) {
        if (Array.isArray(rows) && rows.length) {

            const filteredCruiseGroups = rows.filter(
                row => row.props.group.name.toLowerCase().indexOf(searchInput.toLowerCase()) > -1
            );

            filteredCruiseGroups.forEach(company => (CruiseGroups.includes(company.props.group.name) ?
                "" : CruiseGroups.push(company.props.group.name)));
            return filteredCruiseGroups

        } else {
            return rows;
        }
    }


    const CruiseGroups: String[] = [];

    const {t} = useTranslation()

    return (
        <div>
            <Autocomplete
                options={CruiseGroups}
                inputValue={searchInput}
                style={{width: 300, marginBottom: 16}}
                noOptionsText={t('no options')}
                onChange={(event, value) => {
                    setSearchInput(value as string ?? '')
                }}
                renderInput={(params) => (
                    <TextField {...params} label={t('search cruiseGroup')} variant="outlined"
                               onChange={(e) => setSearchInput(e.target.value)}/>
                )}
            />
            <TableContainer component={Paper} style={{
                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`
            }}>
                <Table aria-label="CruiseGroups">
                    <TableHead>
                        <TableRow>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t('expand cruises')}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("cruiseName")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("company name")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{"UUID"}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("numberOfSeats")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("price")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("description")}</TableCell>
                            <TableCell  style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("deactivate")}</TableCell>
                            <TableCell  style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("changeData")}</TableCell>
                            <TableCell align="center" style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("metadata")}</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {search(cruiseGroupL.map((cruiseGroups, index) => (
                            <Row key={index} group={cruiseGroups}  style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }} onChange={getCruiseGroupFromWorker}/>
                        )))}
                    </TableBody>
                </Table>
            </TableContainer>

        </div>
    );
};

export default ListCruiseGroups;