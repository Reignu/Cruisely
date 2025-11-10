import {useTranslation} from 'react-i18next'
import React, {useEffect, useState} from "react";
import ListClient from "./moderator/ListClient";

import SettingsIcon from '@material-ui/icons/SettingsRounded'
import AccountsListIcon from '@material-ui/icons/PeopleAltRounded'
import BusinessIcon from '@material-ui/icons/Business'

import {useSelector} from "react-redux";
import {selectDarkMode} from "../../redux/slices/userSlice";
import ChangeEmail from "../../components/changeData/ChangeEmail";
import ChangePassword from "../../components/changeData/ChangePassword";
import ChangeModeratorData from "../../components/changeData/ChangeModeratorData";
import ManageWorkers from "./moderator/ManageBusinessWorkers";
import PanelLayout from "../../layouts/PanelLayout";
import ListWorkersForCompany from "./moderator/ListWorkersForCompany"
import {getSelfMetadataDetails} from "../../Services/accountsService";
import {refreshToken} from "../../Services/userService";
import useHandleError from "../../errorHandler";
import ListCompany from "./moderator/ListCompany";
import ListCruiseGroup from "./moderator/ListCruiseGroups";
import AddCompany from "./moderator/AddCompany";
import ListClientRatings from "./moderator/ListClientRatings";
import AttractionList from "./admin/AttractionList";
import ModeratorHomePage from "../../components/ModeratorHomePage";
import CruiseIcon from "@material-ui/icons/CardTravelRounded";
import CreateIcon from "@material-ui/icons/Create";


export default function ModeratorPanel() {
    const {t} = useTranslation()
    const handleError = useHandleError()

    const [listClient, setListClient] = useState(true)
    const handleListClient = () => {
        setListClient(state => !state)
        setManage(true)
    }

    const darkModel = useSelector(selectDarkMode)

    const [manageAccount, setManage] = useState(true)
    const handleManageAccount = () => {
        setManage(state => !state)
        setListClient(true)
    }

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

    return (
        <PanelLayout
            color={{
                light: 'pink-dark',
                dark: 'white'
            }}
            menu={[
                {
                    link: '/accounts',
                    text: t('list accounts'),
                    Icon: AccountsListIcon,
                    Component: ListClient
                },
                {
                    link: '/listCruiseGroup',
                    Icon: CruiseIcon,
                    text: t('listCruiseGroup'),
                    Component: ListCruiseGroup
                },
                {
                    link: '/companies',
                    Icon: BusinessIcon,
                    text: t('list companies'),
                    Component: ListCompany
                },
                {
                    link: '/add-company',
                    Icon: CreateIcon,
                    text: t('add company'),
                    Component: AddCompany
                },
                {
                    link: '/unconfirmed-business-workers',
                    text: t('Manage business workers'),
                    Icon: AccountsListIcon,
                    Component: ManageWorkers
                },
                {
                    link: '/settings',
                    text: t('settings'),
                    Icon: SettingsIcon,
                    Component: () => (
                        <>
                            <ChangeModeratorData
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
            otherRoutes={
                [
                    {
                        to: '/company/business-workers',
                        Component: ListWorkersForCompany
                    },
                    {
                        to: '/accounts/ratings',
                        Component: ListClientRatings
                    },
                    {
                        to: '/attractions/:uuid',
                        Component: AttractionList
                    },
                    {
                        to: '/',
                        Component: ModeratorHomePage
                    }
                ]
            }
        />
    )
}