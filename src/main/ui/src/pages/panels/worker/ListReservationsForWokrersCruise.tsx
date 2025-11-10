import React, {useEffect, useState} from 'react';
import {getAllCompanies} from "../../../Services/companiesService";
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
import {getReservationMetadata, getReservationsForWorkerCruise} from "../../../Services/reservationService";
import RoundedButton from "../../../components/RoundedButton";
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
function createData(
    clientName: string,
    numberOfSeats: number,
    price: number,
    cruiseName: string,
    attractions: [],
    uuid: string,
) {
    return {
        clientName: clientName,
        numberOfSeats: numberOfSeats,
        price: price,
        cruiseName: cruiseName,
        attractions: attractions,
        uuid: uuid,
    };
}

export interface RowProps {
    row: ReturnType<typeof createData>,
    style: React.CSSProperties
}
interface MetadataReservation {
    uuid: string,
}
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

    const handleMetadata = async ({uuid}: MetadataReservation) =>{
        getReservationMetadata(uuid).then(res => {
            setAlterType(res.data.alterType);
            setAlteredBy(res.data.alteredBy);
            setCreatedBy(res.data.createdBy);
            if(res.data.creationDateTime !=null)
                setCreationDateTime(res.data.creationDateTime.dayOfMonth +" "+ t(res.data.creationDateTime.month) +" "+ res.data.creationDateTime.year +" "+ res.data.creationDateTime.hour +":"+ res.data.creationDateTime.minute.toString().padStart(2, '0'))
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
            <TableCell component="th" scope="row" style={style}>{row.clientName}</TableCell>
            <TableCell style={style}>{row.numberOfSeats}</TableCell>
            <TableCell style={style}>{row.price}</TableCell>
            <TableCell style={style}>{row.cruiseName}</TableCell>
            <TableCell style={style}>{row.attractions.map(item => t(item)).join(', ')}</TableCell>
            <TableCell style={style}>
                <RoundedButton color={"green"}
                               className={buttonClass.root}
                               onClick={() => {
                                   handleMetadata({
                                       uuid: row.uuid,
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

const ListReservationsForWorker = () => {
    const [reservations, setReservations] = useState([]);
    const [searchInput, setSearchInput] = useState("");

    const handleError = useHandleError()

    const darkMode = useSelector(selectDarkMode)
    const uuid =sessionStorage.getItem("cruiseUUID")
    useEffect(() => {

        getReservationsForWorkerCruise(uuid).then(res => {
            setReservations(res.data)
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
        }).then(res => {
            refreshToken()
        });
    }, []);


    function search(rows: any[]) {
        if (Array.isArray(rows) && rows.length) {

            const filteredReservations = rows.filter(
                row => row.props.row.clientName.toLowerCase().indexOf(searchInput.toLowerCase()) > -1
            );

            filteredReservations.forEach(reservation => (reservationss.includes(reservation.props.row.clientName) ?
                "" : reservationss.push(reservation.props.row.clientName)));
            return filteredReservations

        } else {
            return rows;
        }
    }

    const reservationss: String[] = [];

    const {t} = useTranslation()

    return (
        <div>
            <h3>{t("/reservations")}</h3>
            <Autocomplete
                options={reservationss}
                inputValue={searchInput}
                noOptionsText={t('no options')}
                onChange={(event, value) => {
                    setSearchInput(value as string ?? '')
                }}
                renderInput={(params) => (
                    <TextField {...params} label={t('search reservation')} variant="outlined"
                               onChange={(e) => setSearchInput(e.target.value)}/>
                )}
            />
            <TableContainer component={Paper}>
                <Table aria-label="Reservations">
                    <TableHead>
                        <TableRow>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("Client name")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("numberOfSeatsReserved")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("price")+" pln"}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("CruiseName")}</TableCell>
                            <TableCell style={{
                                backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                                color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("Attractions name")}</TableCell>
                            <TableCell style={{
                            backgroundColor: `var(--${!darkMode ? 'white' : 'dark-light'}`,
                            color: `var(--${!darkMode ? 'dark' : 'white-light'}`
                            }}>{t("metadata")}</TableCell>
                    </TableRow>
                    </TableHead>
                    <TableBody>
                        {search(reservations.map((reservation, index) => (
                            <Row key={index} row={reservation} style={{
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

export default ListReservationsForWorker;