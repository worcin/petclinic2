pipeline {
  agent any //Standard-Agent
  environment {
    DOCKERHUB_LOGIN = credentials('jenkins-docker-login')
    DOCKERHUB_PASSWORD = credentials('jenkins-docker-password')
  }
  triggers {
    pollSCM('* * * * *') //Jede Minute auf Änderungen prüfen
  }
  stages { //Definition der Pipeline-Schritte

    stage('Deploy') {
      steps { 
        withCredentials(bindings: [sshUserPrivateKey (credentialsId: 'petclinic-production', keyFileVariable: 'SSH_KEY')]) {
          sh 'ssh -o StrictHostKeyChecking=no -i $SSH_KEY pi@192.168.178.34 echo hello'
        }
      }
    }
  }
}  
