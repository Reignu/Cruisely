import React, {useEffect, useState} from "react";
import {Attraction} from '../../../interfaces/Attraction';
import {deleteAttraction, getAttractionMetadata, getAttractionsByCruiseUUID} from "../../../Services/attractionService";
import {useSnackbarQueue} from "../../snackbar";
import {useTranslation} from "react-i18next";
import {useParams} from 'react-router-dom';
import {DataGrid, GridCellParams, GridColDef, GridRowSelectedParams, plPLGrid} from '@material-ui/data-grid';
import DeleteIcon from '@material-ui/icons/DeleteRounded';
import EditIcon from '@material-ui/icons/EditRounded';
import styles from '../../../styles/AttractionList.module.css';
import Dialog from '../../../components/Dialog';
import axios from "../../../Services/URL";
import DarkedTextField from "../../../components/DarkedTextField";
import {TextareaAutosize} from "@material-ui/core";
import RoundedButton from "../../../components/RoundedButton";
import store from "../../../redux/store";
import useHandleError from "../../../errorHandler";
import {refreshToken} from "../../../Services/userService";
import {useSelector} from "react-redux";
import {selectDarkMode, selectLanguage} from "../../../redux/slices/userSlice";
import {makeStyles} from "@material-ui/styles";
import PopupMetadata from "../../../PopupMetadata";
import PopupAcceptAction from "../../../PopupAcceptAction";


