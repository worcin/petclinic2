pipeline {
  agent none //Keinen Standard-Agent
  environment {
    DOCKERHUB_LOGIN = credentials('jenkins-docker-login')
    DOCKERHUB_PASSWORD = credentials('jenkins-docker-password')
  }
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
        stash name: "warfile", includes: "petclinic/target/petclinic.war" //petclinic.war für spätere Aktivitäten stashen
      }
      post {
        always {
          junit '**/target/surefire-reports/**/*.xml' //Testergebnisse sichern
        }
      }
    }
    stage('BuildDocker') {
      agent any
      steps {
        unstash name: "warfile"
        sh 'docker login -u $DOCKERHUB_LOGIN -p $DOCKERHUB_PASSWORD'
        sh 'docker build -t $DOCKERHUB_LOGIN/petclinic:$BUILD_NUMBER .'
        sh 'docker push $DOCKERHUB_LOGIN/petclinic:$BUILD_NUMBER'
      }
    }
    stage('EndToEnd') {
      agent any
      steps {
        script {
          docker.image("$DOCKERHUB_LOGIN/petclinic:$BUILD_NUMBER").withRun { 
            container ->
              docker.image("maven:3.5-jdk-8").inside("--link=${container.id}:selenium") {
                sh 'mvn verify -Pselenium-tests -Dselenium.host=selenium'
            }
          }
        }
      }
      post {
        always {
          junit '**/target/surefire-reports/**/*.xml'
        }
      }
    }
    stage('LastTest') {
      agent any
      steps {
        script {
          docker.image("$DOCKERHUB_LOGIN/petclinic:$BUILD_NUMBER").withRun { 
            container ->
              docker.image("maven:3.5-jdk-8").inside("--link=${container.id}:selenium") {
                sh 'mvn verify -Plast-tests -Dselenium.host=selenium'
            }
          }
        }
      }
    }
  }
}
