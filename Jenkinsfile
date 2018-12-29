pipeline {
  agent none //Keinen Standard-Agent
  triggers {
    pollSCM('* * * * *') //Jede Minute auf Änderungen prüfen
  }
  stages { //Definition der Build-Schritte
    stage('Build') { //Stage mit namen Build
      agent { //In einem Dockercontainer mit Maven bauen
        docker { 
            image 'maven:3.5-jdk-8'
        }
      }
      steps {
        sh 'mvn -B clean package' //Bauen und Tests ausführen
        stash name: "warfile", includes: "petclinic/target/petclinic.war" //.war für spätere Aktivitäten stashen
      }
      post {
        always {
          junit '**/target/surefire-reports/**/*.xml' //Testergebnisse sichern
        }
      }
    }
  }
}
