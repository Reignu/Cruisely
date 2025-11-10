import React, {useEffect, useState} from "react";
import {Attraction} from '../../../interfaces/Attraction';
import {getAttractionsByCruiseUUID} from "../../../Services/attractionService";
import {useSnackbarQueue} from "../../snackbar";
import {useTranslation} from "react-i18next";
import { useParams } from 'react-router-dom';
import {DataGrid, GridColDef, plPLGrid, enUS} from '@material-ui/data-grid';
import {makeStyles} from "@material-ui/styles";
import {useSelector} from "react-redux";
import {selectDarkMode, selectLanguage} from "../../../redux/slices/userSlice";

export default function AttractionList() {
    const darkMode = useSelector(selectDarkMode);
    const classes = makeStyles(theme => ({
        root: {
            color: `var(--${darkMode ? 'white' : 'dark'})`,
        }
    }))()

    const { uuid } = useParams<{ uuid: string }>();

    const language = useSelector(selectLanguage)
    const {t} = useTranslation();

    const showSuccess = useSnackbarQueue('success');
    const showError = useSnackbarQueue('error');

    const [attractions, setAttractions] = useState<Attraction[]>([]);

    useEffect(() => {
        getAttractionsByCruiseUUID(uuid).then(res => {
            setAttractions(res.data)
        }).catch(error => {
            showError(t(error))
        })
    }, [])

    const cols: GridColDef[] = [
        { field: 'name', headerName: t('attractionName'), flex: 1 },
        { field: 'description', headerName: t('description'), flex: 1 },
        { field: 'price', headerName: t('price'), flex: 1 },
        { field: 'numberOfSeats', headerName: t('numberOfSeats'), flex: 1 }
    ];

    return (
        <>
            <DataGrid
                autoPageSize
                columns={cols}
                rows={attractions.map((attraction) => ({id: attraction.uuid, ...attraction}))}
                className={classes.root}
                localeText={language === 'PL' ? plPLGrid : undefined}
            />
        </>
    )
}