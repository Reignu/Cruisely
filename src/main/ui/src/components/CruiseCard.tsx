import useMediaQuery from '@material-ui/core/useMediaQuery'

import StarIcon from '@material-ui/icons/StarRounded'
import EyeIcon from '@material-ui/icons/VisibilityRounded'

import styles from '../styles/CruiseCard.module.css'
import {useTranslation} from "react-i18next";
import ship3 from '../images/ship3.jpg'
import {useState} from "react";

export interface CruiseCardProps {
    price: number,
    title: string,
    img:string,
}

export default function CruiseCard(
    {
        price,
        title,
        img,

    }: CruiseCardProps) {

    let pictureUrl
    if(img == null) {
        pictureUrl = ship3
    } else {
        pictureUrl = img
    }
    const {t} = useTranslation()
    const xlMatches = useMediaQuery('(min-width: 1920px)')
    const lgMatches = useMediaQuery('(min-width: 1280px)')
    const mdMatches = useMediaQuery('(min-width: 960px)')
    const smMatches = useMediaQuery('(min-width: 600px)')

    return (
        <div className={styles.wrapper} style={{
            height: xlMatches ? 400 : lgMatches ? 300 : 200,
            backgroundImage:`url(${pictureUrl})`
        }}  >
            <div className={styles.cost}>{price} PLN</div>
            <div className={styles.description}>
                <span className={styles.title}>{title}</span>

            </div>
            <div className={styles.eye}>
                <div>
                    <EyeIcon style={{fill: '#FFFFFF', transform: 'scale(2)'}} />
                </div>
            </div>
        </div>
    )
}