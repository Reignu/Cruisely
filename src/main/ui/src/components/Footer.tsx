import Box from '@material-ui/core/Box'

import Brand from './Brand'

import styles from '../styles/Footer.module.css'
import {useSelector} from "react-redux";
import {selectDarkMode} from "../redux/slices/userSlice";

function Footer() {

    const darkMode = useSelector(selectDarkMode)

    return (
        <Box component="footer" className={styles.wrapper + ' ' + styles[`wrapper-${!darkMode ? 'light' : 'dark'}`]}>
            <Brand />
            <div style={{
                fontFamily: '"Montserrat", sans-serrif',
                fontSize: '1.5rem', 
                color: 'var(--white)'}}>© {new Date(Date.now()).getFullYear()}</div>
        </Box>
    )
}

export default Footer