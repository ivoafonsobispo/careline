import './Navbar.css'
import classNames from 'classnames';

import { HouseFill, HeartFill, FileBarGraphFill, MapFill, PersonFill, FileEarmarkMedicalFill } from 'react-bootstrap-icons'
import { NavLink } from 'react-router-dom';

import { useSelector } from 'react-redux';

export default function Navbar({ isActive }) {
    const user = useSelector((state) => state.auth.user);	

    return (
        <nav style={{ 'maxHeight': isActive ? '200px' : '0px' }} id='navbar' className={classNames(user.type === 'patient' ? 'nav-patient' : 'nav-professional')}>
            <ul className='navbar-logo-area'>
                <li className='navbar-logo-name'>CareLine</li>
                <hr></hr>
            </ul>

            <ul className='navbar-buttons-area'>
                <NavLink to='/' className={classNames(user.type === 'patient' ? "navlink-patient" : "navlink-professional")}>
                    <li className='navbar-item'>
                        <HouseFill size={25} className='navbar-svg' />
                        <span className='navbar-item-text'>Home</span>
                    </li>
                </NavLink>
                {user.type === 'patient' ?
                    <>
                        <NavLink to='/measures' className={classNames(user.type === 'patient' ? "navlink-patient" : "navlink-professional")}>
                            <li className='navbar-item'>
                                <HeartFill size={25} className='navbar-svg' />
                                <span className='navbar-item-text'>Measures</span>
                            </li>
                        </NavLink>
                        <NavLink to='/drones' className={classNames(user.type === 'patient' ? "navlink-patient" : "navlink-professional")}>
                            <li className='navbar-item'>
                                <MapFill size={25} className='navbar-svg' />
                                <span className='navbar-item-text'>Drones</span>
                            </li>
                        </NavLink>
                    </>
                    :
                    <>
                        <NavLink to='/patients' className={classNames(user.type === 'patient' ? "navlink-patient" : "navlink-professional")}>
                            <li className='navbar-item'>
                                <PersonFill size={25} className='navbar-svg' />
                                <span className='navbar-item-text'>Patients</span>
                            </li>
                        </NavLink>
                    </>
                }
                <NavLink to='/diagnoses' className={classNames(user.type === 'patient' ? "navlink-patient" : "navlink-professional")}>
                    <li className='navbar-item'>
                        <FileBarGraphFill size={25} className='navbar-svg' />
                        <span className='navbar-item-text'>Diagnoses</span>
                    </li>
                </NavLink>
                <NavLink to='/triage' className={classNames(user.type === 'patient' ? "navlink-patient" : "navlink-professional")}>
                    <li className='navbar-item'>
                        <FileEarmarkMedicalFill size={25} className='navbar-svg' />
                        <span className='navbar-item-text'>Triage</span>
                    </li>
                </NavLink>
                <NavLink to='/profile' className={classNames("navbar-last-item", user.type === 'patient' ? "navlink-patient" : "navlink-professional")} >
                    <li className='navbar-item'>
                        <PersonFill size={25} className='navbar-svg' />
                        <span className='navbar-item-text'>{user.name}</span>
                    </li>
                </NavLink>
            </ul>
        </nav>
    );
}
