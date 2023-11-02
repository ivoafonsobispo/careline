package main

import (
	"net/http"

	"github.com/julienschmidt/httprouter"
)

func (app *application) routes() http.Handler {
	router := httprouter.New()

	router.NotFound = http.HandlerFunc(app.notFoundResponse)
	router.MethodNotAllowed = http.HandlerFunc(app.methodNotAllowed)

	router.HandlerFunc(http.MethodGet, "/v1/healthcheck", app.healthcheckHandler)
	//router.HandlerFunc(http.MethodPost, "/v1/healthdata", app.createHealthdataHandler)
	//router.HandlerFunc(http.MethodGet, "/v1/healthdata/:id", app.showHealthdataHandler)

	return app.recoverPanic(router)
}
