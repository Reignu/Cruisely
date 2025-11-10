import ArrowIcon from '@material-ui/icons/ExpandMoreRounded'

import CruiseCard from '../components/CruiseCard'

import HeaderFooterLayout from '../layouts/HeaderFooterLayout'

import styles from '../styles/Home.module.css'

import { Link } from 'react-router-dom';
import {useTranslation} from 'react-i18next'
import {useSelector} from "react-redux";
import {selectDarkMode, selectLanguage} from "../redux/slices/userSlice";
import React, {useEffect, useState} from "react";
import {getPublishedCruises} from "../Services/cruisesService";
import store from "../redux/store";
import useHandleError from "../errorHandler";
import DateFnsUtils from "@date-io/date-fns";
import {KeyboardDatePicker, MuiPickersUtilsProvider} from '@material-ui/pickers';
import pl from "date-fns/locale/pl";
import eng from "date-fns/locale/en-GB";
import {makeStyles} from "@material-ui/styles";
import RoundedButton from "../components/RoundedButton";
import {Box, TextField} from "@material-ui/core";
import Autocomplete from "../components/Autocomplete";


const useStyles = makeStyles(theme => ({
    light: {
        '& .MuiFormLabel-root, & .MuiInputBase-input': {
            color: 'var(--dark)',
        },
        '& .MuiSvgIcon-root': {
            fill: 'var(--dark)',
        },
        '& .MuiInput-underline::before, & .MuiInput-underline::after, & .MuiInput-underline:hover:not(.Mui-disabled):before' : {
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
        '& .MuiInput-underline::before, & .MuiInput-underline::after, & .MuiInput-underline:hover:not(.Mui-disabled):before' : {
            borderColor: 'var(--white)',
        },
    },
}));



export interface IDate {
    dayOfMonth: number,
    year: number,
    monthValue: number,
}

export interface Cruise {
    price: number,
    name: string,
    img:string,
    relatedCruises: {
        uuid: string,
        endDate: IDate,
        startDate: IDate,
    }[],
}


function createData(
    price: number,
    name: string,
    img: string,

) {
    return {
        price: price,
        name: name,
        img: img,
    };
}


export interface RowProps {
    row: ReturnType<typeof createData>,
}

function Row(props: RowProps) {
    const {

    } = props;


    const {row} = props;

    return (
        <React.Fragment>
            <CruiseCard
                price = {row.price}
                title = {row.name}
                img = {row.img}
            />
        </React.Fragment>
    );
}

export default function Home() {
    const [cruises, setCruises] = useState<Cruise[]>([]);
    const [filteredCruises, setFilteredCruises] = useState<Cruise[]>([]);
    const handleError = useHandleError()

    const {t} = useTranslation()



    const HandleFilterByName = (name : string) => {
        if(!name || name == '') {
            setFilteredCruises(cruises)
            return
        }
        const filtered = cruises.filter(item => {
            return item.name.includes(name)
        })
        setFilteredCruises(filtered)

        }


    const HandleFilterByDateCruise = () => {

        const filtered = cruises.filter(item => {
            return item.relatedCruises.find(r => {
                const {
                    year: sy,
                    monthValue: sm,
                    dayOfMonth: sd,
                } = r.startDate;

                const {
                    year: ey,
                    monthValue: em,
                    dayOfMonth: ed,
                } = r.endDate;

                const cruiseStartDate = new Date(sy, sm - 1, sd, 0, 0, 0, 0);
                const cruiseEndDate = new Date(ey, em - 1, ed, 0, 0, 0, 0);

                startDate?.setHours(0, 0, 0,0)
                endDate?.setHours(0, 0, 0,0)

                return (startDate ? startDate.getTime() <= cruiseStartDate.getTime() : true) &&
                    (endDate ? endDate.getTime() >= cruiseEndDate.getTime() : true)
            })
        })
        setFilteredCruises(filtered)

        handleFilteredStartDate(startDate)
        handleFilteredEndDate(endDate)



    }

    const HandleFilterByLowerPriceCruise = () => {
        setFilteredCruises(prev => {
            prev.sort((a, b) => (b.price - a.price))
            return [...prev];
        })
    }


    const HandleFilterByHigherPriceCruise = () => {
        setFilteredCruises(prev => {
            prev.sort((a, b) => (a.price - b.price))
            return [...prev];
        })
    }

    const darkMode = useSelector(selectDarkMode)
    const headersArray = t("mainPage.headers", {returnObjects: true})


    const [filteredStartDate, setFilteredStartDate] = React.useState<Date | null>();
    const [filteredEndDate, setFilteredEndDate] = React.useState<Date | null>();

    const handleFilteredStartDate = (date: Date | null) => {
        setFilteredStartDate(date);
    };
    const handleFilteredEndDate = (date: Date | null) => {
        setFilteredEndDate(date);
    };


    const [startDate, setStartDate] = React.useState<Date | null>(
        new Date(),
    );

    const handleStartDateChange = (date: Date | null) => {
        setStartDate(date);
    };



    const [endDate, setEndDate] = React.useState<Date | null>(
        new Date(),
    );

    const handleEndDateChange = (date: Date | null) => {
        setEndDate(date);
    };


    const languageType = useSelector(selectLanguage);

    const language = languageType === 'PL' ? pl : eng;


    const [searchInput, setSearchInput] = useState("");

    useEffect(() => {
        getPublishedCruises().then(res => {
            setCruises(res.data)
            setFilteredCruises(res.data)
            setCruisesNameList(res.data.map(({name}: Cruise)  => name))

        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
        })

    }, []);


    const classes = useStyles()
    const [cruisesNameList, setCruisesNameList] = useState<string[]>([])

    return (
        <HeaderFooterLayout>
            <header className={styles.header}>
                <h1>{headersArray[Math.floor(Math.random() * headersArray.length)]}</h1>
                <ArrowIcon className={styles.arrow}/>
            </header>

            <Box style={{
                display: 'flex',
                margin: '32px 60px',
                padding: 16,
                backgroundColor: 'var(--yellow)',
                justifyContent: 'space-around',
                alignItems: 'center',
                borderRadius: 16,
            }}>
                <Autocomplete
                    colorIgnored
                    options={cruisesNameList}
                    inputValue={searchInput}
                    noOptionsText={t('no options')}
                    onChange={(event, value) => {
                        setSearchInput(value as string ?? '')
                        HandleFilterByName(value)
                    }}
                    renderInput={(params) => (
                        <TextField {...params} label={t('search cruise')} variant="outlined"
                                   onChange={(e) => {
                                       setSearchInput(e.target.value)
                                       HandleFilterByName(e.target.value)
                                   }}/>
                    )}
                />
                <Box style={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}>
                    <Box>
                        <MuiPickersUtilsProvider locale={language} utils={DateFnsUtils} >
                            <KeyboardDatePicker
                                style={{marginRight: 30}}
                                className={classes.light}
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
                                label={t("startDate")}
                                value={startDate}
                                onChange={handleStartDateChange}
                                KeyboardButtonProps={{
                                    'aria-label': 'change date',
                                }}
                            />
                            <KeyboardDatePicker
                                className={classes.light}
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
                                label={t("endDate")}
                                value={endDate}
                                onChange={handleEndDateChange}
                                KeyboardButtonProps={{
                                    'aria-label': 'change date',
                                }}
                            />
                        </MuiPickersUtilsProvider>
                    </Box>
                    <RoundedButton
                        onClick={HandleFilterByDateCruise}
                        style={{width: '100%', fontSize: '1.2rem', padding: '10px 0', marginBottom: 20}}
                        color="pink"
                    >{t("filterCruises")} </RoundedButton>
                </Box>

                <Box style={{
                    display: 'flex',
                    flexDirection: 'column',
                }}>
                    <RoundedButton
                        onClick={HandleFilterByLowerPriceCruise}
                        style={{fontSize: '1.2rem', padding: '10px', marginBottom: 20}}
                        color="pink"
                    >{t("filterByHigherPrices")} </RoundedButton>
                    <RoundedButton
                        onClick={HandleFilterByHigherPriceCruise}
                        style={{fontSize: '1.2rem', padding: '10px'}}
                        color="pink"
                    >{t("filterByLowerPriceCruises")} </RoundedButton>
                </Box>
            </Box>

            <section className={styles['top-cruises']}>
                <h2 className={styles.h2}
                    style={{color: `var(--${darkMode ? 'dark' : 'white'}`}}>{t("the best cruises")}</h2>

                <div className={styles['cruises-grid']}>
                    {filteredCruises.map(({relatedCruises, price, name, img}, index) =>(
                        <Link
                            key={index}
                            to={`/cruise/${relatedCruises[0].uuid}`}
                        >
                            <CruiseCard
                                price = {price}
                                title = {name}
                                img = {img}
                            />
                        </Link>
                    ))}
                </div>
            </section>
        </HeaderFooterLayout>
    )
}
