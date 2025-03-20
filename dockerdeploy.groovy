pipeline {
    agent any

    environment {
        REPO_URL = "https://github.com/namrqthakaipa/testapp.git"
        COMPOSE_FILE = "mongodb.yaml"
    }

    stages {
        stage('Clone or Pull Repository') {
            steps {
                script {
                    if (fileExists(".git")) {
                        echo "Repository already exists in Jenkins workspace. Pulling latest changes..."
                        sh "git pull origin main"
                    } else {
                        echo "Cloning repository into Jenkins workspace..."
                        sh "git clone $REPO_URL ."
                    }
                }
            }
        }

        stage('Extract YAML File and Start Containers') {
            steps {
                script {
                    echo "Starting Docker Compose services..."
                    sh "docker-compose -f $COMPOSE_FILE up -d"
                }
            }
        }
        
        
    stage('Verify Running Containers') {
    steps {
        script {
            echo "Verifying running containers..."

            def mongoStatus = sh(script: "docker ps --format '{{.Names}}' | grep -w 'mongo' > /dev/null && echo 'Running' || echo 'Not Running'", returnStdout: true).trim()
            def mongoExpressStatus = sh(script: "docker ps --format '{{.Names}}' | grep -w 'mongo-express' > /dev/null && echo 'Running' || echo 'Not Running'", returnStdout: true).trim()
            def nodeStatus = sh(script: "docker ps --format '{{.Names}}' | grep -w 'node-app' > /dev/null && echo 'Running' || echo 'Not Running'", returnStdout: true).trim()

            echo "-----------------------------"
            echo "Container Status:"
            echo "MongoDB: ${mongoStatus}"
            echo "Mongo Express: ${mongoExpressStatus}"
            echo "Node.js App: ${nodeStatus}"
            echo "-----------------------------"

            if (mongoStatus == 'Not Running' || nodeStatus == 'Not Running') {
                error("ERROR: One or more containers failed to start.")
            }
        }
    }
}

    }
}
