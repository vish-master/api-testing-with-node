node {
    def app

    nodejs('NodeJs-15.0.0') {

        stage('Checkout scm') {
            echo "Checking out scm..."
            checkout(scm)
            sh "ls -l ${env.WORKSPACE}/jenkins/"
        }

        stage("Build App") {
            sh 'npm install'
        }
        stage('Test App') {
            try {
                sh 'set - x'
                sh 'kill $(cat.pidfile)'
                sh 'set + x'
                sh "npm test"
            } catch (Exception e) {
                println e.getMessage()
                throw e
            } finally {
                sh 'set - x'
                sh 'kill $(cat.pidfile)'
                sh 'set + x'
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


