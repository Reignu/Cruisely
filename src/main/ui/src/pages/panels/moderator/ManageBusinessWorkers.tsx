import React, {useEffect, useReducer, useState} from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import {useTranslation} from "react-i18next";
import {useSelector} from "react-redux";
import {selectDarkMode} from "../../../redux/slices/userSlice";
import DarkedTextField from "../../../components/DarkedTextField";
import store from "../../../redux/store";
import axios from "../../../Services/URL";
import {Button, TextField} from '@material-ui/core';
import RoundedButton from "../../../components/RoundedButton";
import {refreshToken} from "../../../Services/userService";
import {useSnackbarQueue} from "../../snackbar";
import useHandleError from "../../../errorHandler";
import PopupAcceptAction from "../../../PopupAcceptAction";
import Autocomplete from "../../../components/Autocomplete";

const useRowStyles = makeStyles({
    root: {
        '& > *': {
            borderBottom: 'unset',
        },
    },
});

function createData(
    login: string,
    firstName: string,
    secondName: string,
    email: string,
    phoneNumber: string,
    companyName: string,
    companyPhoneNumber: string,
    version: string
) {
    return {
        login: login,
        firstName: firstName,
        secondName: secondName,
        email: email,
        phoneNumber: phoneNumber,
        companyName: companyName,
        companyPhoneNumber: companyPhoneNumber,
        version: version

    };
}


export interface RowProps {
    row: ReturnType<typeof createData>,
    style: React.CSSProperties,
    onChange: () => Promise<any>,
}

function Row(props: RowProps) {
    const {
        onChange
    } = props;
    const handleError = useHandleError()
    const [, forceUpdate] = useReducer(x => x + 1, 0);
    const { row } = props;
    const { style } = props;
    const classes = useRowStyles();
    const showSuccess = useSnackbarQueue('success')
    const {t} = useTranslation()
    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);

    const confirmWorker = async(props: any)=> {
        const {token} = store.getState()
        const {row} = props;
        const json = JSON.stringify({
            login: row.login,
            version: row.version
        })

        await axios.put('account/confirm-business-worker', json, {
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "If-Match": row.etag,
                "Authorization": `Bearer ${token}`,

            }
        }).then(res =>{
            refreshToken()
            showSuccess(t('successful action'))

        },error =>{
            const message = error.response.data
            handleError(message, error.response.status)
        });
        return false;
    }
    return (
        <TableRow className={classes.root}>
            <PopupAcceptAction
                open={buttonPopupAcceptAction}
                onConfirm={() => {
                    confirmWorker({row}).then(res => {
                    onChange()})
                    setButtonPopupAcceptAction(false)
                }}
                onCancel={() => {setButtonPopupAcceptAction(false)
                }}
            />
            <TableCell component="th" scope="row" style={style}>
                {row.login}
            </TableCell>
            <TableCell style={style}>{row.firstName}</TableCell>
            <TableCell style={style}>{row.secondName}</TableCell>
            <TableCell style={style}>{row.email}</TableCell>
            <TableCell style={style}>{row.phoneNumber}</TableCell>
            <TableCell style={style}>{row.companyName}</TableCell>
            <TableCell style={style}>{row.companyPhoneNumber}</TableCell>
            <TableCell style={style}><RoundedButton color="blue" onClick={() =>{
                {setButtonPopupAcceptAction(true)
                }}

            }>{t("confirm")}</RoundedButton></TableCell>
        </TableRow>
    );



}



export default function ModListClient() {
    const [, forceUpdate] = useReducer(x => x + 1, 0);
    const [users, setUsers] = useState([]);
    const handleError = useHandleError()
    const [searchInput, setSearchInput] = useState("");
    const darkMode = useSelector(selectDarkMode)
    const getWorkers = () => {
        const {token} = store.getState()
        return axios.get('account/unconfirmed-business-workers', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        }).then(res => {
            setUsers(res.data)
            refreshToken()
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
        })
    }
    useEffect(() => {
        getWorkers()
    }, []);

    function search(rows: any[]) {
        if (Array.isArray(rows) && rows.length) {
            const filteredAccount = rows.filter(
                row => row.props.row.firstName.concat(" ", row.props.row.secondName).toLowerCase().indexOf(searchInput.toLowerCase()) > -1
            );

            filteredAccount.forEach(account => (accounts.includes(account.props.row.firstName + " " + account.props.row.secondName) ?
                "" : accounts.push(account.props.row.firstName + " " + account.props.row.secondName)));
            return filteredAccount
        } else {
            return rows;
        }
    }

    const accounts: String[] = [];


    const {t} = useTranslation()
    return (
        <div>
            <div>
                <Autocomplete
                    options={accounts}
                    inputValue={searchInput}
                    noOptionsText={t('no options')}
                    onChange={(event, value) => {
                        setSearchInput(value as string ?? '')
                    }}
                    renderInput={(params) => (
                        <TextField {...params} label={t('search business workers')} variant="outlined"
                                   onChange={(e) => setSearchInput(e.target.value)}/>
                    )}
                />
            </div>

            <TableContainer component={Paper}>
                <Table aria-label="Business Workers">
                    <TableHead>
                        <TableRow>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("login")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("first name")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("last name")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("email")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("phone number")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("company name")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("company phone number")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("confirm")}</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {search(users.map((user, index) => (
                            <Row key={index} row={user} onChange={getWorkers} style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}/>
                        )))}
                    </TableBody>
                </Table>
            </TableContainer>
        </div>

    );
}
