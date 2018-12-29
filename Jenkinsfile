pipeline {
  agent none
  triggers {
    pollSCM('* * * * *')
  }
  stages {
    stage('Build') {
      agent {
        docker { 
            image 'maven:3.5-jdk-8'
        }
      }
      steps {
        sh "mvn -B clean package"
        stash name: "warfile", includes: "petclinic/target/petclinic.war"
      }
      post {
        always {
          junit '**/target/surefire-reports/**/*.xml'
        }
      }
    }
  }
}
