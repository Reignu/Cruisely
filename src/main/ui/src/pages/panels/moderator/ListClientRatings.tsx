import React, {Component, Ref, useEffect, useImperativeHandle, useRef, useState} from 'react';
import {getClientRatings, removeClientRating} from "../../../Services/ratingsService";
import {makeStyles} from "@material-ui/core/styles";
import {useTranslation} from "react-i18next";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import {Button, TextField} from "@material-ui/core";
import styles from "../../../styles/Header.module.css";
import useHandleError from "../../../errorHandler";
import {useSelector} from "react-redux";
import {selectDarkMode} from "../../../redux/slices/userSlice";
import {refreshToken} from "../../../Services/userService";
import Autocomplete from "../../../components/Autocomplete";
import TableContainer from "@material-ui/core/TableContainer";
import Paper from "@material-ui/core/Paper";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableBody from "@material-ui/core/TableBody";
import {useSnackbarQueue} from "../../snackbar";
import RoundedButton from "../../../components/RoundedButton";
import PopupMetadata from "../../../PopupMetadata";
import {getRatingMetadata} from "../../../Services/ratingService";
import PopupAcceptAction from "../../../PopupAcceptAction";

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
function createData(
    login: string,
    cruiseGroupName: string,
    cruiseGroupUUID: string,
    rating: number,
    ratingUuid: string,
) {
    return {
        login: login,
        cruiseGroupName: cruiseGroupName,
        cruiseGroupUUID: cruiseGroupUUID,
        rating: rating,
        ratingUuid: ratingUuid,
    };
}
interface MetadataRating {
    uuid: string,
}
export interface RowProps {
    row: ReturnType<typeof createData>,
    style: React.CSSProperties,
    onReload: () => void
    setButtonPopupAcceptActionMethod: (val: boolean) => (void),
    removeRatingRef: Ref<{ removeRating: () => void }>
}

