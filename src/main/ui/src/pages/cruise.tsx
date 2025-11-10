import React, {useEffect, useState} from 'react';
import {useHistory, useParams} from "react-router-dom";
import {Card, CardActions, CardContent, Grid, Menu, MenuItem} from "@material-ui/core";
import {getCruiseByUUID, getRelatedCruises} from "../Services/cruisesService";
import styles from '../styles/Cruise.module.css';
import HeaderFooterLayout from "../layouts/HeaderFooterLayout";
import {useTranslation} from "react-i18next";
import PersonIcon from '@material-ui/icons/Person';
import AttachMoneyIcon from '@material-ui/icons/AttachMoney';
import DirectionsBoatIcon from '@material-ui/icons/DirectionsBoat';
import StarIcon from '@material-ui/icons/StarRounded';
import StarHalfIcon from '@material-ui/icons/StarHalfRounded';
import DeleteIcon from '@material-ui/icons/DeleteRounded';
import RoundedButton from "../components/RoundedButton";
import {getAttractionsByCruiseUUID} from "../Services/attractionService";
import {createReservation} from "../Services/reservationService";
import {useSnackbarQueue} from "./snackbar";
import {useSelector} from "react-redux";
import {selectActiveAccessLevel, selectLogin} from "../redux/slices/userSlice";
import StarSpinner from "../components/StarSpinner";
import {createRating, removeRating} from "../Services/ratingService";
import useHandleError from "../errorHandler";
import store from "../redux/store";

function useForceUpdate() {
    const [value, setValue] = useState(0); // integer state
    return () => setValue(value => value + 1); // update the state to force render
}

