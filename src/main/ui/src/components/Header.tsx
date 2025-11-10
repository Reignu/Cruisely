import Box from '@material-ui/core/Box'

import Brand from './Brand'

import styles from '../styles/Header.module.css'
import RoundedButton from './RoundedButton'

import {useTranslation} from 'react-i18next'
import {useSelector} from "react-redux";
import {selectDarkMode} from "../redux/slices/userSlice";
import Authentication from "./Authentication";
import LanguageSetter from "./LanguageSetter";
import AppColorSetter from "./AppColorSetter";
import LineDivider from "./LineDivider";
import RefreshIcon from '@material-ui/icons/Refresh';
import {IconButton} from "@material-ui/core";

function Header({fixed = false}: {fixed?: boolean}) {
    const {t} = useTranslation()

    const darkMode = useSelector(selectDarkMode)

    return (
        <Box className={styles.wrapper + ' ' + styles[`wrapper-${!darkMode ? 'light' : 'dark'}`]}
             style={{
                 position: fixed ? 'fixed' : 'static'
             }}
        >
            <Brand/>
            <Box style={{
                display: 'flex',
                height: '100%',
                alignItems: 'center'
            }}>
                <Authentication />
                <LineDivider style={{margin: '0 24px'}} />
                <LanguageSetter />
                <AppColorSetter />
                <IconButton>
                    <RefreshIcon
                        fontSize="large"
                        style={{
                            fill: 'var(--white)',
                        }}
                        onClick={() => {window.location.reload()}}
                    />
                </IconButton>

            </Box>
        </Box>
    )
}

export default Header