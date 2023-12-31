package data

import (
	"database/sql"
	"errors"
)

var (
	ErrRecordNotFound = errors.New("record not found")
	ErrEditConflict   = errors.New("edit conflict")
)

type Models struct {
	Heartbeats       HeartbeatModel
	BodyTemperatures BodyTemperatureModel
	Patients         PatientModel
	Professionals    ProfessionalModel
}

func NewModels(db *sql.DB) Models {
	return Models{
		Heartbeats:       HeartbeatModel{DB: db},
		BodyTemperatures: BodyTemperatureModel{DB: db},
		Patients:         PatientModel{DB: db},
		Professionals:    ProfessionalModel{DB: db},
	}
}