export default function Cruise() {
    const {id} = useParams<{ id: string }>();

    const login = useSelector(selectLogin);
    const activeAccessLevel = useSelector(selectActiveAccessLevel);
    const forceUpdate = useForceUpdate();

    const {t} = useTranslation();
    const showSuccess = useSnackbarQueue('success')
    const handleError = useHandleError();
    const numberOfSeatsList = [1, 2, 3, 4, 5]
    const [selectedNumberOfSeats, setSelectedNumberOfSeats] = useState(0)
    const [numberOfSeatsString, setNumberOfSeatsString] = useState("")

    const history = useHistory();

    const [cruise, setCruise] = useState<any>()
    const [relatedCruises, setRelatedCruises] = useState<any[]>([])
    const [attractions, setAttractions] = useState<any[]>([])
    const [selectedAttractions, setSelectedAttractions] = useState<string[]>([])

    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const [seatsAnchorEl, setSeatsAnchorEl] = React.useState<null | HTMLElement>(null);

    const handleMenuOpen = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handleSeatsMenuOpen = (event: React.MouseEvent<HTMLButtonElement>) => {
        setSeatsAnchorEl(event.currentTarget);
    };

    const handleMenuClose = () => {
        setAnchorEl(null);
    };

    const handleSeatsMenuClose = () => {
        setSeatsAnchorEl(null);
    };

    const handleCruiseOpen = (uuid: string) => () => {
        history.push(`/cruise/${uuid}`)
        document.location.reload()
    }

    const setNumberOfSeats = (number: number) => {
        setSelectedNumberOfSeats(number)
        setNumberOfSeatsString(`: ${number}`)
        handleSeatsMenuClose()
    }

    const bookCruise = () => {
        createReservation(cruise.version, id, selectedNumberOfSeats, selectedAttractions)
            .then(() => {
                showSuccess(t('successful action'))
            })
            .catch(error => {
                const message = error.response?.data
                handleError(message, error.response?.status)
            });
    }

    const isBookingDisabled = () => {
        const {token} = store.getState()
        return (numberOfSeatsString == "" || token == "")
    }

    const getCruise = () => {
        getCruiseByUUID(id)
            .then(res => {
                setCruise(res.data)

                getRelatedCruises(res.data.cruiseGroupDto.uuid).then(related => {
                    setRelatedCruises(related.data.filter((item: any) => item.uuid !== res.data.uuid))
                })

                getAttractionsByCruiseUUID(id).then(r => {
                    setAttractions(r.data)
                })
            })
            .catch(error => {
                history.push('/404')
            })
    }

    useEffect(() => {
        getCruise()
    }, [])

    const getDate = (date: any) => {
        if (!date) {
            return
        }

        const {
            dayOfMonth,
            monthValue,
            year
        } = date;

        return dayOfMonth + '.' + monthValue + '.' + year;
    }

    const handleCreateRating = (rating: number) => {
        createRating(rating, cruise?.cruiseGroupDto?.uuid).then(res => {
            showSuccess(t('successful action'))
            getCruise()
        })
    }

    const handleRemoveRating = () => {
        console.log(cruise?.cruiseGroupDto?.uuid)
        removeRating(cruise?.cruiseGroupDto?.uuid).then(res => {
            showSuccess(t('successfulaction'))
            getCruise()
        }).catch(error => {
            const message = error.response.data
            const status =  error.response.status
            handleError(message,status)
        })
    }

    const addAttraction = (uuid: string) => {
        let index = selectedAttractions.indexOf(uuid);
        if (index === -1) {
            selectedAttractions.push(uuid)
            setSelectedAttractions(selectedAttractions)
        }
        forceUpdate()
    }

    const deleteAttraction = (uuid: string) => {
        let index = selectedAttractions.indexOf(uuid);
        if (index !== -1) {
            selectedAttractions.splice(index, 1);
        }
        forceUpdate()
    }

    const isAttractionAdded = (uuid: string) => {
        let index = selectedAttractions.indexOf(uuid);
        return index !== -1
    }

    return (
        <HeaderFooterLayout>
            <Grid container className={styles.wrapper}>
                <Grid item xs className={styles.infowrap}>
                    <div>
                        <h3>{cruise?.cruiseGroupDto?.company.name}</h3>
                        <h1>{cruise?.cruiseGroupDto?.name}</h1>
                        <div>
                            {
                                new Array(Math.floor(cruise?.cruiseGroupDto?.avgRating || 0)).fill(1).map((item, index) => (
                                    <StarIcon key={index} fontSize="large" style={{fill: 'var(--yellow-dark)'}}/>
                                ))
                            }
                            {
                                cruise?.cruiseGroupDto?.avgRating % 1 >= 0.4 && cruise?.cruiseGroupDto?.avgRating % 1 <= 0.7 ? (
                                    <StarHalfIcon fontSize="large" style={{fill: 'var(--yellow-dark)'}}/>
                                ) : cruise?.cruiseGroupDto?.avgRating % 1 > 0.7 ? (
                                    <StarIcon fontSize="large" style={{fill: 'var(--yellow-dark)'}}/>
                                ) : null
                            }
                        </div>
                        <h4>{getDate(cruise?.startDate)} - {getDate(cruise?.endDate)}</h4>
                        <p className={styles.description}>
                            {cruise?.cruiseGroupDto?.description || t('no description')}
                        </p>
                        <div style={{display: 'flex', alignItems: 'center'}}>
                            <PersonIcon fontSize="large" style={{fill: 'var(--dark-dark)', marginRight: 8}}/>
                            {cruise?.realNumberOfSeats}/{cruise?.cruiseGroupDto?.numberOfSeats}
                        </div>
                        <div style={{display: 'flex', alignItems: 'center'}}>
                            <AttachMoneyIcon fontSize="large" style={{fill: 'var(--dark-dark)', marginRight: 8}}/>
                            {cruise?.cruiseGroupDto?.price}
                        </div>
                        <div style={{display: 'flex', alignItems: 'center'}}>
                            <DirectionsBoatIcon fontSize="large" style={{fill: 'var(--dark-dark)', marginRight: 8}}/>
                            <div>
                                <div>{`${cruise?.cruiseGroupDto?.cruiseAddress.street}, ${cruise?.cruiseGroupDto?.cruiseAddress.streetNumber} (${cruise?.cruiseGroupDto?.cruiseAddress.harborName})`}</div>
                                <div>{`${cruise?.cruiseGroupDto?.cruiseAddress.cityName}, ${cruise?.cruiseGroupDto?.cruiseAddress.countryName}`}</div>
                            </div>
                        </div>
                    </div>
                    <RoundedButton color="blue" style={{fontSize: 16}} onClick={handleSeatsMenuOpen}>
                        {t('choose_number_of_seats') + `${numberOfSeatsString}`}
                    </RoundedButton>
                    <Menu
                        id="number-of-seats"
                        anchorEl={seatsAnchorEl}
                        open={Boolean(seatsAnchorEl)}
                        onClose={handleSeatsMenuClose}
                        style={{height: 300}}>
                        {
                            numberOfSeatsList.map((index: number) => (
                                <MenuItem key={index} onClick={() => setNumberOfSeats(index)}>
                                    {index}
                                </MenuItem>
                            ))
                        }
                    </Menu>
                    <div style={{display: 'flex', justifyContent: 'space-between'}}>
                        <RoundedButton
                            color="blue"
                            style={{fontSize: 16}}
                            disabled={isBookingDisabled()}
                            onClick={numberOfSeatsString !== "" ? bookCruise : undefined}
                        >
                            {t('reserve')}
                        </RoundedButton>

                        <div>
                            <RoundedButton
                                disabled={!relatedCruises.length} color="dark"
                                onClick={relatedCruises.length ? handleMenuOpen : undefined}
                            >
                                {t(relatedCruises.length ? 'choose another date' : 'no dates')}
                            </RoundedButton>
                            <Menu
                                id="simple-menu"
                                anchorEl={anchorEl}
                                keepMounted
                                open={Boolean(anchorEl)}
                                onClose={handleMenuClose}
                                style={{height: 300}}
                            >
                                {
                                    relatedCruises.map(({uuid, startDate, endDate}: any, index: number) => (
                                        <MenuItem key={index} onClick={handleCruiseOpen(uuid)}>
                                            {`${getDate(startDate)} - ${getDate(endDate)}`}
                                        </MenuItem>
                                    ))
                                }
                            </Menu>
                        </div>
                    </div>
                </Grid>

                <Grid item xs={1}/>

                <Grid item xs className={styles.attractions}>
                    <h3 style={{marginRight: 24}}>{t('attractions')}</h3>
                    <div style={{padding: '0 24px', width: '100%', overflow: 'auto', height: '100%'}}>
                        {
                            attractions.length ?
                                attractions.map(({name, description, price, uuid}, index) => (
                                    <Card key={index} style={{marginBottom: 16}}>
                                        <CardContent>
                                            <h4>{name}</h4>
                                            <p>{description}</p>
                                            <div style={{display: 'flex', alignItems: 'center'}}>
                                                <AttachMoneyIcon style={{fill: 'var(--dark-dark)', marginRight: 8}}/>
                                                {price}
                                            </div>
                                        </CardContent>
                                        <CardActions>
                                            { selectedAttractions.includes(uuid)
                                                ? <RoundedButton color="pink"
                                                                 onClick={() => deleteAttraction(uuid)}>{t('delete.attraction')}</RoundedButton>
                                                : <RoundedButton color="yellow"
                                                                 onClick={() => addAttraction(uuid)}>{t('add.attraction')}</RoundedButton>
                                            }
                                        </CardActions>
                                    </Card>
                                ))
                                : <h4>{t('no attractions')}</h4>
                        }
                    </div>
                </Grid>

                <Grid item xs={1}/>

                <Grid item xs className={styles.ratings}>
                    <h3 style={{marginRight: 24}}>{t('ratings')}</h3>
                    <div style={{padding: '0 24px', width: '100%', overflow: 'auto', height: '100%'}}>
                        {
                            cruise?.cruiseGroupDto?.ratings.length ?
                                cruise?.cruiseGroupDto?.ratings.map(({login: rLogin, accountFirstName, accountSecondName, rating}: any, index: number) => (
                                    <Card key={index} style={{marginBottom: 16}}>
                                        <CardContent style={{
                                            display: 'flex',
                                            alignItems: 'center',
                                        }}>
                                            <div style={{flex: 1}}>
                                                <h4 style={{marginLeft: 6}}>{rLogin} ({accountFirstName} {accountSecondName})</h4>
                                                <div>
                                                    {
                                                        new Array(Math.floor(rating || 0)).fill(1).map((item, index) => (
                                                            <StarIcon key={index} fontSize="large"
                                                                      style={{fill: 'var(--yellow-dark)'}}/>
                                                        ))
                                                    }
                                                    {
                                                        rating % 1 == 0.5 ? (
                                                            <StarHalfIcon fontSize="large"
                                                                          style={{fill: 'var(--yellow-dark)'}}/>
                                                        ) : null
                                                    }
                                                </div>
                                            </div>
                                            {
                                                rLogin === login ?
                                                    <DeleteIcon fontSize="large" className={styles.delete}
                                                                onClick={handleRemoveRating}/> : null
                                            }

                                        </CardContent>
                                    </Card>
                                ))
                                : <h4>{t('no ratings')}</h4>
                        }
                    </div>
                    {
                        !cruise?.cruiseGroupDto?.ratings.find((item: any) => item.login === login) && activeAccessLevel !== '' ? (
                            <div style={{height: 200}}>
                                <StarSpinner onSubmit={handleCreateRating}/>
                            </div>
                        ) : null
                    }

                </Grid>
            </Grid>

        </HeaderFooterLayout>
    )
}