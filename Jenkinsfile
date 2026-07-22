pipeline {
    agent any

    tools {
        jdk 'JDK17'          // must match the name you configured in Manage Jenkins > Tools
        maven 'Maven3'       // must match the name you configured in Manage Jenkins > Tools
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

        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/Kusuma123-19/book_my_ticket.git'
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
