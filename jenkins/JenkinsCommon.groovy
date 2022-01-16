String repoName
String appVersion

def runPipelineSteps(){

    repoName = getRepoName()
    appVersion = getAppVersion()

    stage("Checkout SCM"){
        checkout(scm)
    }


    stage("build NodeJs"){
        buildNodesJsApp()
    }

    stage("unit testing"){
        try {
            sh "./jenkins/scripts/start.sh"
            sh "npm test"
        }catch(Exception e){
            throw e.getMessage()
        }finally{
            sh "./jenkins/scripts/kill.sh"
        }
    }

    stage("build Docker image"){
        String dockerImage = docker.build "${CI_DOCKER_REPO_URI}:${appVersion}"

        docker.withRegistry('', ${DOCKER_REGISTRY_CREDENTIAL}) {
           dockerImage.push()
        }
    }
}

String getAppVersion(){
    def packageJson = readJSON file: 'package.json'
    appVersion = "${packageJson.version}"

    return appVersion
}

String getRepoName(){
    return scm.getUserRemoteConfigs()[0].getUrl.tokenize('/').last().split("\\.")[0]
}


def buildNodesJsApp(){
    sh 'npm install'
    sh "chmod +x -R ${env.WORKSPACE}"
}

def cleanProjectWs(){
    cleanWs()
}

String successString(String message){
    return "\033[42m ${message} \033[0m"
}

String failureString(String message){
    return "\033[41m ${message} \033[0m"
}