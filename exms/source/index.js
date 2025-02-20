const fs = require('fs')
const path = require('path');
const { EXCEPTION_CODE_PREFIX_SEPARATOR } = require('../consts')

const sourceFilePath = path.join(__dirname, 'data', 'source.json');
const source = JSON.parse(fs.readFileSync(sourceFilePath).toString())

function syncJsonFile() {
  fs.writeFileSync(sourceFilePath, JSON.stringify(source, null, 2))
}

module.exports.jsonSource = {
  add(moduleId, exceptionCodePrefix) {
    if (source[moduleId]) {
      throw new Error(`exception code prefix for moduleId ${moduleId} already exists, please delete it first`)
    }

    const trimExceptionCodePrefix = exceptionCodePrefix?.trim();
    if (!trimExceptionCodePrefix) {
      throw new Error('exception code prefix can not be blank')
    }

    if (trimExceptionCodePrefix.includes(EXCEPTION_CODE_PREFIX_SEPARATOR)) {
      throw new Error(`exception code prefix can not contain reserved separator ${EXCEPTION_CODE_PREFIX_SEPARATOR}`)
    }

    source[moduleId] = trimExceptionCodePrefix

    syncJsonFile()
  },
  delete(moduleId) {
    if (!moduleId) {
      throw new Error('module id is mandatory')
    }

    if (!source[moduleId]) {
      return
    }

    delete source[moduleId]

    syncJsonFile()
  },
  update(moduleId, exceptionCodePrefix) {
    if (!moduleId) {
      throw new Error('module id is mandatory')
    }

    const trimExceptionCodePrefix = exceptionCodePrefix?.trim();
    if (!trimExceptionCodePrefix) {
      throw new Error('exception code prefix can not be blank')
    }

    if (trimExceptionCodePrefix.includes(EXCEPTION_CODE_PREFIX_SEPARATOR)) {
      throw new Error(`exception code prefix can not contain reserved separator ${EXCEPTION_CODE_PREFIX_SEPARATOR}`)
    }

    if (!source[moduleId]) {
      throw new Error(`exception code prefix is not configured for moduleId ${moduleId}`)
    }

    source[moduleId] = exceptionCodePrefix

    syncJsonFile()
  },
  get(moduleId) {
    return source[moduleId]
  },
  list() {
    return {
      ...source
    }
  }
}
