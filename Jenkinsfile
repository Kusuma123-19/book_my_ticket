pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
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
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh './mvnw test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
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

        stage('Clean Up') {
            steps {
                sh "docker rmi ${DOCKER_IMAGE}:${IMAGE_TAG} ${DOCKER_IMAGE}:latest || true"
                sh 'docker system prune -f || true'
            }
        }
    }

    post {
        success {
            echo "Build succeeded — image pushed: ${DOCKER_IMAGE}:${IMAGE_TAG}"
        }
        failure {
            echo "Build failed"
        }
        always {
            sh 'docker logout'
            cleanWs()
        }
    }
}