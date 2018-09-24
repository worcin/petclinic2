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
    stage('EndToEnd') {
      parallel {
        stage('EndToEnd') {
          post {
            success {
              junit '**/target/surefire-reports/**/*.xml'

            }

            always {
              sh '''docker stop dockertest
			docker rm dockertest'''

            }

          }
          steps {
            sh '''docker run -d --name dockertest -p 58080:8080 tomcat:petclinic
mvn clean verify -Pselenium-tests -Dselenium.port=58080'''
          }
        }
        stage('Lt') {
          steps {
            sh '''docker run -d --name dockertest -p 58080:8080 tomcat:petclinic
mvn clean verify -Pjmeter-tests'''
          }
        }
      }
    }
    stage('LastTest') {
      post {
        success {
          junit '**/target/surefire-reports/**/*.xml'

        }

        always {
          sh '''docker stop dockertest
			docker rm dockertest'''

        }

      }
      steps {
        sh '''docker run -d --name dockertest -p 58080:8080 tomcat:petclinic
		mvn clean verify -Pjmeter-tests'''
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