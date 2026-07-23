pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }

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
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
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

        stage('Deploy') {
            steps {
                sh '''
                    docker-compose down
                    docker-compose up -d --build
                '''
            }
        }
    }

    post {
        success {
            echo "Build succeeded"
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