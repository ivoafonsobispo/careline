import PageTitle from "../../Components/PageTitle/PageTitle";
import "../../Components/ClientComponents/ClientBase.css";
import ProfessionalTriageComponent from "../../Components/ProfessionalComponents/ProfessionalTriageComponent";
import "./ProfessionalTriage.css"

import { useState } from 'react';
import { ToastContainer } from "react-toastify";

//Day Picker
import { format } from 'date-fns';
import { DayPicker } from 'react-day-picker';
import 'react-day-picker/dist/style.css';
import '../../DayPicker.css';
import classNames from "classnames";

import axios from "axios";
import { useEffect } from "react";

import Stomp from 'stompjs';
import SockJS from 'sockjs-client';

import { useSelector } from "react-redux";

export default function ProfessionalTriage() {
    const token = useSelector((state) => state.auth.token);
    const [selected, setSelected] = useState(new Date());
    const [date, setDate] = useState("2023-12-25");

    let footer = <p>Please pick a day.</p>;
    if (selected) {
        footer = <p>You picked {format(selected, 'PP')}.</p>;
    }

    const [selectedButton, setButton] = useState("all"); // all; urtriage; rtriage

    useEffect(() => {
        if (selected) {
            const year = selected.getFullYear();
            const month = String(selected.getMonth() + 1).padStart(2, '0');
            const day = String(selected.getDate()).padStart(2, '0');
            const formattedDate = `${year}-${month}-${day}`;
            setDate(formattedDate);
            console.log(formattedDate);
        }
    }, [selected]);


    const urlTriage = `/triages/date/${date}`;
    const [triages, setTriages] = useState([]);
    useEffect(() => {
        axios.get(urlTriage, {
            headers: {
                'Access-Control-Allow-Origin': '*',
                Authorization: `Bearer ${token}`,
            },
            proxy: {
                port: 8080
            }
        })
            .then(response => {
                setTriages(response.data.content);
                // console.log(response.data.content);
            })
            .catch(error => {
                console.log(error);
            });
    }, [urlTriage, token]);

    let stompClient;

    useEffect(() => {
        const socket = new SockJS('http://10.20.229.55/websocket-endpoint');
        stompClient = Stomp.over(socket);

        try {
            stompClient.connect({}, () => {
                stompClient.subscribe('/topic/triages', (message) => {
                    let newTriage = JSON.parse(message.body);
                    if (newTriage.status !== 'REVIEWED') {
                        setTriages((prevTriages) => [newTriage, ...prevTriages]);
                    } else {
                        const index = triages.findIndex(triage => triage.id === newTriage.id);

                        setTriages((prevTriages) => {
                            const updatedTriages = [...prevTriages];
                            updatedTriages[index] = newTriage;
                            return updatedTriages;
                        });
                    }
                });
            });


        } catch (error) {
            console.error('WebSocket connection error:', error);
            // Handle the error here, e.g., show a user-friendly message or retry the connection
        }

        return () => {
            if (stompClient && stompClient.connected) {
                stompClient.disconnect();
            }
        };
    }, []);

    return (
        <div className="horizontal-container">
            <div className="vertical-container">
                <PageTitle title="Triage" />
                <div className='App-content'>
                    <div className="vertical-container">
                        <div className="align-line-row" style={{ marginBottom: "2%", gap: "2%" }}>
                            <button className={classNames("professional-triage-button", selectedButton !== 'all' ? "professional-triage-button-inactive" : "")} onClick={() => setButton("all")}>All Triage</button>
                            <button className={classNames("professional-triage-button", selectedButton !== 'urtriage' ? "professional-triage-button-inactive" : "")} onClick={() => setButton("urtriage")}>Unreviewed Triage</button>
                            <button className={classNames("professional-triage-button", selectedButton !== 'rtriage' ? "professional-triage-button-inactive" : "")} onClick={() => setButton("rtriage")}>Reviewed Triage</button>
                        </div>
                        <div className="vertical-container diagnoses-list" style={{ maxHeight: "470px" }}>
                            {selectedButton === 'all' ? (
                                <>
                                    {!triages || triages === null || triages.length === 0 ? (
                                        <div className='no-records'>No triage yet.</div>
                                    ) : (
                                        <>
                                            {triages.map((triage, index) => {
                                                return (
                                                    <ProfessionalTriageComponent key={index} triage={triage} />
                                                )
                                            })}
                                        </>
                                    )}
                                </>
                            ) : selectedButton === 'urtriage' ? (
                                <>
                                    {!triages || triages.filter(triage => triage.status === 'UNREVIEWED').length === 0 ? (
                                        <div className='no-records'>No unreviewed triage.</div>
                                    ) : (
                                        <>
                                            {triages.filter(triage => triage.status === 'UNREVIEWED').map((triage, index) => {
                                                return (
                                                    <ProfessionalTriageComponent key={index} triage={triage} />
                                                )
                                            })}
                                        </>
                                    )}
                                </>
                            ) : (
                                <>
                                    {!triages || triages.filter(triage => triage.status === 'REVIEWED').length === 0 ? (
                                        <div className='no-records'>No reviewed triage.</div>
                                    ) : (
                                        <>
                                            {triages.filter(triage => triage.status === 'REVIEWED').map((triage, index) => {
                                                return (
                                                    <ProfessionalTriageComponent key={index} triage={triage} />
                                                )
                                            })}
                                        </>
                                    )}
                                </>
                            )}
                        </div>
                    </div>
                </div>
            </div>
            <div className='day-picker' style={{ background: "var(--professionalLightColor)" }}>
                <DayPicker
                    mode="single"
                    selected={selected}
                    onSelect={setSelected}
                    footer={footer}
                    className='day-picker-style-professional'
                />
            </div>
            <ToastContainer />
        </div>
    );
}