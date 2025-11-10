import React, {useEffect, useState} from 'react';
import {getAllCompanies, getCompanyMetadata} from "../../../Services/companiesService";
import {makeStyles} from "@material-ui/core/styles";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import useHandleError from "../../../errorHandler";
import {useSelector} from "react-redux";
import {selectDarkMode} from "../../../redux/slices/userSlice";
import {refreshToken} from "../../../Services/userService";
import {useTranslation} from "react-i18next";
import Autocomplete from "../../../components/Autocomplete";
import {Button, TextField} from "@material-ui/core";
import {Link} from "react-router-dom";
import TableContainer from "@material-ui/core/TableContainer";
import Paper from "@material-ui/core/Paper";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableBody from "@material-ui/core/TableBody";
import RoundedButton from "../../../components/RoundedButton";
import PopupMetadata from "../../../PopupMetadata";

const useRowStyles = makeStyles({
    root: {
        '& > *': {
            borderBottom: 'unset',
        },
    },
});

function createData(
    name: string,
    nip: number,
    phoneNumber: string,
    country: string,
    city: string,
    street: string,
    houseNumber: string,
    postalCode: string,
) {
    return {
        name: name,
        nip: nip,
        phoneNumber: phoneNumber,
        country: country,
        city: city,
        street: street,
        houseNumber: houseNumber,
        postalCode: postalCode,
    };
}

export interface RowProps {
    row: ReturnType<typeof createData>,
    style: React.CSSProperties
}

interface MetadataCompany {
    nip: string,
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


function Row(props: RowProps) {
    const {row} = props;
    const {style} = props;
    const {t} = useTranslation();
    const classes = useRowStyles();
    const buttonClass = useButtonStyles();
    const handleError = useHandleError();

    const [metadataPopupAcceptAction, setMetadataPopupAcceptAction] = useState(false);
    const [alterType, setAlterType] = useState('')
    const [alteredBy, setAlteredBy] = useState('')
    const [createdBy, setCreatedBy] = useState('')
    const [creationDateTime, setCreationDateTime] = useState('')
    const [lastAlterDateTime, setLastAlterDateTime] = useState('')
    const [version, setVersion] = useState('')

    const handleMetadata = async ({nip}: MetadataCompany) =>{
        getCompanyMetadata(nip).then(res => {
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
    return (
        <TableRow className={classes.root}>
            <TableCell component="th" scope="row" style={style}>{row.name}</TableCell>
            <TableCell style={style}>{row.nip}</TableCell>
            <TableCell style={style}>{row.phoneNumber}</TableCell>
            <TableCell style={style}>{row.country}</TableCell>
            <TableCell style={style}>{row.city}</TableCell>
            <TableCell style={style}>{row.street}</TableCell>
            <TableCell style={style}>{row.houseNumber}</TableCell>
            <TableCell style={style}>{row.postalCode}</TableCell>
            <TableCell style={style}>

                <Link to="/company/business-workers">
                    <RoundedButton color="pink" onClick={() => sessionStorage.setItem("currentCompanyName", row.name)}>
                        {t("show business workers")}
                    </RoundedButton>
                </Link>
            </TableCell>
            <TableCell style={style}>
                <RoundedButton color={"green"}
                               className={buttonClass.root}
                               onClick={() => {
                                   handleMetadata({
                                       nip: String(row.nip)
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

const ListCompany = () => {
    const [companiesL, setCompaniesL] = useState([]);
    const [searchInput, setSearchInput] = useState("");

    const handleError = useHandleError()

    const darkMode = useSelector(selectDarkMode)

    useEffect(() => {
        getAllCompanies().then(res => {
            setCompaniesL(res.data)
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
        }).then(res => {
            refreshToken()
        });
    }, []);


    function search(rows: any[]) {
        if (Array.isArray(rows) && rows.length) {

            const filteredCompanies = rows.filter(
                row => row.props.row.name.toLowerCase().indexOf(searchInput.toLowerCase()) > -1
            );

            filteredCompanies.forEach(company => (companies.includes(company.props.row.name) ?
                "" : companies.push(company.props.row.name)));
            return filteredCompanies

        } else {
            return rows;
        }
    }

    const companies: String[] = [];

    const {t} = useTranslation()

    return (
        <div>
            <Autocomplete
                options={companies}
                inputValue={searchInput}
                noOptionsText={t('no options')}
                onChange={(event, value) => {
                    setSearchInput(value as string ?? '')
                }}
                renderInput={(params) => (
                    <TextField {...params} label={t('search company')} variant="outlined"
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
                            }}>{t("company name")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{"NIP"}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("phoneNumber")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("country")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("city")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("street")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("houseNumber")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("postalCode")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("show business workers")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("metadata")}</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {search(companiesL.map((company, index) => (
                            <Row key={index} row={company} style={{
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

export default ListCompany;