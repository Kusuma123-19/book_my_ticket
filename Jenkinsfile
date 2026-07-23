pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
        DOCKER_IMAGE          = "kusumacr21/book-my-ticket"
        IMAGE_TAG             = "${env.BUILD_NUMBER}"
        DB_USERNAME     = 'admin'
        DB_PASSWORD     = 'admin@123'
        MAIL_USERNAME   = 'kusumacr19@gmail.com'
        MAIL_PASSWORD   = 'geylgfuvsnnrhwsj'
        ADMIN_EMAIL     = 'admin@gmail.com'
        ADMIN_PASSWORD  = 'admin'
        CLOUDINARY_URL  = 'cloudinary://123456789012345:dummy_secret@dummy_cloud'
    }

    options {
        timestamps()
        skipDefaultCheckout(false)
        disableConcurrentBuilds()
    }

    environment {
    DB_USERNAME     = 'admin'
    DB_PASSWORD     = 'admin@123'
    MAIL_USERNAME   = 'kusumacr19@gmail.com'
    MAIL_PASSWORD   = 'geylgfuvsnnrhwsj'
    ADMIN_EMAIL     = 'admin@gmail.com'
    ADMIN_PASSWORD  = 'admin'
    CLOUDINARY_URL  = 'cloudinary://123456789012345:dummy_secret@dummy_cloud'
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
            when {
                expression { env.DOCKERHUB_CREDENTIALS }
            }
            steps {
                sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"
                sh "docker push ${DOCKER_IMAGE}:${IMAGE_TAG}"
                sh "docker push ${DOCKER_IMAGE}:latest"
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    pkill -f book-my-ticket.jar || true
                    nohup java -jar target/book-my-ticket-0.0.1-SNAPSHOT.jar \
                        --spring.datasource.username=$DB_USERNAME \
                        --spring.datasource.password=$DB_PASSWORD \
                        --spring.mail.username=$MAIL_USERNAME \
                        --spring.mail.password=$MAIL_PASSWORD \
                        --admin.email=$ADMIN_EMAIL \
                        --admin.password=$ADMIN_PASSWORD \
                        --cloudinary.url=$CLOUDINARY_URL \
                        > app.log 2>&1 &
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
            sh 'docker logout || true'
            cleanWs()
        }
        }

        stage('Deploy') {
            steps {
                sh '''
                    pkill -f book-my-ticket.jar || true
                    nohup java -jar target/book-my-ticket-0.0.1-SNAPSHOT.jar \
                        --spring.datasource.username=$DB_USERNAME \
                        --spring.datasource.password=$DB_PASSWORD \
                        --spring.mail.username=$MAIL_USERNAME \
                        --spring.mail.password=$MAIL_PASSWORD \
                        --admin.email=$ADMIN_EMAIL \
                        --admin.password=$ADMIN_PASSWORD \
                        --cloudinary.url=$CLOUDINARY_URL \
                        > app.log 2>&1 &
                '''
            }
        }

        stage('Done') {
            steps {
                echo 'Deployment completed successfully.'
            }
        }
    }
}