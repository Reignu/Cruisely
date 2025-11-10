import React from "react";
import MuiAutocomplete, { AutocompleteProps as MuiAutocompleteProps } from "@material-ui/lab/Autocomplete";
import {makeStyles} from "@material-ui/core/styles";
import {useSelector} from "react-redux";
import {selectDarkMode} from "../redux/slices/userSlice";

const useAutocompleteStyles = (darkMode: boolean) => makeStyles(theme => ({
    root: {
        '& .MuiFormLabel-root, & input': {
            color: `var(--${darkMode ? 'white' : 'dark'})`,
        },
        '& svg': {
            fill: `var(--${darkMode ? 'white' : 'dark'})`,
        },
        '& .MuiOutlinedInput-notchedOutline, & .MuiOutlinedInput-root:hover .MuiOutlinedInput-notchedOutline, & .MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline': {
            borderColor: `var(--${darkMode ? 'white' : 'dark'})`,
        },
        '': {

        }
    }
}))();

export interface AutoCompleteProps extends MuiAutocompleteProps<any, any, any, any> {
    colorIgnored?: boolean,
}

export default function Autocomplete(
    {
        colorIgnored = false,
        options,
        inputValue,
        style,
        noOptionsText,
        onChange,
        renderInput,
    }: AutoCompleteProps) {
    const darkMode = useSelector(selectDarkMode);
    const classes = useAutocompleteStyles(!colorIgnored && darkMode);

    return (
        <MuiAutocomplete
            className={classes.root}
            options={options}
            inputValue={inputValue}
            style={{width: 300, marginBottom: 16, ...style}}
            noOptionsText={noOptionsText}
            onChange={onChange}
            renderInput={renderInput}
        />
    )
}