export default function AttractionList() {
    const darkMode = useSelector(selectDarkMode);
    const classes = makeStyles(theme => ({
        root: {
            color: `var(--${darkMode ? 'white' : 'dark'})`,
        }
    }))()
    interface MetadataAttraction {
        uuid: string,
    }
    const {uuid, published} = useParams<{ uuid: string, published: string }>();
    const {token} = store.getState()
    const {t} = useTranslation();
    const language = useSelector(selectLanguage)

    const showSuccess = useSnackbarQueue('success');
    const handleError = useHandleError();

    const [attractions, setAttractions] = useState<Attraction[]>([]);

    const getAttractions = () =>{
        getAttractionsByCruiseUUID(uuid).then(res => {
            setAttractions(res.data)
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
        })
    }

    useEffect(() => {
        getAttractions()
    }, [])

    const [metadataPopupAcceptAction, setMetadataPopupAcceptAction] = useState(false);
    const [alterType, setAlterType] = useState('')
    const [alteredBy, setAlteredBy] = useState('')
    const [createdBy, setCreatedBy] = useState('')
    const [creationDateTime, setCreationDateTime] = useState('')
    const [lastAlterDateTime, setLastAlterDateTime] = useState('')
    const [version, setVersion] = useState('')

    const handleMetadata = async ({uuid}: MetadataAttraction) =>{
        getAttractionMetadata(uuid).then(res => {
            setAlterType(res.data.alterType);
            setAlteredBy(res.data.alteredBy);
            setCreatedBy(res.data.createdBy);
            if(res.data.creationDateTime !=null)
                setCreationDateTime(res.data.creationDateTime.dayOfMonth +" "+ t(res.data.creationDateTime.month) +" "+ res.data.creationDateTime.year +" "+ res.data.creationDateTime.hour +":"+ res.data.creationDateTime.minute.toString().padStart(2, '0') )
            if(res.data.lastAlterDateTime !=null)
                setLastAlterDateTime(res.data.lastAlterDateTime.dayOfMonth +" "+ t(res.data.lastAlterDateTime.month) +" "+ res.data.lastAlterDateTime.year +" "+ res.data.lastAlterDateTime.hour +":"+ res.data.lastAlterDateTime.minute.toString().padStart(2, '0'));
            setVersion(res.data.version);
            refreshToken();
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        }).then(res => {
            setMetadataPopupAcceptAction(true);
        })
    }
    // Data grid events
    const [selectedRow, setSelectedRow] = useState('')

    const handleSelectedRow = (row: GridRowSelectedParams) => {
        setSelectedRow(row.data.id as string)
    }


    const [buttonPopupAccept, setButtonPopupAccept] = useState(false);
    const [id, setId] = useState("");

    const cols: GridColDef[] = [
        {field: 'name', headerName: t('attractionName'), flex: 1},
        {field: 'description', headerName: t('description'), flex: 1},
        {field: 'price', headerName: t('price'), flex: 1},
        {field: 'numberOfSeats', headerName: t('numberOfSeats'), flex: 1},
        {
            field: 'metadata',
            headerName: t('metadata'),
            sortable: false,
            disableColumnMenu: true,
            flex: 0.5,
            renderCell: (params: GridCellParams) =>
                (
                    <div>
                        {selectedRow === params.row.id && (
                            <>
                                <RoundedButton
                                    color="green"
                                   onClick={() => {
                                       const attraction = attractions.find(attraction => attraction.uuid === params.row.id)
                                       if(attraction){
                                           handleMetadata({
                                           uuid: attraction.uuid
                                       })
                                       setMetadataPopupAcceptAction(true)
                                   }}
                                   }>{t("metadata")}</RoundedButton>
                                <PopupMetadata
                                    open={metadataPopupAcceptAction}
                                    onCancel={() => {setMetadataPopupAcceptAction(false)}}
                                    alterType={alterType}
                                    alteredBy={alteredBy}
                                    createdBy={createdBy}
                                    creationDateTime={creationDateTime}
                                    lastAlterDateTime={lastAlterDateTime}
                                    version={version}
                                />
                            </>
                            )}
                    </div>
            )
        },
        {
            field: '',
            renderHeader: params => <RoundedButton color="blue" onClick={() => {
                setCreateAttractionDialogOpen(true)
            }}>{t('create')}</RoundedButton>,
            headerClassName: styles['create-wrap'],
            sortable: false,
            disableColumnMenu: true,
            flex: 0.5,
            renderCell: (params: GridCellParams) =>
                (
                    <>
                        {selectedRow === params.row.id && (
                            <div className={styles['icon-wrap']}>
                                <EditIcon
                                    className={styles.edit}
                                    onClick={() => {
                                        const attraction = attractions.find(attraction => attraction.uuid === params.row.id)

                                        if (attraction) {
                                            setAttractionUUID(attraction.uuid)
                                            setAttractionVersion(attraction.version)
                                            setAttractionEtag(attraction.etag)
                                            setNameEdit(attraction.name)
                                            setDescriptionEdit(attraction.description)
                                            setPriceEdit(attraction.price)
                                            setNumberOfSeatsEdit(attraction.numberOfSeats)
                                            setEditAttractionDialogOpen(true)
                                        }
                                    }}
                                />
                                <DeleteIcon
                                    className={styles.delete}
                                    onClick={() => {
                                        setId(params.row.id)
                                        setButtonPopupAccept(true)

                                    }}
                                />
                            </div>
                        )}
                    </>
                )
        }
    ];

    // Requests handler
    const handleDeleteAttraction = (uuid: string) => ()  =>{
        deleteAttraction(uuid).then(res => {
            setButtonPopupAccept(false)
            showSuccess(t('successful action'))
            getAttractions()
            }).catch(error => {
            setButtonPopupAccept(false)
            const message = error.response.data
            handleError(t(message),error.response.data)
        })
    }
    const handleMetadataAttraction = (uuid: string) => ()  =>{
        deleteAttraction(uuid).then(res => {
            showSuccess(t('successful action'))
            getAttractions()
        }).catch(error => {
            const message = error.response.data
            handleError(t(message),error.response.data)
        })
    }


    const handleEditAttraction = async () => {
        const json = {
            "uuid": attractionUUID,
            "newName": nameEdit,
            "newDescription": descriptionEdit,
            "newPrice": priceEdit,
            "newNumberOfSeats": numberOfSeatsEdit,
            "version": attractionVersion
        }

        console.log('attractionEdit:');
        console.log(json)
        await axios.put('attractions/edit-attraction', json, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'If-Match': attractionEtag
            }
        }).then(res => {
            showSuccess(t('attraction edited'))
            refreshToken()
            setEditAttractionDialogOpen(false)
            getAttractions()
        }).catch(err => {
            const message = err.response.data
            handleError(message, err.response.status)
        })
    }

    const handleCreateAttraction = async () => {
        const json = {
            "cruiseUUID": uuid,
            "name": nameCreate,
            "description": descriptionCreate,
            "price": priceCreate,
            "numberOfSeats": numberOfSeatsCreate
        }

        await axios.post('attractions/add-attraction', json, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        }).then(res => {
            showSuccess(t('attraction created'))
            refreshToken()
            setCreateAttractionDialogOpen(false)
            getAttractions()
        }).catch(err => {
            const message = err.response.data
            handleError(message, err.response.status)
        })
    }

