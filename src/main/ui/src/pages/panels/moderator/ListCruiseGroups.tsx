import React, {useEffect, useReducer, useState} from 'react';
import {makeStyles} from "@material-ui/core/styles";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import useHandleError from "../../../errorHandler";
import {useSelector} from "react-redux";
import Box from '@material-ui/core/Box';
import {selectDarkMode} from "../../../redux/slices/userSlice";
import {refreshToken} from "../../../Services/userService";
import Collapse from '@material-ui/core/Collapse';
import {Button, TextField} from "@material-ui/core";
import {useTranslation} from "react-i18next";
import Autocomplete from "../../../components/Autocomplete";
import TableContainer from "@material-ui/core/TableContainer";
import Paper from "@material-ui/core/Paper";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableBody from "@material-ui/core/TableBody";
import {ImageListType} from "react-images-uploading";
import {getAllCruiseGroup, getCruisesForCruiseGroup} from "../../../Services/cruiseGroupService";
import IconButton from "@material-ui/core/IconButton";
import KeyboardArrowUpIcon from "@material-ui/icons/KeyboardArrowUp";
import KeyboardArrowDownIcon from "@material-ui/icons/KeyboardArrowDown";
import {useSnackbarQueue} from "../../snackbar";
import {Link} from "react-router-dom";
import store from "../../../redux/store";
import axios from "../../../Services/URL";
import PopupAcceptAction from "../../../PopupAcceptAction";
import ActiveIcon from "../../../components/ActiveIcon";
import RoundedButton from "../../../components/RoundedButton";
import {getCruiseMetadata} from "../../../Services/cruisesService";
import PopupMetadata from "../../../PopupMetadata";


const useRowStyles = makeStyles({
    root: {
        '& > *': {
            borderBottom: 'unset',
        },
    },
});

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

export function createCruiseGroup(
    price: any,
    start_time: any,
    end_time: any,
    numberOfSeats: any,
    name: any,
    cruisePictures: ImageListType,
    etag: string,
    version: bigint,
    uuid: any,
    description: any,
    active: boolean,
    company: any,
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
    };
}

export interface CruiseData {
    group: ReturnType<typeof createCruiseGroup>,
    style: React.CSSProperties
}

interface DeactivateCruise {
    uuid: string,
    etag: string,
    version:bigint
}
interface MetadataCruise {
    uuid: string,
}

function Row(props: CruiseData) {
    const {group} = props;
    const {style} = props;
    const {t} = useTranslation();
    const [open, setOpen] = useState(false);
    const classes = useRowStyles();
    const handleError = useHandleError();
    const [cruises, setCruises] = useState([]);
    const darkMode = useSelector(selectDarkMode)
    const showSuccess = useSnackbarQueue('success')
    const buttonClass = useButtonStyles();
    const [deactivateCruise, setDeactivateCruise] = useState<DeactivateCruise>({
        uuid : "",
        etag : "",
        version: BigInt(0) });
    const [, forceUpdate] = useReducer(x => x + 1, 0);

    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);
    const [metadataPopupAcceptAction, setMetadataPopupAcceptAction] = useState(false);

    const [alterType, setAlterType] = useState('')
    const [alteredBy, setAlteredBy] = useState('')
    const [createdBy, setCreatedBy] = useState('')
    const [creationDateTime, setCreationDateTime] = useState('')
    const [lastAlterDateTime, setLastAlterDateTime] = useState('')
    const [version, setVersion] = useState('')

    const handleMetadata = async ({uuid}: MetadataCruise) =>{
        getCruiseMetadata(uuid).then(res => {
            setAlterType(res.data.alterType);
            setAlteredBy(res.data.alteredBy);
            setCreatedBy(res.data.createdBy);
            if(res.data.creationDateTime !=null)
                setCreationDateTime(res.data.creationDateTime.dayOfMonth +" "+ t(res.data.creationDateTime.month) +" "+ res.data.creationDateTime.year +" "+ res.data.creationDateTime.hour +":"+ res.data.creationDateTime.minute.toString().padStart(2, '0') )
            if(res.data.lastAlterDateTime !=null)
                setLastAlterDateTime(res.data.lastAlterDateTime.dayOfMonth +" "+ t(res.data.lastAlterDateTime.month) +" "+ res.data.lastAlterDateTime.year +" "+ res.data.lastAlterDateTime.hour +":"+ res.data.lastAlterDateTime.minute.toString().padStart(2, '0'));
            setVersion(res.data.version);
            refreshToken();
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        }).then(res => {
            setMetadataPopupAcceptAction(true);
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
                <TableCell style={style}><ActiveIcon active={group.active} /></TableCell>
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
                                        }}>{t("attractions")}</TableCell>
                                        <TableCell align="center" style={{
                                            backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                            color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                        }}>{t("deactivate")}</TableCell>
                                        <TableCell align="center" style={{
                                            backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                            color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                        }}>{t("metadata")}</TableCell>
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
                                                <Link to={`attractions/${cruise.uuid}`}><RoundedButton color="blue" className={buttonClass.root}>{t("attractions")}</RoundedButton></Link>
                                            </TableCell>
                                                <React.Fragment>
                                                    <TableCell align="center">
                                                        <RoundedButton color={"pink"}
                                                            disabled={!(cruise.published && cruise.active)}
                                                            className={buttonClass.root}  onClick={() => {
                                                            setDeactivateCruise({
                                                                uuid : cruise.uuid,
                                                                etag : cruise.etag,
                                                                version : cruise.version
                                                            })
                                                            setButtonPopupAcceptAction(true)
                                                        }
                                                        }>{t("deactivate")}</RoundedButton>
                                                    </TableCell>
                                                    <TableCell align="center">
                                                            <RoundedButton color={"green"}
                                                                           className={buttonClass.root}
                                                            onClick={() => {
                                                                handleMetadata({
                                                                    uuid: cruise.uuid
                                                                })
                                                                setMetadataPopupAcceptAction(true)
                                                            }
                                                            }>{t("metadata")}</RoundedButton>
                                                    </TableCell>

                                                </React.Fragment>
                                        </TableRow>
                                    ))}
                                </TableBody>
                                <PopupMetadata
                                    open={metadataPopupAcceptAction}
                                    onCancel={() => {setMetadataPopupAcceptAction(false)}}
                                    alterType={alterType}
                                    alteredBy={alteredBy}
                                    createdBy={createdBy}
                                    creationDateTime={creationDateTime}
                                    lastAlterDateTime={lastAlterDateTime}
                                    version={version}
                                />
                                <PopupAcceptAction
                                    open={buttonPopupAcceptAction}
                                    onConfirm={()=>{handleConfirm(deactivateCruise)}}
                                    onCancel={() => {setButtonPopupAcceptAction(false)
                                    }}/>
                            </Table>
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </React.Fragment>
    );
}

const ListCruiseGroups = () => {
    const [cruiseGroupL, setCruiseGroupL] = useState([]);
    const [searchInput, setSearchInput] = useState("");

    const handleError = useHandleError()

    const darkMode = useSelector(selectDarkMode)

    useEffect(() => {
        getAllCruiseGroup().then(res => {
            setCruiseGroupL(res.data)
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
        }).then(res => {
            refreshToken()
        });
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
                            <TableCell>{t('expand cruises')}</TableCell>
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
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("active")}</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {search(cruiseGroupL.map((cruiseGroups, index) => (
                            <Row key={index} group={cruiseGroups} style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}/>
                        )))}
                    </TableBody>
                </Table>
            </TableContainer>
        </div>
    );
};

export default ListCruiseGroups;