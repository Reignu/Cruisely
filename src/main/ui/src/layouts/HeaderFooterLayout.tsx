import Header from '../components/Header'
import Footer from '../components/Footer'
import LayoutProps from './LayoutProps'
import Box from "@material-ui/core/Box";
import {useSelector} from "react-redux";
import {selectDarkMode} from "../redux/slices/userSlice";

export default function HeaderFooterLayout({children}: LayoutProps) {

    const darkMode = useSelector(selectDarkMode)

    return (
        <Box style={{backgroundColor: `var(--${!darkMode ? 'white' : 'dark'}`}}>
            <Header fixed />
            {children}
            <Footer />
        </Box>
    )
}