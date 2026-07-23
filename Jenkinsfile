pipeline {
    agent any

    tools {
        maven 'Maven3'   // Must match the Maven installation name configured in Jenkins Global Tool Configuration
        jdk 'JDK21'      // Must match the JDK installation name configured in Jenkins Global Tool Configuration
    }

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')   // Jenkins credential ID for Docker Hub
        DOCKER_IMAGE          = "kusumacr21/book-my-ticket"
        IMAGE_TAG             = "${env.BUILD_NUMBER}"
    }

    options {
        timestamps()
        skipDefaultCheckout(false)
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} -t ${DOCKER_IMAGE}:latest ."
            }
        }

        stage('Docker Login & Push') {
            steps {
                sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"
                sh "docker push ${DOCKER_IMAGE}:${IMAGE_TAG}"
                sh "docker push ${DOCKER_IMAGE}:latest"
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    docker compose down
                    docker compose up -d --build
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Build #${env.BUILD_NUMBER} deployed successfully."
        }
        failure {
            echo "❌ Build #${env.BUILD_NUMBER} failed. Check logs above."
        }
        always {
            sh 'docker logout'
            cleanWs()
        }
    }
}