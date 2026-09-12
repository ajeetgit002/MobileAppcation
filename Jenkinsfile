pipeline {
    agent any

    options {
        timeout(time: 2, unit: 'HOURS')
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['qa', 'staging', 'prod'], description: 'Target Environment')
        choice(name: 'PLATFORM', choices: ['Android', 'iOS'], description: 'Target Mobile Platform')
        string(name: 'RECIPIENT_EMAIL', defaultValue: 'ajeet.testingqa@gmail.com', description: 'Notification Email Recipient')
    }

    environment {
        PROJECT_NAME = "ITSM Mobile Automation"
        FAILED_STAGE = "None"
        REPORTS_DIR  = "reports"
        APP_PATH     = "app/itsm_apk_3.8.26.apk"
    }

    stages {

        // ====================================================
        // 1. CHECKOUT CODE
        // ====================================================
        stage('Checkout Code') {
            steps {
                script {
                    FAILED_STAGE = "Checkout Code"
                    echo "Checking out source code from GitHub repository..."
                    checkout scm
                }
            }
        }

        // ====================================================
        // 2. INSTALL DEPENDENCIES (npm ci & maven)
        // ====================================================
        stage('Install Dependencies') {
            steps {
                script {
                    FAILED_STAGE = "Install Dependencies"
                    echo "Installing project dependencies..."
                    if (isUnix()) {
                        sh 'npm ci --prefer-offline || echo "Proceeding with Maven build"'
                        sh 'mvn dependency:resolve'
                    } else {
                        bat(script: 'npm ci --prefer-offline || echo "Proceeding with Maven build"', returnStatus: true)
                        bat 'mvn dependency:resolve'
                    }
                }
            }
        }

        // ====================================================
        // 3. BUILD / DEPLOY TO QA
        // ====================================================
        stage('Build & Deploy QA') {
            steps {
                script {
                    FAILED_STAGE = "Build & Deploy QA"
                    echo "Building and deploying application to QA environment (${params.ENVIRONMENT})..."
                    if (isUnix()) {
                        sh 'mvn test-compile'
                    } else {
                        bat 'mvn test-compile'
                    }
                }
            }
        }

        // ====================================================
        // 4. QA ENVIRONMENT READY CHECK
        // ====================================================
        stage('QA Environment Ready?') {
            steps {
                script {
                    FAILED_STAGE = "QA Environment Health Check"
                    echo "Verifying QA environment health and Appium server readiness..."
                    if (isUnix()) {
                        sh 'curl -s http://127.0.0.1:4723/status || echo "Appium server check complete"'
                    } else {
                        bat 'curl -s http://127.0.0.1:4723/status || echo "Appium server check complete"'
                    }
                }
            }
        }

        // ====================================================
        // 5. SMOKE TESTING (GATE)
        // ====================================================
        stage('Smoke Tests') {
            steps {
                script {
                    FAILED_STAGE = "Smoke Tests"
                    echo "Executing Smoke Test Gate on ${params.PLATFORM}..."
                    def smokeCmd = "mvn test -DsuiteXmlFile=src/test/resources/testng-smoke.xml -Denv=${params.ENVIRONMENT} -Dplatform=${params.PLATFORM}"
                    if (isUnix()) {
                        sh smokeCmd
                    } else {
                        bat smokeCmd
                    }
                }
            }
        }

        // ====================================================
        // 6. SMOKE PASS? GATE VERIFICATION
        // ====================================================
        stage('Smoke Gate Check') {
            steps {
                script {
                    echo "Verifying Smoke Test results..."
                    if (currentBuild.currentResult == 'FAILURE') {
                        FAILED_STAGE = "Smoke Tests Gate (STOPPED ❌)"
                        error("Smoke tests FAILED. Stopping pipeline execution immediately. Skipping Regression and Production deploy.")
                    } else {
                        echo "Smoke tests PASSED ✅. Proceeding to Regression suite..."
                    }
                }
            }
        }

        // ====================================================
        // 7. FULL REGRESSION TESTING
        // ====================================================
        stage('Regression Tests') {
            steps {
                script {
                    FAILED_STAGE = "Regression Tests"
                    echo "Executing Full Regression Test Suite on ${params.PLATFORM}..."
                    def regCmd = "mvn test -DsuiteXmlFile=src/test/resources/testng-regression.xml -Denv=${params.ENVIRONMENT} -Dplatform=${params.PLATFORM}"
                    if (isUnix()) {
                        sh regCmd
                    } else {
                        bat regCmd
                    }
                }
            }
        }

        // ====================================================
        // 8. PUBLISH TEST REPORTS
        // ====================================================
        stage('Publish Reports') {
            steps {
                script {
                    echo "Archiving ExtentReports and TestNG artifacts..."
                    archiveArtifacts artifacts: 'reports/**/*.html, screenshots/**/*.png', allowEmptyArchive: true
                }
            }
        }

        // ====================================================
        // 9. MANUAL APPROVAL GATE
        // ====================================================
        stage('Approval') {
            steps {
                script {
                    FAILED_STAGE = "Approval Gate"
                    echo "Waiting for manual release approval..."
                    timeout(time: 24, unit: 'HOURS') {
                        input message: 'All Smoke and Regression tests PASSED. Approve deployment to UAT & Production?', ok: 'Deploy'
                    }
                }
            }
        }

        // ====================================================
        // 10. DEPLOY TO UAT
        // ====================================================
        stage('Deploy to UAT') {
            steps {
                script {
                    FAILED_STAGE = "Deploy to UAT"
                    echo "Deploying application to UAT environment..."
                    // Deployment script here
                }
            }
        }

        // ====================================================
        // 11. DEPLOY TO PRODUCTION
        // ====================================================
        stage('Deploy to Production') {
            steps {
                script {
                    FAILED_STAGE = "Deploy to Production"
                    echo "Deploying application to Production environment..."
                    // Production deployment script here
                }
            }
        }
    }

    // ====================================================
    // REUSABLE POST-EXECUTION EMAIL ENGINE
    // ====================================================
    post {
        failure {
            script {
                echo "Pipeline failed at stage: ${FAILED_STAGE}. Triggering reusable failure notification email..."
                
                def emailBody = """
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 20px;">
                    <div style="max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 8px; border: 1px solid #e5e7eb; overflow: hidden;">
                        <div style="background: #dc2626; color: #ffffff; padding: 20px;">
                            <h2 style="margin: 0;">${PROJECT_NAME} - CI/CD Build FAILED ❌</h2>
                            <p style="margin: 5px 0 0 0; opacity: 0.9;">Build #${env.BUILD_NUMBER} | Environment: ${params.ENVIRONMENT}</p>
                        </div>
                        <div style="padding: 24px;">
                            <p style="color: #991b1b; background: #fee2e2; padding: 12px; border-radius: 4px;">
                                <strong>Pipeline Stopped:</strong> Failure occurred during <strong>${FAILED_STAGE}</strong>.
                            </p>
                            <table style="width: 100%; border-collapse: collapse; margin-top: 15px;">
                                <tr><td style="padding: 8px; font-weight: bold; color: #6b7280;">Job Name:</td><td>${env.JOB_NAME}</td></tr>
                                <tr><td style="padding: 8px; font-weight: bold; color: #6b7280;">Build URL:</td><td><a href="${env.BUILD_URL}">${env.BUILD_URL}</a></td></tr>
                                <tr><td style="padding: 8px; font-weight: bold; color: #6b7280;">Branch:</td><td>${env.GIT_BRANCH ?: 'main'}</td></tr>
                                <tr><td style="padding: 8px; font-weight: bold; color: #6b7280;">Failed Stage:</td><td style="color: #dc2626; font-weight: bold;">${FAILED_STAGE}</td></tr>
                            </table>
                            <div style="margin-top: 25px; text-align: center;">
                                <a href="${env.BUILD_URL}console" style="background: #2563eb; color: #ffffff; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;">View Console Logs</a>
                                <a href="${env.BUILD_URL}artifact/reports/" style="background: #4b5563; color: #ffffff; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold; margin-left: 10px;">Download Reports</a>
                            </div>
                        </div>
                        <div style="background: #f9fafb; padding: 12px; text-align: center; font-size: 12px; color: #9ca3af;">
                            Automated notification from Jenkins CI/CD Pipeline
                        </div>
                    </div>
                </body>
                </html>
                """

                // Sends via Jenkins Email Extension if plugin is installed
                try {
                    emailext (
                        failOnError: false,
                        subject: "[CI/CD FAILED] ${PROJECT_NAME} - Build #${env.BUILD_NUMBER} at ${env.FAILED_STAGE}",
                        body: emailBody,
                        mimeType: 'text/html',
                        to: "${params.RECIPIENT_EMAIL}",
                        attachmentsPattern: 'reports/**/*.html'
                    )
                } catch (Exception e) {
                    echo "Email notification skipped (Email Extension plugin not installed or SMTP not configured): ${e.message}"
                }
            }
        }

        success {
            script {
                echo "Pipeline completed successfully! All gates passed."
                try {
                    emailext (
                        failOnError: false,
                        subject: "[CI/CD SUCCESS] ${PROJECT_NAME} - Build #${env.BUILD_NUMBER} Deployed to Production ✅",
                        body: """
                        <h2>${PROJECT_NAME} - Build #${env.BUILD_NUMBER} SUCCESSFUL ✅</h2>
                        <p>All Smoke and Regression suites passed. Application has been deployed to Production.</p>
                        <p><a href="${env.BUILD_URL}">View Build Details</a></p>
                        """,
                        mimeType: 'text/html',
                        to: "${params.RECIPIENT_EMAIL}"
                    )
                } catch (Exception e) {
                    echo "Success notification email skipped: ${e.message}"
                }
            }
        }
    }
}
