import {Link, useHistory} from 'react-router-dom'

import Grid from '@material-ui/core/Grid'

import LayoutProps from './LayoutProps'
import styles from '../styles/auth.global.module.css'

import {useTranslation} from 'react-i18next'
import {useDispatch, useSelector} from "react-redux";
import {popHistory, selectHistory} from "../redux/slices/historySlice";

export default function AuthLayout({children}: LayoutProps) {

    const {t} = useTranslation()

    const history = useHistory()
    const h = useSelector(selectHistory)
    const dispatch = useDispatch()


    return (
            <Grid container className={styles.wrapper}>
                <Grid item className={styles.form} xs md={9} xl={6}>
                    {children}
                </Grid>
                <a
                    style={{
                        cursor: 'pointer',
                        position: 'absolute',
                        top: 20,
                        left: 20,
                        fontFamily: '"Montserrat", sans-serif',
                        fontSize: '1.2rem',
                    }}
                    onClick={() => {
                        history.push(h[h.length - 1])
                        dispatch(popHistory())
                    }}
                >
                    {t('go back')}
                </a>
            </Grid>
    )
}