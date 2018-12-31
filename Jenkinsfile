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
    stage('Build') { //Stage mit Namen Build
      agent { //In einem Dockercontainer mit Maven bauen
        docker { 
            image 'maven:3.5-jdk-8'
        }
      }
      steps {
        sh 'mvn clean package' //Bauen und Tests ausführen
        stash name: "warfile", includes: "petclinic/target/petclinic.war" //petclinic.war für spätere Aktivitäten stashen
      }
      post {
        always {
          junit '**/target/surefire-reports/**/*.xml' //Testergebnisse sichern
        }
      }
    }
    stage('BuildDocker') {
      steps {
        //petclinic.war wiederherstellen
        unstash name: "warfile"
        //Login bei Dockerhub
        sh 'docker login -u $DOCKERHUB_LOGIN -p $DOCKERHUB_PASSWORD'
        //Dockerimage bauen
        sh 'docker build -t $DOCKERHUB_LOGIN/petclinic:$BUILD_NUMBER .'
        //Dockerimage hochladen
        sh 'docker push $DOCKERHUB_LOGIN/petclinic:$BUILD_NUMBER'
      }
    }
    stage('AbnahmeTests') {
      parallel {
        stage('EndToEnd') {
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
          steps {
            script {
              docker.image("$DOCKERHUB_LOGIN/petclinic:$BUILD_NUMBER").withRun { 
                container ->
                  docker.image("maven:3.5-jdk-8").inside("--link=${container.id}:lasttest") {
                    sh 'mvn verify -Plast-tests'
                }
              }
            }
          }
        }
      }
    }
    stage('Deploy') {
      steps { 
        sshagent (credentials: ['petclinic-production']) {
          sh 'ssh pi@192.168.178.34 echo hello'
        }
      }
    }
  }
}  
