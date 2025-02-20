const express = require('express')
const prefixRoute = require('./routes/prefix-route')
const exceptionEnumsRoute = require('./routes/exception-enum')

const port = process.env.PORT || 8080;

const app = express()

app.use(express.json())

app.use('/prefix', prefixRoute)
app.use('/exception-enum', exceptionEnumsRoute)

app.get('/healthcheck', (_, res) => {
    res.send('OK')
})

app.listen(port, () => {
    console.log(`server starts successfully on port ${port}`)
})
