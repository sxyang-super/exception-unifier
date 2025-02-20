const express = require('express');
const {jsonSource: source} = require("../source");

const router = express.Router();

const parameterNameModuleId = 'moduleId'
const queryNameSecret = "secret"

router.get('/:moduleId', (req, res) => {
    const moduleId = req.params[parameterNameModuleId]
    const secret = req.query[queryNameSecret]

    if (secret !== 'secret001') {
        res.status(403)
            .send(`unauthenticated`)
        return
    }

    if (!moduleId) {
        res.status(400)
            .send(`${parameterNameModuleId} is required`)
        return
    }

    const exceptionCodePrefix = source.get(moduleId);

    if (!exceptionCodePrefix) {
        res.status(404)
            .send(`exception source code for moduleId ${moduleId} is not configured`)
        return
    }

    res
        .send(exceptionCodePrefix)
})

module.exports = router
