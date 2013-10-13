## Overview
The aim of this Readme file is to describe the SelfLet project from a practical point of view.
If you are looking for higher level information (i.e., purpose, architecture, case studies, please refer to the "Paper" section of this file).

This file contains information about the following aspects:

- Installation
- Deploy
- Development

## Installation
Before starting with the installation process please make sure you meet the following requirements:

### Requirements
- Java ( >=1.5)
- Maven (>= 3.0)
- Git

### Installation instructions
The SelfLet project is structured in different subprojects distributed as Maven packages.
Each (sub)project has its own github repository as describe below:

- Selflet (this repository) (https://github.com/ncalca/selflet)  
- SelfletCommon (https://github.com/ncalca/selflet-common)
- RedsBroker (https://github.com/ncalca/reds-broker)
- REDs middleware (https://github.com/ncalca/reds)
- Request dispatcher (https://github.com/ncalca/selflet-request-dispatcher)

The first step is to clone the previously listed git repositories in a common folder. 
One way to do this using Eclipse IDE is to:

1. Checkout the git repository locally
2. Import project from local git repository as a `General Project`
3. Convert the project to a Maven project (Configure -> Convert to Maven project)
4. Compile and install the projects. This can be done through the command line using the ```mvn``` executable (```mvn install```).

Make sure you don't get any errors at the end of this procedure (Maven will download the necessary dependencies automatically and place the Jars in its own local repository).

## Deploy
You can run a Selflet system in your local machine or in a distributed setting (i.e., Amazon EC2 VMs).
These are the steps to run a simple example SelfLet-based system already shipped with the code:

1. Run the `reds-broker` using the `exec:java` goal of the reds-broker/pom.xml file (```mvn exec:java```). This will run the publish/subscribe broker to which the SelfLets and the request dispatcher will connect. The default port is 8000.

2. Lauch the SelfLet by using the `exec:java` goal fo the selflet-pom.xml. 
In this case we also need to pass three additional parameters specify the: 1) name of the selflet project to run, 2) the selflet unique numeric identifier and the broker address to which the SelfLet will connect. The final expression (using an example SelfLet is): ```mvn exec:java -Dexec.args="src/main/resources/selflets/selflet1 -i 1 -b 127.0.0.1:8000"```

3. Launch the request dispatcher as a Jetty HTTP server. In order to do this you need to execute the ```jetty:run``` maven command.
4. Launch the workload generator using JMeter. The request track is stored in ```selflet-request-dispatcher/src/main/resources/jmeter_track.jmx``` and in order to be run JMeter needs the so called "JMeter Plugins" that can be found at http://jmeter-plugins.org/.


The optimization model can be found in ```selflet-request-dispatcher/src/main/resources/optimization_model```.

**Important**: Before launching the dispatcher it is necessary to set the Amazon Credentials in the ```selflet-request-dispacther/src/main/resources/AwsCredentials.properties``` file. The content of the file has the following format:
```
accessKey=SECRET
secretKey=SECRET
```
where ```SECRET``` is substituted with your AWS credentials.

## Papers
1. [Incorporating prediction models in the SelfLet framework: a plugin approach](http://arxiv.org/pdf/1005.2299)
2. [The emergence of load balancing in distributed systems: the selflet approach](http://home.deib.polimi.it/calcavecchia/publications/runtime_models2010.pdf)
3. [Complex Autonomic Systems for Networked Enterprises: Mechanisms, Solutions and Design Approaches](http://home.deib.polimi.it/dubois/papers/artdeco12.pdf)

## Development
*TODO*

## License

SelfLet is released under the [MIT License](http://www.opensource.org/licenses/MIT).
