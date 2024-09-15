require('dotenv').config();

const express = require('express')
const prefixRoute = require('./routes/prefix-route')

const port = process.env.PORT || 8080;

const app = express()

app.use(express.json())

app.use('/prefix', prefixRoute)

app.get('/healthcheck', (_, res) => {
  res.send('OK')
})

app.listen(port, () => {
  console.log(`server starts successfully on port ${port}`)
})
