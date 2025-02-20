const express = require('express');
const {jsonSource: source} = require("../source");

const router = express.Router();
const queryNamePushSecret = 'push_secret'

router.post('/bulk', (req, res) => {
    if (!req.body) {
        res.status(404)
            .send()
        return;
    }

    const secret = req.query[queryNamePushSecret]

    if (secret !== 'secret002') {
        res.status(403)
            .send(`unauthenticated`)
        return
    }

    console.log(req.body)
    res.status(201)
        .send()
})

module.exports = router
