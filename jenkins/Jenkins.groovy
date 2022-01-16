def commonPipeline

pipeline {
    agent any

    tools { nodejs "node" }

    stages {
        stage("load pipeline properties") {
            steps {
                script {
                    load "jenkins/JenkinsConstants.groovy"
                    commonPipeline = load "jenkins/JenkinsCommon.groovy"
                }
            }
        }

        stage("run pipeline steps") {
            steps {
                script {
                    commonPipeline.runPipelineSteps()
                }
            }

        }
    }

    post {
        always {
            echo commonPipeline.infoString("Done")
            script {
                commonPipeline.cleanProjectWs()
            }
        }

        success {
            echo commonPipeline.successString("Success")
        }

        failure {
            echo commonPipeline.failureString("Failure")
        }
    }
}

