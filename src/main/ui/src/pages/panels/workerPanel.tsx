import {useTranslation} from 'react-i18next'
import SettingsIcon from '@material-ui/icons/SettingsRounded'
import CruiseIcon from '@material-ui/icons/CardTravelRounded'
import CreateIcon from '@material-ui/icons/Create';
import {useSelector} from "react-redux";
import {selectDarkMode} from "../../redux/slices/userSlice";
import ChangeBusinessWorkerData from "../../components/changeData/ChangeBusinessWorkerData";
import React, {useEffect, useState} from "react";
import ChangeEmail from "../../components/changeData/ChangeEmail";
import ChangePassword from "../../components/changeData/ChangePassword";
import PanelLayout from "../../layouts/PanelLayout";
import {getSelfAddressMetadataDetails, getSelfMetadataDetails} from "../../Services/accountsService";
import {refreshToken} from "../../Services/userService";
import useHandleError from "../../errorHandler";
import AccountsListIcon from "@material-ui/icons/PeopleAltRounded";
import ListCompany from "./moderator/ListCompany";
import AddCruiseGroup from "./worker/AddCruiseGroup";
import ListCruiseGroup from "./worker/ListCruiseGroupForWorker"
import AddCruise from "./worker/AddCruise"
import ListReservationsForWorker from "./worker/ListReservationsForWokrersCruise";
import ChangeCruiseGroup from "./worker/ChangeCruiseGroup"
import AttractionList from "./worker/AttractionList";
import WorkerHomePage from "../../components/WorkerHomePage";

export default function WorkerPanel() {
    const {t} = useTranslation()
    const handleError = useHandleError()

    const darkMode = useSelector(selectDarkMode)

    const [isEmailEdit, setIsEmailEdit] = useState(false)
    const [isDataEdit, setIsDataEdit] = useState(false)
    const [isPasswordEdit, setIsPasswordEdit] = useState(false)

    const handleIsEmailEdit = () => {
        getSelfMetadataDetails().then(res => {
            sessionStorage.setItem("changeSelfAccountDataMta", JSON.stringify(res.data));
            refreshToken();
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });
        setIsEmailEdit(true)
        setIsDataEdit(false)
        setIsPasswordEdit(false)
    }

    const handleIsDataEdit = () => {
        getSelfMetadataDetails().then(res => {
            sessionStorage.setItem("changeSelfAccountDataMta", JSON.stringify(res.data));
            refreshToken();
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });
        setIsDataEdit(true)
        setIsEmailEdit(false)
        setIsPasswordEdit(false)
    }

    const handleIsPasswordEdit = () => {
        getSelfMetadataDetails().then(res => {
            sessionStorage.setItem("changeSelfAccountDataMta", JSON.stringify(res.data));
            refreshToken();
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });
        setIsPasswordEdit(true)
        setIsDataEdit(false)
        setIsEmailEdit(false)
    }
    useEffect(() => {
        getSelfMetadataDetails().then(res => {
            sessionStorage.setItem("changeSelfAccountDataMta", JSON.stringify(res.data));
            sessionStorage.setItem("changeSelfAddressDataMta", JSON.stringify(res.data));
            refreshToken();
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });

    }, [])

    return (
        <PanelLayout
            color={{
                light: 'green-dark',
                dark: 'white'
            }}
            menu={[
                {
                    link: '/listCruiseGroup',
                    Icon: CruiseIcon,
                    text: t('listCruiseGroup'),
                    Component: ListCruiseGroup
                }, {
                    link: '/addCruiseGroup',
                    text: t('createCruiseGroup'),
                    Icon: CreateIcon,
                    Component: AddCruiseGroup

                },
                {
                    link: '/addCruise',
                    text: t('createCruise'),
                    Icon: CreateIcon,
                    Component: AddCruise
                },
                {
                    link: '/settings',
                    text: t('settings'),
                    Icon: SettingsIcon,
                    Component: () => (
                        <>
                            <ChangeBusinessWorkerData
                                open={isDataEdit}
                                onOpen={handleIsDataEdit}
                                onConfirm={() => {
                                    setIsDataEdit(false)
                                }}
                                onCancel={() => {
                                    setIsDataEdit(false)
                                }}
                            />
                            <ChangeEmail
                                open={isEmailEdit}
                                onOpen={handleIsEmailEdit}
                                onConfirm={() => {
                                    setIsEmailEdit(false)
                                }}
                                onCancel={() => {
                                    setIsEmailEdit(false)
                                }}
                            />
                            <ChangePassword
                                open={isPasswordEdit}
                                onOpen={handleIsPasswordEdit}
                                onConfirm={() => {
                                    setIsPasswordEdit(false)
                                }}
                                onCancel={() => {
                                    setIsPasswordEdit(false)
                                }}
                            />
                        </>
                    )
                }
            ]}
            otherRoutes={[
                {
                    to: '/reservations',
                    Component: ListReservationsForWorker
                },
                {
                    to: '/changeCruiseGroupData',
                    Component: ChangeCruiseGroup
                },
                {
                    to: '/attractions/:uuid/:published',
                    Component: AttractionList
                },
                {
                    to: '/',
                    Component: WorkerHomePage
                }
            ]}

        />
    )
}
