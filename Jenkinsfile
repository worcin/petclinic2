pipeline {
  agent none #Keinen default Agent
  triggers {
    pollSCM('* * * * *') #Jede Minute nachfragen, ob es Änderungen gab
  }
  stages { #Definition der Stages
    stage('Build') { #Stage mit namen Build
      agent { #Wird in einem Dockercontainer mit Maven gebaut
        docker { 
            image 'maven:3.5-jdk-8'
        }
      }
      steps {
        sh "mvn -B clean package"  #Bauen und Tests ausführen
        stash name: "warfile", includes: "petclinic/target/petclinic.war" #War für spätere Aktivitäten stashen
      }
      post {
        always {
          junit '**/target/surefire-reports/**/*.xml'   #Testergebnisse sichern
        }
      }
    }
  }
}
