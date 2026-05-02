pipeline {
  agent any

  environment {
    IMAGE = "13.201.141.194:5000/order-service"
    VERSION = "${BUILD_NUMBER}"
  }

  stages {
    stage('Clean Workspace') {
      steps {
        cleanWs()
      }
    }

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build JAR') {
      steps {
        sh '''
        mvn clean package -DskipTests
        ls -lh target/
        jar tf target/app.jar | grep BOOT-INF
        '''
      }
    }

    stage('Push JAR to Nexus') {
      steps {
        sh 'mvn deploy -DskipTests'
      }
    }

    stage('Build Docker Image') {
      steps {
        sh '''
        docker build --no-cache -t $IMAGE:$VERSION .
        docker tag $IMAGE:$VERSION $IMAGE:latest
        '''
      }
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
          docker push $IMAGE:latest
          '''
        }
      }
    }
  }

  post {
    success {
      echo "Order service image pushed: $IMAGE:$VERSION"
    }
  }
}
