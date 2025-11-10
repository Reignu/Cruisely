import {Link, Route} from 'react-router-dom';
import Header from "../components/Header";
import Breadcrumb from "../components/Breadcrumb";
import {Color} from "../components/interfaces";
import styles from "../styles/moderatorPanel.module.css";
import Grid from "@material-ui/core/Grid";
import {useSelector} from "react-redux";
import {selectDarkMode} from "../redux/slices/userSlice";
import PanelMenu from "../components/PanelMenu";
import {List, ListItem, ListItemIcon, ListItemText} from "@material-ui/core";
import {OverridableComponent} from "@material-ui/core/OverridableComponent";

export interface PanelLayoutProps {
    color: {
        light: Color,
        dark: Color
    },
    menu: Array<{
            link: string,
            Icon: OverridableComponent<any>,
            text: string,
            Component: () => JSX.Element,
        }>,
    otherRoutes?: Array<{
        to: string,
        Component: () => JSX.Element
    }>
}

export default function PanelLayout({
        color: {
            dark, light
        },
        menu,
        otherRoutes
    }: PanelLayoutProps) {

    const darkMode = useSelector(selectDarkMode)


    return (
        <>
            <Header />
            <Breadcrumb />
            <Grid container className={styles.wrapper}>
                <Grid item  xs={2} md={3} xl={2}>
                    <PanelMenu color={!darkMode ? light : dark}>
                        <List className={styles.menu + ' ' + styles['menu-' + (!darkMode ? 'light' : 'dark')]} component="nav">
                            {
                                menu.map(({link, Icon, text}, index) => (
                                    <Link key={index} to={link}>
                                        <ListItem button>
                                            <ListItemIcon>
                                                <Icon style={{ fill: `var(--${!darkMode ? 'white' : 'dark'})` }} />
                                            </ListItemIcon>
                                            <ListItemText>{text}</ListItemText>
                                        </ListItem>
                                    </Link>
                                ))
                            }
                        </List>
                    </PanelMenu>
                </Grid>

                <Grid item className={styles.content + ' ' + styles[`content-${!darkMode ? 'light' : 'dark'}`]} xs={10} md={9} xl={10}>
                    {
                        menu.map(({link, text, Component}) => (
                            <Route exact path={link}>
                                <h3>{text}</h3>
                                <Component />
                            </Route>
                        ))
                    }

                    {
                        otherRoutes && otherRoutes.map(({to, Component}) => (
                            <Route key={to} exact path={to}>
                                <Component />
                            </Route>
                        ))
                    }
                </Grid>
            </Grid>
        </>
    )
}