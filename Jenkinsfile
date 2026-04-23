pipeline {
  agent any

  environment {
    IMAGE = "13.201.141.194:5000/order-service"
    VERSION = "${BUILD_NUMBER}"
  }

  stages {
    stage('Checkout') { steps { checkout scm } }
    stage('Build') { steps { sh 'mvn clean package -DskipTests' } }
    stage('Push JAR') { steps { sh 'mvn deploy -DskipTests' } }
    stage('Docker Build') { steps { sh 'docker build -t $IMAGE:$VERSION .' } }
    stage('Docker Push') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'nexus-creds',
          usernameVariable: 'USER',
          passwordVariable: 'PASS'
        )]) {
          sh '''
          echo "$PASS" | docker login 13.201.141.194:5000 -u "$USER" --password-stdin
          docker push $IMAGE:$VERSION
          '''
        }
      }
    }
  }
}