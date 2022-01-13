node {
    def app

    nodejs('NodeJs-15.0.0') {
        stage("Build App") {

            buildApp()
        }
        stage('Test App') {
            try {
                sh "./jenkins/scripts/start.sh"
                sh "npm test"
            } catch (Exception e) {
                println e.getMessage()
                throw e
            } finally {
                sh "./jenkins/scripts/killapp.sh"
            }
        }
    }

    stage('Build image') {
        app = docker.build("19972909/node-js")
    }

    stage('Push image') {
        docker.withRegistry('https://registry.hub.docker.com', 'git') {
            app.push("${env.BUILD_NUMBER}")
            app.push("latest")
        }
    }
}

def buildApp(){
    sh 'npm install'
}

def startApp() {
    sh ' set -x'
    sh 'npm start & sleep 1'
    sh 'echo $! > .pidfile'
    sh ' set +x'
}

def testApp() {
    sh ' set - x'
    sh 'npm test'
    sh 'set + x'
}

