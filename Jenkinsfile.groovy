pipeline {
    agent any

    stages {
        stage('Temp:Test GitHub Access') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: 'master']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/ShroukElghoul/Jenkins_Repo.git',
                        credentialsId: 'Jenkins_user'
                    ]]
                ])
            }
        }
        stage('Checkout merge commit') {
            steps {
                script {
                    if (env.CHANGE_ID) {
                        // Build the test merge commit (PR + master)
                        checkout([
                            $class: 'GitSCM',
                            userRemoteConfigs: [[url: "https://github.com/USER/REPO.git"]],
                            branches: [[name: "refs/pull/${env.CHANGE_ID}/merge"]]
                        ])
                    } else {
                        // Normal master build
                        checkout scm
                    }
                }
            }
        }

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
