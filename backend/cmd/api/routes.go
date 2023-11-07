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

	router.HandlerFunc(http.MethodPost, "/v1/heartbeat", app.createHeartbeatHandler)
	router.HandlerFunc(http.MethodGet, "/v1/heartbeat/:id", app.showHeartbeatHandler)
	router.HandlerFunc(http.MethodPut, "/v1/heartbeat/:id", app.updateHeartbeatHandler)
	router.HandlerFunc(http.MethodDelete, "/v1/heartbeat/:id", app.deleteHeartbeatHandler)

	router.HandlerFunc(http.MethodPost, "/v1/bodytemperature", app.createBodytemperatureHandler)
	router.HandlerFunc(http.MethodGet, "/v1/bodytemperature/:id", app.showBodytemperatureHandler)
	router.HandlerFunc(http.MethodPut, "/v1/bodytemperature/:id", app.updateBodytemperatureHandler)
	router.HandlerFunc(http.MethodDelete, "/v1/bodytemperature/:id", app.deleteBodytemperatureHandler)

	router.HandlerFunc(http.MethodPost, "/v1/user", app.createUserHandler)
	router.HandlerFunc(http.MethodGet, "/v1/user/:id", app.showUserHandler)

	return app.recoverPanic(router)
}