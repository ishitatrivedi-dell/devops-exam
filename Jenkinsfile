pipeline {
    agent any

    environment {
        // Define environment variables
        MAVEN_OPTS = '-Xmx1024m'
        APP_NAME = 'crud-app'
        VERSION = "1.0.0-${BUILD_NUMBER}"
    }

   tools {
    maven 'mvn-3'
    jdk 'jdk-21'
}

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code from repository...'
                // Checkout from GitHub repository
                checkout scm
                
                // Display information about the build
                echo "Building ${env.APP_NAME} version ${VERSION}"
                echo "Branch: ${env.BRANCH_NAME}"
                echo "Build Number: ${env.BUILD_NUMBER}"
            }
        }

        stage('Build') {
            steps {
                echo 'Building the application...'
                // Clean and compile the project
                sh 'mvn clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                echo 'Running unit tests...'
                // Run all tests
                sh 'mvn test'
            }
            post {
                always {
                    // Publish test results
                    junit '**/target/surefire-reports/*.xml'
                    // Publish test coverage report
                    jacoco execPattern: 'target/jacoco.exec'
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging the application...'
                // Package the application (skip tests as they already ran)
                sh 'mvn package -DskipTests'
                
                // Verify the JAR file was created
                sh 'ls -la target/*.jar'
            }
        }

        stage('Code Quality Analysis') {
            steps {
                echo 'Running code quality checks...'
                // Run Checkstyle or SpotBugs if configured
                // sh 'mvn checkstyle:check'
                
                // Archive test results and coverage
                archiveArtifacts artifacts: 'target/surefire-reports/*.xml', fingerprint: true
            }
        }

        stage('Build Docker Image') {
            when {
                branch 'main'
            }
            steps {
                echo 'Building Docker image...'
                script {
                    // Build Docker image
                    docker.build("${env.APP_NAME}:${VERSION}")
                    
                    // Tag with latest
                    docker.build("${env.APP_NAME}:latest")
                }
            }
        }

        stage('Push to Registry') {
            when {
                branch 'main'
            }
            steps {
                echo 'Pushing Docker image to registry...'
                script {
                    // Configure Docker registry credentials (add credentials in Jenkins)
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                        def image = docker.build("${env.APP_NAME}:${VERSION}")
                        image.push()
                        image.push('latest')
                    }
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo 'Archiving build artifacts...'
                // Archive the JAR file
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                
                // Archive the Dockerfile and other config files
                archiveArtifacts artifacts: 'Dockerfile', allowEmptyArchive: true
                archiveArtifacts artifacts: 'Jenkinsfile', allowEmptyArchive: true
            }
        }

        stage('Deploy to Dev') {
            when {
                branch 'develop'
            }
            steps {
                echo 'Deploying to development environment...'
                // Add deployment steps here
                // Example: Deploy to Kubernetes or Docker Compose
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                echo 'Deploying to production environment...'
                // Add production deployment steps
                // Requires manual approval in production
                input message: 'Deploy to Production?', ok: 'Deploy'
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed'
            // Clean up workspace
            cleanWs()
        }
        success {
            echo 'Build succeeded!'
            // Send success notification
            // slackSend(channel: '#builds', message: "Build succeeded: ${env.APP_NAME} - ${VERSION}")
        }
        failure {
            echo 'Build failed!'
            // Send failure notification
            // slackSend(channel: '#builds', message: "Build failed: ${env.APP_NAME} - ${VERSION}")
        }
        unstable {
            echo 'Build is unstable'
        }
    }
}
