import './MeasureList.css'
import './ClientBase.css'

import { NavLink } from 'react-router-dom';

import { Heart, ThermometerHalf } from 'react-bootstrap-icons';

export default function MeasureList({ title, dataArray }) {
    return (
        <div className="client-measure-list-box">
            <span className='list-title align-line-row'>
                {title === "Heartbeat" ? (
                    <>
                        <Heart size={20} color="black" /> &nbsp;
                    </>
                ) : title === "Temperature" ? (
                    <>
                        <ThermometerHalf size={20} color="black" /> &nbsp;
                    </>
                ) : (<></>)}
                {title}:
            </span>
            {title === "Measures" ? (
                <>
                    <div className="client-measure-list">
                        {!dataArray || dataArray.length === 0 ? (
                            <div className='no-records'>No records yet.</div>
                        ) : (
                            <>
                                {dataArray.map((measure, index) => {
                                    return (
                                        <div key={index} className="App-client-measure-list-item" style={{ display: "flex", flexDirection: "column" }}>
                                            <span>Heartbeat: {measure.heartbeat} BPM </span>
                                            <span className='list-item-date'>{measure.created_at} </span>
                                        </div>
                                    )
                                })}
                                <div className="App-client-measure-list-item" style={{ display: "flex", flexDirection: "column" }}>
                                    <NavLink to='/measures' style={{ backgroundColor: "white" }}>
                                        <span className='measure-list-navlink'>Show More</span>
                                    </NavLink>
                                </div>
                            </>
                        )}

                    </div>
                </>
            ) : title === "Diagnoses" ? (
                <>
                    <div className="client-measure-list">
                        {!dataArray || dataArray.length === 0 ? (
                            <div className='no-records'>No records yet.</div>
                        ) : (
                            <>
                                {dataArray.map((diagnosis, index) => {
                                    return (
                                        <div key={index} className="App-client-measure-list-item" style={{ display: "flex", flexDirection: "column" }}>
                                            <span>Diagnosis: {diagnosis.id} - {diagnosis.diagnosis}</span>
                                            <span className='list-item-date'>{diagnosis.created_at} </span>
                                        </div>
                                    )
                                })}
                                <div className="App-client-measure-list-item" style={{ display: "flex", flexDirection: "column" }}>
                                    <NavLink to='/diagnoses' style={{ backgroundColor: "white" }}>
                                        <span className='measure-list-navlink'>Show More</span>
                                    </NavLink>
                                </div>
                            </>
                        )}
                    </div>
                </>
            ) : title === "Temperature" ? (
                <>
                    <div className="client-measure-list">
                        {!dataArray || dataArray.length === 0 ? (
                            <div className='no-records'>No records yet.</div>
                        ) : (
                            <>
                                {dataArray.map((temperature, index) => {
                                    return (
                                        <div key={index} className="App-client-measure-list-item" style={{ display: "flex", flexDirection: "column" }}>
                                            <span>Temperature: {temperature.temperature} °C</span>
                                            <span className='list-item-date'>{temperature.created_at} </span>
                                        </div>
                                    )
                                })}
                            </>
                        )}
                    </div>
                </>
            ) : ( // Heartbeat
                <>
                    <div className="client-measure-list">
                        {!dataArray || dataArray.length === 0 ? (
                            <div className='no-records'>No records yet.</div>
                        ) : (
                            <>
                                {dataArray.map((heartbeat, index) => {
                                    return (
                                        <div key={index} className="App-client-measure-list-item" style={{ display: "flex", flexDirection: "column" }}>
                                            <span>Heartbeat: {heartbeat.heartbeat} BPM</span>
                                            <span className='list-item-date'>{heartbeat.created_at} </span>
                                        </div>
                                    )
                                })}
                            </>
                        )}
                    </div>
                </>
            )}
        </div>
    );
}