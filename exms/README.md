# exception manage server
This is a simple standalone server that serves basic requirements from exception unifier in remote mode.

Actually you can implement your own server with your own familiar tech stack refer to this project.

What you need to do is to implement all following required endpoints.

## how to start

### for development environment

`npm run dev`

### for production environment

`npm run start`

## endpoint list
* (OPTIONAL) return server health status

````http request
GET http://localhost:8080/healthcheck
````

This endpoint should always return "OK" with response status code 200 to tell the health of this server.
 
* (OPTIONAL) configure exception code prefix for given module id

````http request
POST http://localhost:8080/prefix
Content-Type: application/json

{
  "moduleId": "com.sample.module-id",
  "exceptionCodePrefix": "SAMPLE-PREFIX"
}
````

* (OPTIONAL) delete configuration for given module id

````http request
DELETE http://localhost:8080/prefix?moduleId=com.sample.module-id
````

* (OPTIONAL) update configuration for given module id

````http request
PUT http://localhost:8080/prefix
Content-Type: application/json

{
  "moduleId": "com.sample.module-id",
  "exceptionCodePrefix": "ANOTHER-SAMPLE-PREFIX"
}
````

* (REQUIRED) get exception code prefix for given module id

````http request
GET http://localhost:8080/prefix/com.sample.module-id
````

Used for getting configured exception code prefix for given module id,

If it's an unknown moduleId, 404 response status code is supposed to be returned.

* (OPTIONAL) get all configured exception code prefixes

````http request
GET http://localhost:8080/prefix
````
