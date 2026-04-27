pipeline {
  agent any

  environment {
    IMAGE = "13.201.141.194:5000/order-service"
    VERSION = "${BUILD_NUMBER}"
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build JAR') {
      steps { sh 'mvn clean package -DskipTests' }
    }

    stage('Push JAR to Nexus') {
      steps { sh 'mvn deploy -DskipTests' }
    }

    stage('Build Docker Image') {
      steps { sh 'docker build -t $IMAGE:$VERSION .' }
    }

    stage('Push Docker Image') {
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