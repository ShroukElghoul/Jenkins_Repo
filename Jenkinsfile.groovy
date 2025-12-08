pipeline {
    agent any

    stages {
        stage('Prepare Build Directory') {
            steps {
                // Create 'build' directory
                script {
                    if (!fileExists('build')) {
                        bat 'mkdir build'
                    }
                }
            }
        }

        stage('Configure with CMake') {
            steps {
                dir('build') {
                    // Run CMake to generate Makefiles for MinGW
                    bat 'cmake .. -G "MinGW Makefiles"'
                }
            }
        }

        stage('Build') {
            steps {
                dir('build') {
                    // Build the project
                    bat 'cmake --build .'
                }
            }
        }

        stage('Run Executable') {
            steps {
                dir('build') {
                    // Run demo.exe
                    bat 'demo.exe'
                }
            }
        }
    }

    post {
        always {
            echo 'Build finished!'
        }
    }
}
