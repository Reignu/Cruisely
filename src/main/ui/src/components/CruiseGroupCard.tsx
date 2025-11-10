import useMediaQuery from '@material-ui/core/useMediaQuery'

import StarIcon from '@material-ui/icons/StarRounded'
import EyeIcon from '@material-ui/icons/VisibilityRounded'

import styles from '../styles/CruiseCard.module.css'
import {useTranslation} from "react-i18next";
import {createCruiseGroup, dCruiseGroup} from "./ListCruiseGroup";
import ship3 from "../images/ship3.jpg"
import {Button} from "@material-ui/core";
import {Link} from "react-router-dom";
import React from "react";
import {makeStyles} from "@material-ui/core/styles";
import axios from "../Services/URL";
import {useSelector} from "react-redux";
import {selectToken} from "../redux/slices/tokenSlice";
import {useSnackbarQueue} from "../pages/snackbar";
import useHandleError from "../errorHandler";

interface DeactivateCruiseData {
    uuid: string;
    etag: string;
    version: bigint,
    token: string
}
export interface  CruiseData{
   group: ReturnType<typeof createCruiseGroup>,
    onChange: () => Promise<any>,
}
export interface  CruiseDataD{
    group: ReturnType<typeof dCruiseGroup>
}
const deactivateCruiseGroup = ({uuid, etag, version, token}: DeactivateCruiseData) => {
    const json = JSON.stringify({
            uuid: uuid,
            version: version,
        }
    );
    return axios.put('cruiseGroup/deactivate-cruise-group', json, {
        headers: {
            'Content-Type': 'application/json',
            'If-Match': etag,
            'Authorization': `Bearer ${token}`
        }

    });
};

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

export default function CruiseGroupCard(props: CruiseData) {
    const {t} = useTranslation()
    const xlMatches = useMediaQuery('(min-width: 1920px)')
    const lgMatches = useMediaQuery('(min-width: 1280px)')
    const mdMatches = useMediaQuery('(min-width: 960px)')
    const smMatches = useMediaQuery('(min-width: 600px)')
    const buttonClass = useButtonStyles();
    const {onChange} = props;
    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')
    const token = useSelector(selectToken)
    const {group} = props
    let pictureUrl


    console.log(group.etag);


    if(group.cruisePictures.length>0){
        pictureUrl ='data:image/png;base64,'+ group.cruisePictures[0].dataURL
    }
    else {
        pictureUrl= ship3
    }

    return (
        <div className={styles.wrapper} style={{
            height: xlMatches ? 400 : lgMatches ? 300 : 200,
            backgroundImage:`url(${pictureUrl})`
        }}>
            <div className={styles.cost}>{group.price+ " pln"}</div>
            <div className={styles.data}>
                <div>{t("seats")+" "+group.numberOfSeats}</div>
                <div>{t("start_date")+" "+group.start_time}</div>
                <div>{t("end_date")+" "+group.end_time}</div>
            </div>
            <div className={styles.description}>
                <span className={styles.title}>{group.name}</span>
                <div>
                    {[0,1,2,3,4].map(id =>
                        <StarIcon key={id} style={{
                            fill: 'var(--yellow)'
                        }} />)}
                </div>
            </div>
            <div className={styles.eye}>
                <div>
                    <EyeIcon style={{fill: '#FFFFFF', transform: 'scale(2)'}} />
                </div>
            </div>
        </div>
    )
}