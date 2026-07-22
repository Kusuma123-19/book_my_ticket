pipeline {
    agent any

    tools {
        jdk 'JDK17'          // adjust to whatever JDK name you configured in Jenkins Global Tool Config
        maven 'Maven3'       // same — must match the name in Manage Jenkins > Tools
    }

    environment {
        // Pull sensitive values from Jenkins Credentials Store instead of hardcoding
        DB_USERNAME     = credentials('db-username')
        DB_PASSWORD     = credentials('db-password')
        MAIL_USERNAME   = credentials('mail-username')
        MAIL_PASSWORD   = credentials('mail-password')
        ADMIN_EMAIL     = credentials('admin-email')
        ADMIN_PASSWORD  = credentials('admin-password')
        CLOUDINARY_URL  = credentials('cloudinary-url')
    }

    stages {

        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/YourUsername/book-my-ticket.git'
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
