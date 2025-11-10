import useMediaQuery from "@material-ui/core/useMediaQuery";
import {useTheme} from "@material-ui/core/styles";


export default function useBreakpoints() {
    const theme = useTheme()

    const xl = useMediaQuery(theme.breakpoints.up('xl'));
    const lg = useMediaQuery(theme.breakpoints.up('lg'));
    const md = useMediaQuery(theme.breakpoints.up('md'));
    const sm = useMediaQuery(theme.breakpoints.up('sm'));
    // const xs = useMediaQuery(theme.breakpoints.up('xs'));

    return () => xl ? 'xl' : lg ? 'lg' : md ? 'md' : sm ? 'sm' : 'xs';
}