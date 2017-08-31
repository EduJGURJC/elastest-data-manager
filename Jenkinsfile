node('docker'){
    stage "Container Prep"
        echo("the node is up")
        sh 'echo 262144 | sudo tee /proc/sys/vm/max_map_count'
        //sysctl -w vm.max_map_count=262144
        //sysctl vm.max_map_count
        def mycontainer = docker.image('sgioldasis/ci-docker-in-docker:latest')
        // def mycontainer = docker.image('elastest/ci-docker-compose-siblings')
        //def mycontainer = docker.image('elastest/docker-siblings:latest')
        mycontainer.pull() // make sure we have the latest available from Docker Hub
        mycontainer.inside("-u jenkins -v /var/run/docker.sock:/var/run/docker.sock:rw") {
            git 'https://github.com/elastest/elastest-data-manager.git'

            stage "Building Java API"
                echo ("Starting to build Java API...")
                sh 'chmod +x bin/* && bin/run-build-test-java.sh'
                sh 'pwd'
                // step([$class: 'JUnitResultArchiver', testResults: '**/rest-java/rest_api_project/edm-rest/target/surefire-reports/*.xml'])
                step([$class: 'JUnitResultArchiver', testResults: '**/rest-java/rest_api_project/edm-rest/target/site/cobertura/coverage.xml'])

                stage "Cobertura"
                    //sh 'bin/run-tests.sh'
                    sh('cd rest-java/rest_api_project/edm-rest && git rev-parse HEAD > GIT_COMMIT')
                        git_commit=readFile('rest-java/rest_api_project/edm-rest/GIT_COMMIT')

                    sh 'export GIT_COMMIT=$git_commit'

                    sh 'export GIT_BRANCH=master'
                    def codecovArgs = "-v -t $COB_EDM_TOKEN"

                    echo "$codecovArgs"

                    def exitCode = sh(
                        returnStatus: true,
                        script: "curl -s https://codecov.io/bash | bash -s - $codecovArgs")
                        //script: "curl -s https://raw.githubusercontent.com/codecov/codecov-bash/master/codecov | bash -s - $codecovArgs")
                        //script: " pip install --user codecov && codecov -v -t $COB_EDM_TOKEN")
                        if (exitCode != 0) {
                            echo( exitCode +': Failed to upload code coverage to codecov')
                        }

            stage "Build Rest API image - Package"
                echo ("building..")
                def rest_api_image = docker.build("elastest/edm:0.1","./rest")

            stage "Build Alluxio image - Package"
                echo ("building..")
                sh 'chmod +x alluxio/entrypoint.sh'
                def alluxio_image = docker.build("elastest/edm-alluxio:0.1","./alluxio")

            stage "Build Hadoop image - Package"
                echo ("building..")
                def hadoop_image = docker.build("elastest/edm-hadoop:0.1","./hadoop")

            stage "Build Elasticsearch image - Package"
                echo ("building..")
                def elasticsearch_image = docker.build("elastest/edm-elasticsearch:0.1","./elasticsearch")

            stage "Build Kibana image - Package"
                echo ("building..")
                def kibana_image = docker.build("elastest/edm-kibana:0.1","./kibana")

            stage "Build Cerebro image - Package"
                echo ("building..")
                def cerebro_image = docker.build("elastest/edm-cerebro:0.1","./cerebro")

            // stage "Build MySQL image - Package"
            //    echo ("building..")
            //    def mysql_image = docker.build("elastest/edm-mysql:0.1","./mysql")

            stage "Run EDM docker-compose"
                sh 'chmod +x bin/* && bin/teardown-ci.sh && bin/startup-ci.sh'
                echo ("EDM System is running..")

            // stage "Unit tests"
            //     echo ("Starting unit tests...")
            //     sh 'bin/run-tests.sh'
            //     step([$class: 'JUnitResultArchiver', testResults: '**/rest/rest_api_project/nosetests.xml'])

            // stage "Cobertura"
            //     //sh 'bin/run-tests.sh'
            //     sh('cd rest/rest_api_project && git rev-parse HEAD > GIT_COMMIT')
            //         git_commit=readFile('rest/rest_api_project/GIT_COMMIT')
            //
            //     sh 'export GIT_COMMIT=$git_commit'
            //
            //     sh 'export GIT_BRANCH=master'
            //     def codecovArgs = "-v -t $COB_EDM_TOKEN"
            //
            //     echo "$codecovArgs"
            //
            //     def exitCode = sh(
            //         returnStatus: true,
            //         script: "curl -s https://codecov.io/bash | bash -s - $codecovArgs")
            //         //script: "curl -s https://raw.githubusercontent.com/codecov/codecov-bash/master/codecov | bash -s - $codecovArgs")
            //         //script: " pip install --user codecov && codecov -v -t $COB_EDM_TOKEN")
            //         if (exitCode != 0) {
            //             echo( exitCode +': Failed to upload code coverage to codecov')
            //         }
            //
            stage "publish"
                echo ("publishing..")
                withCredentials([[
                    $class: 'UsernamePasswordMultiBinding',
                    credentialsId: 'elastestci-dockerhub',
                    usernameVariable: 'USERNAME',
                    passwordVariable: 'PASSWORD']]) {
                        sh 'docker login -u "$USERNAME" -p "$PASSWORD"'
                        //here your code
                        rest_api_image.push()
                        alluxio_image.push()
                        hadoop_image.push()
                        elasticsearch_image.push()
                        kibana_image.push()
                        cerebro_image.push()
                        // mysql_image.push()
                    }

        }
}
