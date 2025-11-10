import React, {useState} from 'react';
import AddIcon from '@material-ui/icons/Add';
import RemoveIcon from '@material-ui/icons/Remove';
import StarIcon from '@material-ui/icons/StarRounded';
import StarHalfIcon from '@material-ui/icons/StarHalfRounded';
import EmptyStarIcon from '@material-ui/icons/StarOutlineRounded';
import styles from '../styles/StarSpinner.module.css';
import RoundedButton from "./RoundedButton";
import {useTranslation} from "react-i18next";

export interface StarSpinnerProps {
    onSubmit: (rating: number) => void,
}

export default function StarSpinner({onSubmit}: StarSpinnerProps) {

    const {t} = useTranslation();

    const [counter, setCounter] = useState<number>(1)

    const decrement = () => {
        if (counter !== 1) {
            setCounter(prev => prev - 0.5)
        }
    }

    const increment = () => {
        if (counter !== 5) {
            setCounter(prev => prev + 0.5)
        }
    }

    return (
        <div style={{
            padding: '16px',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'flex-end',

        }}>
            <div style={{
                width: '100%',
                display: 'flex',
                marginBottom: 12,
                justifyContent: 'center',
            }}>
                <RemoveIcon className={styles.minus} style={{ fontSize: 48 }} onClick={decrement} />
                <div style={{
                    margin: '0 24px',
                }}>
                    {
                        new Array(counter - counter % 1).fill(1).map((star, index) => (
                            <StarIcon key={`fullstar${index}`} style={{fontSize: 48, fill: 'var(--yellow-dark'}} />
                        ))
                    }
                    {
                        counter % 1 === 0.5 ? <StarHalfIcon style={{fontSize: 48, fill: 'var(--yellow-dark'}} /> : null
                    }
                    {
                        new Array(5 - Math.round(counter)).fill(1).map((star, index) => (
                            <EmptyStarIcon key={`emptystar${index}`} style={{fontSize: 48, fill: 'var(--yellow-dark'}} />
                        ))
                    }
                </div>
                <AddIcon className={styles.plus} style={{ fontSize: 48 }} onClick={increment} />
            </div>
            <RoundedButton color="blue" onClick={() => {onSubmit(counter)}}>{t('confirm')}</RoundedButton>
        </div>
    )
}