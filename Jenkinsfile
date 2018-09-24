pipeline {
  agent any
      triggers {
        pollSCM('H/1 * * * *')
    }
  stages {
    stage('Build') {
      steps {
        sh '''mvn clean install
		docker build -t tomcat:petclinic .'''
      }
      post {
        success {
          junit '**/target/surefire-reports/**/*.xml'
        }
      }
    }
    stage('EndToEnd') {
      steps {
        sh '''docker run -d --name dockertest -p 58080:8080 tomcat:petclinic
		mvn verify -Pselenium-tests'''
      }
      post {
        success {
          junit '**/target/surefire-reports/**/*.xml'
        }
        always {
			sh '''docker stop dockertest
			docker rm dockertest'''
        }
      }
    }
    stage('LastTest') {
      steps {
        sh '''docker run -d --name dockertest -p 58080:8080 tomcat:petclinic
		mvn clean verify -Pjmeter-tests'''
      }
      post {
        success {
          junit '**/target/surefire-reports/**/*.xml'
        }
        always {
          sh '''docker stop dockertest
			docker rm dockertest'''
        }
      }
    }
    stage('deploy') {
      steps {
        script {
          try{
            sh '''docker stop prodclinic'''
            echo('Docker stopped')
            sh '''docker rm prodclinic'''
            echo('Docker removed')
          }catch(e) {}
          sh '''docker run -d --name prodclinic -p 48080:8080 tomcat:petclinic'''
        }
      }
    }
  }
}