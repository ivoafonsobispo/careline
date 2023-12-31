package main

import (
	"errors"
	"fmt"
	"net/http"

	"github.com/ivoafonsobispo/careline/backend/internal/data"
	"github.com/ivoafonsobispo/careline/backend/internal/validator"
)

func (app *application) createHeartbeatHandler(w http.ResponseWriter, r *http.Request) {
	var input struct {
		Heartbeat int32 `json:"heartbeat"`
	}

	err := app.readJSON(w, r, &input)
	if err != nil {
		app.badRequestResponse(w, r, err)
		return
	}

	h := &data.Heartbeat{
		Heartbeat: input.Heartbeat,
	}

	v := validator.New()

	if data.ValidateHeartbeat(v, h); !v.Valid() {
		app.failedValidationResponse(w, r, v.Errors)
		return
	}

	err = app.models.Heartbeats.Insert(h)
	if err != nil {
		app.serverErrorResponse(w, r, err)
		return
	}

	headers := make(http.Header)
	headers.Set("Location", fmt.Sprintf("/v1/heartbeat/%d", h.ID))

	err = app.writeJSON(w, http.StatusCreated, envelope{"heartbeat": h}, headers)
	if err != nil {
		app.serverErrorResponse(w, r, err)
	}
}

func (app *application) showHeartbeatHandler(w http.ResponseWriter, r *http.Request) {
	id, err := app.readIDParam(r)
	if err != nil {
		app.notFoundResponse(w, r)
		return
	}

	h, err := app.models.Heartbeats.Get(id)
	if err != nil {
		switch {
		case errors.Is(err, data.ErrRecordNotFound):
			app.notFoundResponse(w, r)
		default:
			app.serverErrorResponse(w, r, err)
		}
		return
	}

	err = app.writeJSON(w, http.StatusOK, envelope{"heartbeat": h}, nil)
	if err != nil {
		app.serverErrorResponse(w, r, err)
	}

}

func (app *application) updateHeartbeatHandler(w http.ResponseWriter, r *http.Request) {

}

func (app *application) deleteHeartbeatHandler(w http.ResponseWriter, r *http.Request) {

}

func (app *application) listHeartbeatsHandler(w http.ResponseWriter, r *http.Request) {
	var input struct {
		data.Filters
	}

	v := validator.New()

	qs := r.URL.Query()

	input.Filters.Page = app.readInt(qs, "page", 1, v)
	input.Filters.PageSize = app.readInt(qs, "page_size", 20, v)
	input.Filters.Sort = app.readString(qs, "sort", "id")

	input.Filters.SortSafeList = []string{"id", "date", "-id", "-date"}

	if data.ValidateFilters(v, input.Filters); !v.Valid() {
		app.failedValidationResponse(w, r, v.Errors)
		return
	}

	heartbeats, metadata, err := app.models.Heartbeats.GetAll(input.Filters)

	if err != nil {
		app.serverErrorResponse(w, r, err)
		return
	}

	err = app.writeJSON(w, http.StatusOK, envelope{"heartbeats": heartbeats, "metadata": metadata}, nil)
	if err != nil {
		app.serverErrorResponse(w, r, err)
	}
}
