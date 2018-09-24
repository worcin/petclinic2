pipeline {
  agent any
  stages {
    stage('Build') {
      post {
        success {
          junit '**/target/surefire-reports/**/*.xml'

        }

      }
      steps {
        sh '''mvn clean install
docker build -t tomcat:petclinic .'''
      }
    }
    stage('IntegrationTesting') {
      parallel {
        stage('EndToEnd') {
          post {
            success {
              junit '**/target/surefire-reports/**/*.xml'

            }

            always {
              sh '''docker stop dockerEnd2End
			docker rm dockerEnd2End'''

            }

          }
          steps {
            sh '''docker run -d --name dockerEnd2End -p 58080:8080 tomcat:petclinic
mvn clean verify -Pselenium-tests -Dselenium.port=58080'''
          }
        }
        stage('LastTest') {
          post {
            success {
              junit '**/target/surefire-reports/**/*.xml'

            }

            always {
              sh '''docker stop dockerLT
			docker rm dockerLT'''

            }

          }
          steps {
            sh '''docker run -d --name dockerLT -p 58080:8080 tomcat:petclinic
mvn clean verify -Pjmeter-tests'''
          }
        }
      }
    }
    stage('Deploy') {
      steps {
        sh 'echo deploy'
      }
    }
  }
  triggers {
    pollSCM('H/1 * * * *')
  }
}