// Dialogs data
    const [editAttractionDialogOpen, setEditAttractionDialogOpen] = useState(false);
    const [attractionUUID, setAttractionUUID] = useState('');
    const [attractionVersion, setAttractionVersion] = useState(0);
    const [attractionEtag, setAttractionEtag] = useState('')
    const [nameEdit, setNameEdit] = useState('');
    const [descriptionEdit, setDescriptionEdit] = useState('');
    const [priceEdit, setPriceEdit] = useState(0);
    const [numberOfSeatsEdit, setNumberOfSeatsEdit] = useState(0);

    const [createAttractionDialogOpen, setCreateAttractionDialogOpen] = useState(false);
    const [nameCreate, setNameCreate] = useState('');
    const [descriptionCreate, setDescriptionCreate] = useState('');
    const [priceCreate, setPriceCreate] = useState(0);
    const [numberOfSeatsCreate, setNumberOfSeatsCreate] = useState(0);

    return (
        <>
            {published === 'false' ? '' : <><h3>{t("attractions read only")}</h3><br/></>}
            <DataGrid
                autoPageSize
                columns={published === 'true' ? cols.slice(0, -1) : cols}
                rows={attractions.map((attraction) => ({id: attraction.uuid, ...attraction}))}
                onRowSelected={handleSelectedRow}
                className={classes.root}
                localeText={language === 'PL' ? plPLGrid : undefined}
            />
            <Dialog
                open={editAttractionDialogOpen}
                title={t('edit')}
                onConfirm={handleEditAttraction}
                onCancel={() => {
                    setEditAttractionDialogOpen(false)
                    setAttractionUUID('')
                    setAttractionVersion(0)
                    setNameEdit('')
                    setDescriptionEdit('')
                    setPriceEdit(0)
                    setNumberOfSeatsEdit(0)
                }}
            >
                <div style={{display: 'flex', flexDirection: 'column'}}>
                    <DarkedTextField
                        style={{marginBottom: 16}}
                        type="text"
                        label={t("attractionName")}
                        value={nameEdit}
                        onChange={event => {
                            setNameEdit(event.target.value)
                        }}
                    />

                    <TextareaAutosize
                        style={{
                            fontFamily: "'Montserrat', sans-serif",
                            fontSize: '1.2rem',
                            marginBottom: 16,
                            padding: '14px 18px',
                        }}
                        placeholder={t('description')}
                        value={descriptionEdit}
                        onChange={event => {
                            setDescriptionEdit(event.target.value)
                        }}
                        rowsMin={7}
                    />

                    <DarkedTextField
                        style={{marginBottom: 16}}
                        type="number"
                        label={t("price")}
                        value={priceEdit}
                        onChange={event => {
                            setPriceEdit(event.target.value as unknown as number)
                        }}
                    />

                    <DarkedTextField
                        style={{marginBottom: 16}}
                        type="number"
                        label={t("numberOfSeats")}
                        value={numberOfSeatsEdit}
                        onChange={event => {
                            setNumberOfSeatsEdit(event.target.value as unknown as number)
                        }}
                    />
                </div>
            </Dialog>
            <Dialog
                open={createAttractionDialogOpen}
                title={t('create')}
                onConfirm={handleCreateAttraction}
                onCancel={() => {
                    setCreateAttractionDialogOpen(false)
                    setNameCreate('')
                    setDescriptionCreate('')
                    setPriceCreate(0)
                    setNumberOfSeatsCreate(0)
                }}
            >
                <div style={{display: 'flex', flexDirection: 'column'}}>
                    <DarkedTextField
                        style={{marginBottom: 16}}
                        type="text"
                        label={t("attractionName")}
                        value={nameCreate}
                        onChange={event => {
                            setNameCreate(event.target.value)
                        }}
                    />

                    <TextareaAutosize
                        style={{
                            fontFamily: "'Montserrat', sans-serif",
                            fontSize: '1.2rem',
                            marginBottom: 16,
                            padding: '14px 18px',
                        }}
                        placeholder={t('description')}
                        value={descriptionCreate}
                        onChange={event => {
                            setDescriptionCreate(event.target.value)
                        }}
                        rowsMin={7}
                    />

                    <DarkedTextField
                        style={{marginBottom: 16}}
                        type="number"
                        label={t("price")}
                        value={priceCreate}
                        onChange={event => {
                            setPriceCreate(event.target.value as unknown as number)
                        }}
                    />

                    <DarkedTextField
                        style={{marginBottom: 16}}
                        type="number"
                        label={t("numberOfSeats")}
                        value={numberOfSeatsCreate}
                        onChange={event => {
                            setNumberOfSeatsCreate(event.target.value as unknown as number)
                        }}
                    />
                </div>
            </Dialog>

            <PopupAcceptAction
                open={buttonPopupAccept}
                onConfirm={handleDeleteAttraction(id)}
                onCancel={() => {
                    setButtonPopupAccept(false)
                }}/>
        </>
    )
}