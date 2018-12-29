pipeline {
  agent none
  triggers {
    pollSCM('H/1 * * * *')
  }
  stages {
    stage('Build') {
      agent {
        docker { 
            image 'maven:3.5-jdk-8' 
            args '-v /maven/.m2:/root/.m2' 
        }
      }
      steps {
        sh "mvn -B clean install"
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
