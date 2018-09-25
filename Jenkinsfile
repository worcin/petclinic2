pipeline {
  triggers {
    pollSCM('H/1 * * * *')
  }
  agent any
  stages {
    stage('Build') {
      steps {
        sh "mvn clean install"
	sh "docker build -t tomcat:petclinic ."
      }
      post {
        always {
          junit '**/target/surefire-reports/**/*.xml'
        }
      }
    }
    stage('IntegrationTesting') {
      parallel {
        stage('EndToEnd') {
          steps {
              sh "docker run -d --name dockerEnd2End -p 48080:8080 tomcat:petclinic"
              sh "mvn verify -Pselenium-tests -Dselenium.port=48080 -pl petclinic_it"
          }
          post {
            always {
              junit '**/target/surefire-reports/**/*.xml'
              sh "docker stop dockerEnd2End"
              sh "docker rm dockerEnd2End"
            }
          }
        }
        stage('LastTest') {
          steps {
              sh "docker run -d --name dockerLT -p 58080:8080 tomcat:petclinic"
              sh "mvn verify -Pjmeter-tests -pl petclinic_it"
          }
          post {
            always {
              junit '**/target/surefire-reports/**/*.xml'
              sh "docker stop dockerLT"
              sh "docker rm dockerLT"
            }
          }
        }
      }
    }
    stage('Deploy') {
      steps {
        sh "echo deploy"
      }
    }
  }
}
