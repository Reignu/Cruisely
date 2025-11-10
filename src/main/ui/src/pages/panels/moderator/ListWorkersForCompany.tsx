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
import store from "../../../redux/store";
import axios from "../../../Services/URL";
import {TextField} from '@material-ui/core';
import RoundedButton from "../../../components/RoundedButton";
import {refreshToken} from "../../../Services/userService";
import {useSnackbarQueue} from "../../snackbar";
import useHandleError from "../../../errorHandler";
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
    style: React.CSSProperties
}

function Row(props: RowProps) {
    const handleError = useHandleError()
    const {row} = props;
    const {style} = props;
    const classes = useRowStyles();
    const showSuccess = useSnackbarQueue('success')
    const {t} = useTranslation()
    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);

    return (
        <TableRow className={classes.root}>
            <TableCell component="th" scope="row" style={style}>{row.login}</TableCell>
            <TableCell style={style}>{row.firstName}</TableCell>
            <TableCell style={style}>{row.secondName}</TableCell>
            <TableCell style={style}>{row.email}</TableCell>
            <TableCell style={style}>{row.phoneNumber}</TableCell>
        </TableRow>
    );
}

const getWorkers = (companyName: string) => {
    const {token} = store.getState()
    return axios.get(`company/${companyName}/business-workers`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
}


export default function ModListClient() {
    const [, forceUpdate] = useReducer(x => x + 1, 0);
    const [users, setUsers] = useState([]);
    const [searchInput, setSearchInput] = useState("");
    const darkMode = useSelector(selectDarkMode)
    const companyName = sessionStorage.getItem("currentCompanyName");

    useEffect(() => {
        getWorkers(companyName as string).then(res => {
            setUsers(res.data)
            refreshToken()
        })
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
            <h1 style={{
                color: `var(--${!darkMode ? 'dark' : 'white-light'}`,
                font: "Montserrat"
            }}>{companyName}: {t("business workers")}</h1>
            <br/>
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
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {search(users.map((user, index) => (
                            <Row key={index} row={user} style={{
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