function Row({row,style, onReload, setButtonPopupAcceptActionMethod, removeRatingRef}: RowProps) {
    const {t} = useTranslation();

    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')
    const classes = useRowStyles();


    const buttonClass = useButtonStyles();

    const [metadataPopupAcceptAction, setMetadataPopupAcceptAction] = useState(false);
    const [alterType, setAlterType] = useState('')
    const [alteredBy, setAlteredBy] = useState('')
    const [createdBy, setCreatedBy] = useState('')
    const [creationDateTime, setCreationDateTime] = useState('')
    const [lastAlterDateTime, setLastAlterDateTime] = useState('')
    const [version, setVersion] = useState('')

    const handleMetadata = async ({uuid}: MetadataRating) =>{
        getRatingMetadata(uuid).then(res => {
            setAlterType(res.data.alterType);
            setAlteredBy(res.data.alteredBy);
            setCreatedBy(res.data.createdBy);
            if(res.data.creationDateTime !=null)
                setCreationDateTime(res.data.creationDateTime.dayOfMonth +" "+ t(res.data.creationDateTime.month) +" "+ res.data.creationDateTime.year +" "+ res.data.creationDateTime.hour +":"+ res.data.creationDateTime.minute.toString().padStart(2, '0').toString().padStart(2, '0') )
            if(res.data.lastAlterDateTime !=null)
                setLastAlterDateTime(res.data.lastAlterDateTime.dayOfMonth +" "+ t(res.data.lastAlterDateTime.month) +" "+ res.data.lastAlterDateTime.year +" "+ res.data.lastAlterDateTime.hour +":"+ res.data.lastAlterDateTime.minute.toString().padStart(2, '0').toString().padStart(2, '0'));
            setVersion(res.data.version);
            refreshToken();
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        }).then(res => {
            setMetadataPopupAcceptAction(true);
        })
    }

    useImperativeHandle(removeRatingRef, () => ({removeRating}))
    const removeRating = () => {
        removeClientRating(row.login, row.cruiseGroupUUID).then(res => {
            setButtonPopupAcceptActionMethod(false)
            showSuccess(t('data.delete.success'))
            onReload()
        }).catch(error => {
            setButtonPopupAcceptActionMethod(false)
            const message = error.response.data
            handleError(message, error.response.status)
            onReload()
        }).then(res => {
            setButtonPopupAcceptActionMethod(false)
            refreshToken()
        });
    }

    return (
        <TableRow className={classes.root}>
            <TableCell component="th" scope="row" style={style}>{row.cruiseGroupName}</TableCell>
            <TableCell style={style}>{row.rating}</TableCell>
            <TableCell style={style}>
                <RoundedButton color="pink" onClick={() => {
                    setButtonPopupAcceptActionMethod(true)
                }}>
                    {t("remove rating")}
                </RoundedButton>
            </TableCell>
            <TableCell style={style}>
                <RoundedButton color={"green"}
                               className={buttonClass.root}
                               onClick={() => {
                                   handleMetadata({
                                       uuid: row.ratingUuid
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
                version={version}
            />
        </TableRow>
    );
}

const ListClientRatings = () => {
    const [ratings, setRatings] = useState([]);
    const [searchInput, setSearchInput] = useState("");
    const login = sessionStorage.getItem("login")!;
    const handleError = useHandleError();
    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);
    const darkMode = useSelector(selectDarkMode)

    useEffect(() => {
        onReload()
    }, []);

    function onReload() {
        getClientRatings(login).then(res => {
            setRatings(res.data)
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
        }).then(res => {
            refreshToken()
        });
    }

    function search(rows: any[]) {
        if (Array.isArray(rows) && rows.length) {

            const filteredRatings = rows.filter(
                row => row.props.row.cruiseGroupName.toLowerCase().indexOf(searchInput.toLowerCase()) > -1
            );

            filteredRatings.forEach(rating => (searchRatingList.includes(rating.props.row.cruiseGroupName) ?
                "" : searchRatingList.push(rating.props.row.cruiseGroupName)));
            return filteredRatings

        } else {
            return rows;
        }
    }

    const searchRatingList: String[] = [];

    const setButtonPopupAcceptActionMethod = (val: boolean) => {
        setButtonPopupAcceptAction(val)
    }

    const {t} = useTranslation()

    const removeRatingRef = useRef<{ removeRating: () => void }>(null);

    return (
        <>
            <div>
                <h3>{login}</h3>
                <Autocomplete
                    options={searchRatingList}
                    inputValue={searchInput}
                    noOptionsText={t('no options')}
                    onChange={(event, value) => {
                        setSearchInput(value as string ?? '')
                    }}
                    renderInput={(params) => (
                        <TextField {...params} label={t('search cruise')} variant="outlined"
                                   onChange={(e) => setSearchInput(e.target.value)}/>
                    )}
                />

                <TableContainer component={Paper}>
                    <Table aria-label="Companies">
                        <TableHead>
                            <TableRow>
                                <TableCell style={{
                                    backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                    color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                }}>{t("cruiseGroupName")}</TableCell>
                                <TableCell style={{
                                    backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                    color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                }}>{t("rating")}</TableCell>
                                <TableCell style={{
                                    backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                    color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                }}>{t("remove button")}</TableCell>
                                <TableCell style={{
                                    backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                    color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                }}>{t("metadata")}</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {search(ratings.map((rating, index) => (
                                <Row key={index} row={rating} style={{
                                    backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                    color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                                }} onReload={onReload}
                                     setButtonPopupAcceptActionMethod={setButtonPopupAcceptActionMethod}
                                     removeRatingRef={removeRatingRef}/>
                            )))}
                        </TableBody>
                    </Table>
                </TableContainer>
                <PopupAcceptAction
                    open={buttonPopupAcceptAction}
                    onConfirm={() => {
                        if (removeRatingRef.current) {
                            removeRatingRef.current.removeRating()
                        }
                    }}
                    onCancel={() => {
                        setButtonPopupAcceptAction(false)
                    }}/>
            </div>
        </>
    );
};

export default ListClientRatings;