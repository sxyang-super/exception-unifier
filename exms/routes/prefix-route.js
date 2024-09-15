const express = require('express');
const { jsonSource: source } = require("../source");

const router = express.Router();

const parameterNameModuleId = 'moduleId'
const bodyExceptionCodePrefixPropertyName = 'exceptionCodePrefix'

router.post('/', (req, res) => {
  const exceptionCodePrefix = (req.body)[bodyExceptionCodePrefixPropertyName];
  const moduleId = (req.body)[parameterNameModuleId];

  if (!moduleId || !exceptionCodePrefix) {
    res.status(400)
      .send(`${parameterNameModuleId} and ${bodyExceptionCodePrefixPropertyName} are both mandatory`)
    return
  }

  source.add(moduleId, exceptionCodePrefix)

  res.status(204)
    .send()
})

router.delete('/', (req, res) => {
  const moduleId = req.query[parameterNameModuleId];

  if (!moduleId) {
    res.status(400)
      .send(`${parameterNameModuleId} is required`)
    return
  }

  source.delete(moduleId)
  res.send('OK')
})

router.put('/', (req, res) => {
  const exceptionCodePrefix = (req.body)[bodyExceptionCodePrefixPropertyName];
  const moduleId = (req.body)[parameterNameModuleId];

  if (!moduleId || !exceptionCodePrefix) {
    res.status(400)
      .send(`${parameterNameModuleId} and ${bodyExceptionCodePrefixPropertyName} are both mandatory`)
    return
  }

  source.update(moduleId, exceptionCodePrefix)

  res.status(200)
    .send()
})

router.get('/:moduleId', (req, res) => {
  const moduleId = req.params[parameterNameModuleId]

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

router.get('/', (_, res) => {
  const exceptionCodePrefixes = source.list();

  res.json(exceptionCodePrefixes)
})


module.exports = router
