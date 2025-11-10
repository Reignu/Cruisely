import React from 'react';
import {useSelector} from "react-redux";
import {selectDarkMode} from "../redux/slices/userSlice";
import {
    Box,
    Chip
} from '@material-ui/core'
import ArrowIcon from '@material-ui/icons/ArrowForwardIos';

import {Link} from 'react-router-dom';

import {useHistory, useLocation} from "react-router-dom";
import {useEffect} from "react";
import {useTranslation} from "react-i18next";
import i18n from '../i18n';


export default function Breadcrumb() {
    const darkMode = useSelector(selectDarkMode)

    const location = useLocation()

    const {t} = useTranslation()

    return (
        <Box style={{
            display: 'flex',
            alignItems: 'center',
            height: 'var(--header-breadcrumb)',
            backgroundColor: `var(--${darkMode ? 'dark-light' : 'yellow-dark'})`
        }}>
            {
                location.pathname
                    .split('/')
                    .map(item => '/' + item)
                    .map((item, index, self) => self.slice(0, index + 1).reduce((acc, curr) => acc + curr))
                    .map(item => item.replace('//', '/'))
                    .filter((item, index, self) => self.indexOf(item) === index && i18n.exists(item))
                    .map((item, index, self) =>
                        <React.Fragment key={index + item}>
                            {
                                index !== self.length - 1 ? (
                                    <Link style={{display: 'block', cursor: 'pointer'}} to={item}>
                                        <Chip clickable
                                              label={t(item)}
                                              style={{
                                                  fontFamily: "'Montserrat', sans-serif",
                                                  fontSize: '12px',
                                                  margin: '0 10px',
                                                  backgroundColor: `var(--white)`,
                                                  color: `var(--dark'})`
                                              }}
                                        />
                                    </Link>
                                ) : (
                                    <Chip
                                          label={t(item)}
                                          style={{
                                              fontFamily: "'Montserrat', sans-serif",
                                              fontSize: '12px',
                                              margin: '0 10px',
                                              backgroundColor: `var(--white)`,
                                              color: `var(--dark'})`
                                          }}
                                    />
                                )
                            }

                            {index !== self.length - 1 && <ArrowIcon fontSize="small" style={{fill: `var(--white)`}} />}
                        </React.Fragment>)
            }
        </Box>
    )
}