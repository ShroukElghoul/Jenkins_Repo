pipeline {
    agent any

    stages {
        stage('Checkout PR merge commit') {
            steps {
                script {
                    if (env.CHANGE_ID) {
                        // Build the PR merge commit
                        checkout([
                            $class: 'GitSCM',
                            branches: [[name: "refs/remotes/origin/pr/${env.CHANGE_ID}"]],
                            userRemoteConfigs: [[
                                url: 'https://github.com/ShroukElghoul/Jenkins_Repo.git',
                                credentialsId: 'github-creds'
                            ]]
                        ])
                    } else {
                        // Build master normally
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
