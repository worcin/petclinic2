FROM tomcat:8.5
WORKDIR /opt/tomcat
ADD ./petclinic/target/petclinic.war /usr/local/tomcat/webapps/petclinic.war