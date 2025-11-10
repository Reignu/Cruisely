import Box from '@material-ui/core/Box'
import { Link } from 'react-router-dom';

export default function Brand() {
    return (
        <Box style={{
            fontFamily: '"Montserrat", sans-serif',
            fontSize: '2rem',
            color: 'var(--white)'
        }}>
            <Link to="/" style={{
                fontWeight: 600,
            }}>
                Cruise<span style={{color: 'var(--yellow)'}}>Hub</span>
            </Link>
        </Box>
    )
}