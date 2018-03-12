#!/usr/bin/groovy
package io.stakater


def shOutput(String command) {
    return sh(
        script: """
            ${command}
        """,
        returnStdout: true).toString().trim()
}

def getEnvValue(String key) {
    sh "echo \$${key} > ${key}"
    return readFile(key).trim()
}

return this
